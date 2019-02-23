package org.pure4s.ftp4s

import cats.effect.IO
import org.pure4s.ftp4s.model._
import org.scalatest._

class FFTPApacheSpec extends FunSpec with Matchers with Fixtures {

  describe("isConnected()") {

    it ("should return true and keep same Open FtpState if it is successful") {
      implicit val ftp: FTP[Fake] = ftpFake(yesOrNo = true)
      val fftp = FFTP[IO, Fake]
      val openFTPConn = FTPConn[Open, FTP[Fake]](ftp)

      val fResult = fftp.isConnected()

      fResult.runA(openFTPConn).unsafeRunSync() shouldBe true
      fResult.runS(openFTPConn).unsafeRunSync() shouldBe openFTPConn
    }

    it ("should return false and keep same Open FtpState if it is successful") {
      implicit val ftp: FTP[Fake] = ftpFake()
      val fftp = FFTP[IO, Fake]
      val openFTPConn = FTPConn[Open, FTP[Fake]](ftp)

      val fResult = fftp.isConnected()

      fResult.runA(openFTPConn).unsafeRunSync() shouldBe false
      fResult.runS(openFTPConn).unsafeRunSync() shouldBe openFTPConn
    }

    it ("should fail if it fails") {
      implicit val ftp: FTP[Fake] = ftpFake(yesOrNo = false, Some(new RuntimeException("test!")))
      val fftp = FFTP[IO, Fake]
      val openFTPConn = FTPConn[Open, FTP[Fake]](ftp)

      val fResult = fftp.isConnected()

      assertThrows[RuntimeException](fResult.run(openFTPConn).unsafeRunSync())
    }
  }

  describe("login()") {

    it ("should change Open FtpState to Auth if login is successful") {
      implicit val ftp: FTP[Fake] = ftpFake(yesOrNo = true)
      val fftp = FFTP[IO, Fake]
      val openFTPConn = FTPConn[Open, FTP[Fake]](ftp)
      val authFTPConn = FTPConn[Auth, FTP[Fake]](ftp)

      val fResult = fftp.login(config.user, config.host)

      fResult.runS(openFTPConn).unsafeRunSync() shouldBe authFTPConn
    }

    it ("should fail if login fails") {
      implicit val ftp: FTP[Fake] = ftpFake(yesOrNo = false, Some(new RuntimeException("test!")))
      val fftp = FFTP[IO, Fake]
      val openFTPConn = FTPConn[Open, FTP[Fake]](ftp)

      val fResult = fftp.login(config.user, config.host)

      assertThrows[RuntimeException](fResult.runS(openFTPConn).unsafeRunSync())
    }
  }
}
