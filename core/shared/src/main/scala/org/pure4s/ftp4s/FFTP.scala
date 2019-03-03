package org.pure4s.ftp4s

import cats.data.{IndexedStateT, StateT}
import cats.effect.Sync
import cats.implicits._
import org.pure4s.ftp4s.model._

trait FFTP[F[_], FTPCli] {

  def isConnected(): StateT[F, FTPConn[Open, FTP[FTPCli]], Boolean]

  def login(user: String, pass: String): IndexedStateT[F, FTPConn[Open, FTP[FTPCli]], FTPConn[Auth, FTP[FTPCli]], Unit]

  def withFTP[R](config: FTPConfig)(f: FTPConn[Open, FTP[FTPCli]] => F[R])(implicit ftp: FTP[FTPCli]): F[R]
}

object FFTP {

  def apply[F[_], FTPCli](implicit ev: FFTP[F, FTPCli]): FFTP[F, FTPCli] = ev

  implicit def sync[F[_]: Sync, FTPCli](implicit ftp: FTP[FTPCli]): FFTP[F, FTPCli] = new FFTP[F, FTPCli] {

    override def isConnected(): StateT[F, FTPConn[Open, FTP[FTPCli]], Boolean] =
      StateT.inspectF { conn =>
        Sync[F].delay(conn.ftp.isConnected())
      }

    override def login(user: String, pass: String): IndexedStateT[F, FTPConn[Open, FTP[FTPCli]], FTPConn[Auth, FTP[FTPCli]], Unit] =
      IndexedStateT.modifyF { conn =>
        for {
          isLogged <- Sync[F].delay(conn.ftp.login(user, pass))
          authConn <- if (!isLogged) Sync[F].raiseError(new RuntimeException("LoginError"))
                      else Sync[F].pure(FTPConn[Auth, FTP[FTPCli]](conn.ftp))
        } yield authConn
      }

    override def withFTP[R](config: FTPConfig)(f: FTPConn[Open, FTP[FTPCli]] => F[R])(implicit ftp: FTP[FTPCli]): F[R] =
      Sync[F].bracket(connect(config.host, config.port)
        .runS(create))(f)(disconnect().runA(_))

    private def create(implicit ftp: FTP[FTPCli]): FTPConn[New, FTP[FTPCli]] = FTPConn[New, FTP[FTPCli]](ftp)

    private def connect(host: String, port: Int): IndexedStateT[F, FTPConn[New, FTP[FTPCli]], FTPConn[Open, FTP[FTPCli]], Unit] =
      IndexedStateT.modifyF { conn =>
        Sync[F].delay(conn.ftp.connect(host, port)) *>
          Sync[F].pure(FTPConn[Open, FTP[FTPCli]](conn.ftp))
      }

    private def disconnect(): IndexedStateT[F, FTPConn[Open, FTP[FTPCli]], FTPConn[Close, FTP[FTPCli]], Unit] =
      IndexedStateT.modifyF { conn =>
        Sync[F].delay(conn.ftp.disconnect()) *>
          Sync[F].pure(FTPConn[Close, FTP[FTPCli]](conn.ftp))
      }
  }
}
