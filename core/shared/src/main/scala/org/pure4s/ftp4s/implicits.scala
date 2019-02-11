package org.pure4s.ftp4s

import cats.effect.Sync

object implicits {

  implicit def fftpApache[F[_]: Sync] = new FFTPApache[F]
}
