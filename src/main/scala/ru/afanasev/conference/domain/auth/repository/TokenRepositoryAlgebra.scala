package ru.afanasev.conference.domain.auth.repository

import ru.afanasev.conference.domain.auth.repository.model._
import java.time.LocalDateTime

trait TokenRepositoryAlgebra[F[_]] {

  def findTokenByJwt(jwt: String): F[Option[TokenBody]]

  def updateToken(jwt: String, newRefresh: String): F[Option[Pair]]
}
