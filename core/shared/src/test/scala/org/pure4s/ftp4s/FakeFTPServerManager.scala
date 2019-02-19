package org.pure4s.ftp4s

import org.mockftpserver.fake.filesystem.{DirectoryEntry, FileEntry, Permissions, UnixFakeFileSystem}
import org.mockftpserver.fake.{FakeFtpServer, UserAccount}
import org.pure4s.ftp4s.model.FTPConfig

object FakeFTPServerManager {

  private lazy val server = new FakeFtpServer()

  def start(config: FTPConfig,  files: List[(String, String)]) = {

    server.setServerControlPort(config.port)

    server.addUserAccount(new UserAccount(config.user, config.pass, config.path))

    val fileSystem = new UnixFakeFileSystem()

    val directoryEntry = createDirectory(config.path, config.user)
    fileSystem.add(directoryEntry)

    for {
      (path, content) <- files
    } yield fileSystem.add(createFile(path, content))

    server.setFileSystem(fileSystem)

    server.start()
  }

  def stop() = if (server.isStarted) server.stop()

  private def createDirectory(path: String, user: String) = {
    val directoryEntry = new DirectoryEntry(path)
    directoryEntry.setPermissions(Permissions.ALL)
    directoryEntry.setOwner(user)
    directoryEntry
  }

  private def createFile(path: String, content: String) = {
    val fileEntry = new FileEntry(path, content)
    fileEntry.setPermissions(Permissions.ALL)
    fileEntry
  }
}
