package ru.afanasev.conference.domain.auth.repository

import ru.afanasev.conference.domain.auth.repository.model._
import scala.collection.concurrent._
import cats.effect.kernel.Sync
import cats.effect.kernel.Async
import java.time.LocalDateTime

class TokenRepositoryInterpreter[F[_]: Async] extends TokenRepositoryAlgebra[F] {

  val repository: Map[String, TokenBody] = TrieMap.empty

  override def findTokenByJwt(jwt: String): F[Option[TokenBody]] = Sync[F].delay {
    repository.get(jwt)
  }

  override def updateToken(jwt: String, refresh: String) = Sync[F].delay {
    repository.replace(jwt, TokenBody(jwt, refresh, LocalDateTime.now())).map { tokenBody => 
      Pair(jwt, refresh)  
    }
  }
}

object TokenRepositoryInterpreter {
  def apply[F[_]: Async] = 
    new TokenRepositoryInterpreter[F]
}