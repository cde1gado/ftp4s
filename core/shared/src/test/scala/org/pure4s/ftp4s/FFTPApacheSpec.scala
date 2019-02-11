package org.pure4s.ftp4s

import cats.effect.IO
import org.apache.commons.net.ftp.FTPClient
import org.mockito.Mockito._
import org.pure4s.ftp4s.model._
import org.scalatest._
import org.scalatest.mockito.MockitoSugar

class FFTPApacheSpec extends FunSpec with Matchers with MockitoSugar with Fixtures {

  val ftp: FTPClient = mock[FTPClient]

  val newFTPConn = FTPConn[New, FTPClient](ftp)
  val openFTPConn= FTPConn[Open, FTPClient](ftp)
  val authFTPConn = FTPConn[Auth, FTPClient](ftp)
  val closeFTPConn = FTPConn[Close, FTPClient](ftp)

  val apacheFFTP = new FFTPApache[IO] {
    override def init: FTPConn[New, FTPCli] = FTPConn[New, FTPCli](ftp)
  }

  describe("isConnected()") {

    it ("should return true and keep same Open FtpState if it is successful") {
      when(ftp.isConnected).thenReturn(true)

      val fResult = apacheFFTP.isConnected

      fResult.runA(openFTPConn).unsafeRunSync() shouldBe true
      fResult.runS(openFTPConn).unsafeRunSync() shouldBe openFTPConn
    }

    it ("should return false and keep same Open FtpState if it is successful") {
      when(ftp.isConnected).thenReturn(false)

      val fResult = apacheFFTP.isConnected

      fResult.runA(openFTPConn).unsafeRunSync() shouldBe false
      fResult.runS(openFTPConn).unsafeRunSync() shouldBe openFTPConn
    }

    it ("should fail if it fails") {
      when(ftp.isConnected).thenThrow(new RuntimeException("test!"))

      val fResult = apacheFFTP.isConnected

      assertThrows[RuntimeException](fResult.run(openFTPConn).unsafeRunSync())
    }
  }

  describe("login()") {

    it ("should change Open FtpState to Auth if login is successful") {
      when(ftp.login(config.user, config.host)).thenReturn(true)

      val fResult = apacheFFTP.login(config.user, config.host)

      fResult.runS(openFTPConn).unsafeRunSync() shouldBe authFTPConn
    }

    it ("should fail if login fails") {
      when(ftp.login(config.user, config.host)).thenReturn(false)

      val fResult = apacheFFTP.login(config.user, config.host)

      assertThrows[RuntimeException](fResult.runS(openFTPConn).unsafeRunSync())
    }
  }
}
