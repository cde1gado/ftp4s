package org.pure4s.ftp4s

import org.pure4s.ftp4s.model.FTPConfig

trait Fixtures {

  val config = FTPConfig("localhost", 9999, "user", "pass", "/")
}
