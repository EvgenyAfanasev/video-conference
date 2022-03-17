package ru.afanasev.conference.config

import cats.effect.kernel.Resource
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import cats.effect.kernel.Async
import ru.afanasev.conference.config.Properties._

class DatabaseConfiguration[F[_]: Async](databaseProperties: DatabaseProperties) {
  def connection: Resource[F, HikariTransactor[F]] = for {
    pool <- ExecutionContexts.fixedThreadPool(databaseProperties.maxPoolSize)
    xa   <- HikariTransactor.newHikariTransactor[F](
       databaseProperties.driverClassName,
       databaseProperties.url,
       databaseProperties.username, 
       databaseProperties.password, 
       pool
    )
  } yield xa
}

object DatabaseConfiguration {
  def apply[F[_]: Async](databaseProperties: DatabaseProperties) = 
    new DatabaseConfiguration[F](databaseProperties)
}