package org.pure4s.ftp4s

import org.pure4s.ftp4s.model.FTPConfig

trait Fixtures {

  val config = FTPConfig("localhost", 9999, "user", "pass", "/")

  case class Fake()

  def ftpFake(yesOrNo: Boolean = false, maybeError: Option[Throwable] = None): FTP[Fake] = new FTP[Fake] {

    override val instance: Fake = Fake()

    override def connect(host: String, port: Int): Unit = errorOrUnit(maybeError)

    override def disconnect(): Unit = errorOrUnit(maybeError)

    override def isConnected(): Boolean = maybeError.fold(yesOrNo)(error => throw error)

    override def login(user: String, pass: String): Boolean = yesOrNo

    private def errorOrUnit(maybeError: Option[Throwable]) =
      maybeError match {
        case Some(error) => throw error
        case None => ()
      }
  }

}
