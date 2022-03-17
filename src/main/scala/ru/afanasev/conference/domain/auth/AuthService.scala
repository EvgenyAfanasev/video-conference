package ru.afanasev.conference.domain.auth

import ru.afanasev.conference.domain.user.UserService
import ru.afanasev.conference.domain.user.model.User
import cats.data.Kleisli
import cats.data.OptionT
import org.http4s.Request
import cats.implicits._
import cats.effect.kernel.Async
import org.http4s.server.AuthMiddleware
import java.time.LocalDateTime
import ru.afanasev.conference.config.Properties._

class AuthService[F[_]: Async](
    userService: UserService[F], 
    tokenService: TokenService[F], 
    authProperties: AuthenticationProperties
  ) {

  type Auth[A] = OptionT[F, A]

  def authorize(jwt: String, refresh: String): F[Option[User]] = (for {
    tokenBody <- OptionT(tokenService.findTokenByJwt(jwt))
    result    <- tokenService.decodeToken(tokenBody, authProperties.secret)
  } yield result).value

  def authenticate(username: String, password: String): F[Option[User]] = {
    ???
  }

  private def authUser: Kleisli[Auth, Request[F], User] = Kleisli { request => for {
      jwt     <- OptionT.fromOption[F](request.cookies.find(request => request.name == "auth-token"))
      refresh <- OptionT.fromOption[F](request.cookies.find(request => request.name == "refresh-token"))
      user    <- OptionT(authorize(jwt.content, refresh.content))
    } yield user
  }

  val middlware = AuthMiddleware(authUser)
}

object AuthService {
  def apply[F[_]: Async](
    userService: UserService[F], 
    tokenService: TokenService[F], 
    authProperties: AuthenticationProperties
  ) = new AuthService[F](userService, tokenService, authProperties: AuthenticationProperties)
}
