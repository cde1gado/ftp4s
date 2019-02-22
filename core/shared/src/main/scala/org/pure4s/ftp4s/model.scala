package org.pure4s.ftp4s

object model {

  case class FTPConn[S <: FTPState, A](ftp: A)

  sealed trait FTPState
  trait New extends FTPState
  trait Open extends FTPState
  trait Auth extends Open
  trait Close extends FTPState

  case class FTPConfig(host: String, port: Int, user: String, pass: String, path: String)
}
