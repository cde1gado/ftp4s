package org.pure4s.ftp4s

import org.apache.commons.net.ftp.FTPClient

trait FTP[A] {

  val instance: A

  def connect(host: String, port: Int): Unit

  def disconnect(): Unit

  def isConnected(): Boolean

  def login(user: String, pass: String): Boolean
}

object FTP {

  implicit val ftpApache: FTP[FTPClient] = new FTP[FTPClient] {

    override val instance: FTPClient = new FTPClient()

    override def disconnect(): Unit = instance.disconnect()

    override def connect(host: String, port: Int): Unit = instance.connect(host, port)

    override def isConnected(): Boolean = instance.isConnected()

    override def login(user: String, pass: String): Boolean = instance.login(user, pass)
  }
}
