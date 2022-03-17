package ru.afanasev.conference.config

import pureconfig._
import pureconfig.generic.auto._

class Properties[F[_]] {
  import Properties._
  def getProperties = ConfigSource.default.loadOrThrow[ApplicationProperties]
}

object Properties {

  case class ApplicationProperties(
    server: ServerProperties,
    authentication: AuthenticationProperties,
    database: DatabaseProperties
  )

  case class ServerProperties(
    port: Int
  )

  case class AuthenticationProperties(
    secret: String
  )

  case class DatabaseProperties(
    maxPoolSize: Int, 
    driverClassName: String, 
    url: String,
    username: String,
    password: String
  )

  def apply[F[_]] = new Properties[F]
}
