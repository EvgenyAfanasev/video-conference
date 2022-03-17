package ru.afanasev.conference.domain.auth

import io.circe.generic.auto._
import io.circe.syntax._
import cats.effect.kernel.Async
import org.http4s.HttpRoutes
import org.http4s.dsl._
import org.http4s.circe._
import ru.afanasev.conference.domain.auth.repository.model.Form
import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._
import cats.implicits._

class AuthRoute[F[_]: Async](
    authService: AuthService[F] 
  ) {

  val dsl = Http4sDsl.apply[F]
  import dsl._

  def request = HttpRoutes.of[F] {
    case req @ POST -> Root / "auth" => for {
      form   <- req.as[Form]
      result <- authService.authenticate(
          form.username, 
          form.password
        )
      response <- result match {
        case Some(user) => Ok(user)
        case None       => Forbidden()
      }
    } yield response
  }
}
