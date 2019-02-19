package org.pure4s.ftp4s

import cats.data.{IndexedStateT, StateT}
import org.pure4s.ftp4s.model._

trait FFTP[F[_]] {

  type FTPCli

  def init: FTPConn[New, FTPCli]

  def isConnected(): StateT[F, FTPConn[Open, FTPCli], Boolean]

  def login(user: String, pass: String): IndexedStateT[F, FTPConn[Open, FTPCli], FTPConn[Auth, FTPCli], Unit]

  def withFTP[R](config: FTPConfig)(init: FTPConn[New, FTPCli])(f: FTPConn[Open, FTPCli] => F[R]): F[R]
}

object FFTP {

  def apply[F[_]](implicit ev: FFTP[F]): FFTP[F] = ev
}
