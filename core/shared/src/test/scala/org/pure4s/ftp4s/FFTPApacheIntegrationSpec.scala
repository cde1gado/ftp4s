package org.pure4s.ftp4s

import cats.effect.IO
import implicits._
import org.scalatest._

class FFTPApacheIntegrationSpec extends FunSpec with BeforeAndAfter with Matchers with Fixtures {

  val fftp = FFTP[IO]

  before {
    FakeFTPServerManager.start(config, List(("/file1.txt", "hello!")))
  }

  after {
    FakeFTPServerManager.stop()
  }

  describe("ApacheFTP") {

    it ("should return true when isConnected() if connect() is successful") {
      val fResult =
        fftp.withFTP(config)(fftp.init) { conn =>
          (for {
            isConnected <- fftp.isConnected()
          } yield isConnected).runA(conn)
        }

      fResult.unsafeRunSync() shouldBe true
    }
  }
}
