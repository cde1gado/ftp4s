package org.pure4s.ftp4s

import cats.data._
import cats.effect._
import cats.implicits._
import org.apache.commons.net.ftp.FTPClient
import org.pure4s.ftp4s.model._

class FFTPApache[F[_]: Sync] extends FFTP[F] {

  type FTPCli = FTPClient

  override def init: FTPConn[New, FTPCli] = FTPConn[New, FTPCli](new FTPClient())

  override def isConnected(): StateT[F, FTPConn[Open, FTPCli], Boolean] =
    StateT.inspectF { conn =>
      Sync[F].delay(conn.ftpClient.isConnected())
    }

  override def login(username: String, password: String): IndexedStateT[F, FTPConn[Open, FTPCli], FTPConn[Auth, FTPCli], Unit] =
    IndexedStateT.modifyF { conn =>
      for {
        isLogged <- Sync[F].delay(conn.ftpClient.login(username, password))
        authConn <- if (!isLogged) Sync[F].raiseError(new RuntimeException("LoginError"))
                    else Sync[F].pure(FTPConn[Auth, FTPCli](conn.ftpClient))
      } yield authConn
    }

  override def withFTP[R](config: FTPConfig)(init: FTPConn[New, FTPCli])(f: FTPConn[Open, FTPCli] => F[R]): F[R] =
    Sync[F].bracket(connect(config.host, config.port)
      .runS(init))(f)(disconnect().runA(_))

  private def connect(host: String, port: Int): IndexedStateT[F, FTPConn[New, FTPCli], FTPConn[Open, FTPCli], Unit] =
    IndexedStateT.modifyF { conn =>
      Sync[F].delay(conn.ftpClient.connect(host, port)) *>
        Sync[F].pure(FTPConn[Open, FTPCli](conn.ftpClient))
    }

  private def disconnect(): IndexedStateT[F, FTPConn[Open, FTPCli], FTPConn[Close, FTPCli], Unit] =
    IndexedStateT.modifyF { conn =>
      Sync[F].delay(conn.ftpClient.disconnect()) *>
        Sync[F].pure(FTPConn[Close, FTPCli](conn.ftpClient))
    }
}
