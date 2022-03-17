package ru.afanasev.conference.domain.auth

import ru.afanasev.conference.domain.auth.repository.TokenRepositoryAlgebra
import ru.afanasev.conference.domain.auth.repository.model._
import java.time.LocalDateTime
import pdi.jwt.JwtCirce
import pdi.jwt.JwtAlgorithm
import io.circe.parser.decode
import io.circe.generic.auto._
import io.circe.syntax._
import ru.afanasev.conference.domain.user.model.User
import cats.effect.kernel.Async
import cats.implicits._
import scala.util.Random
import cats.data.OptionT

class TokenService[F[_]: Async](tokenRepository: TokenRepositoryAlgebra[F], random: Random) {

  def findTokenByJwt(jwt: String): F[Option[TokenBody]] = 
    tokenRepository.findTokenByJwt(jwt: String)

  def decodeJwt(jwt: String, secret: String) = for {
      claims  <- JwtCirce.decode(
        token      = jwt,
        key        = secret,
        algorithms = Seq(JwtAlgorithm.RS256)
      ).toOption
      subject <- claims.subject
      user    <- decode[User](subject).toOption
    } yield user

  def refreshToken(jwt: String): F[Option[Pair]] = 
    tokenRepository.updateToken(jwt, generateRefresh)

  def generateRefresh: String = random.nextString(11)

  def decodeToken(tokenBody: TokenBody, secret: String) = 
    if(tokenBody.expireAt.isAfter(LocalDateTime.now)) 
      OptionT.fromOption[F](decodeJwt(tokenBody.jwt, secret))
    else if(tokenBody.refresh == tokenBody.refresh) 
      OptionT(refreshToken(tokenBody.jwt)).flatMap { pair => 
        OptionT.fromOption[F](decodeJwt(pair.jwt, secret))
      } 
    else OptionT.none[F, User]
  
}

object TokenService {
  def apply[F[_]: Async](tokenRepository: TokenRepositoryAlgebra[F], random: Random) =
    new TokenService[F](tokenRepository, random)
}
