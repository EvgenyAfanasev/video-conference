package ru.afanasev.conference

import org.http4s.blaze.server.BlazeServerBuilder
import cats.effect.kernel.Async
import ru.afanasev.conference.domain.chat.ChatRoute
import cats.effect.ExitCode
import cats.effect.IOApp
import cats.effect.IO
import com.typesafe.scalalogging.Logger
import com.typesafe.scalalogging.LazyLogging
import ru.afanasev.conference.domain.chat.repository.ChatWsRepositoryInterpreter
import ru.afanasev.conference.domain.chat.repository.ChatWsRepositoryAlgebra
import ru.afanasev.conference.domain.chat.ChatService
import cats.implicits._
import cats.effect.kernel.Resource
import org.http4s.server.Router
import ru.afanasev.conference.config.DatabaseConfiguration
import ru.afanasev.conference.domain.chat.repository.ChatRepositoryAlgebra
import ru.afanasev.conference.domain.chat.repository.ChatRepositoryInterpreter
import ru.afanasev.conference.config.Properties
import ru.afanasev.conference.domain.user.UserService
import ru.afanasev.conference.domain.auth.AuthService
import ru.afanasev.conference.domain.auth.repository.TokenRepositoryAlgebra
import ru.afanasev.conference.domain.auth.repository.TokenRepositoryInterpreter
import ru.afanasev.conference.domain.auth.TokenService
import scala.util.Random
import java.time.LocalDateTime

object Application extends IOApp with LazyLogging {

  override def run(args: List[String]): IO[ExitCode] = 
    build[IO].use(_.compile.drain).as(ExitCode.Success)

  def build[F[_]: Async] = for {
    properties       <- Resource.pure(Properties[F].getProperties)
    transactor       <- DatabaseConfiguration[F](properties.database).connection
    random           <- Resource.pure[F, Random](new Random(System.currentTimeMillis()))
    tokenRepository  <- Resource.pure[F, TokenRepositoryAlgebra[F]](TokenRepositoryInterpreter[F])
    chatRepository   <- Resource.pure[F, ChatRepositoryAlgebra[F]](ChatRepositoryInterpreter[F](transactor))
    chatWsRepository <- Resource.pure[F, ChatWsRepositoryAlgebra[F]](ChatWsRepositoryInterpreter[F])
    userService      <- Resource.pure[F, UserService[F]](UserService[F])
    tokenService     <- Resource.pure[F, TokenService[F]](TokenService[F](tokenRepository, random))
    authService      <- Resource.pure[F, AuthService[F]](AuthService[F](userService, tokenService, properties.authentication))
    chatService      <- Resource.pure[F, ChatService[F]](ChatService[F](chatWsRepository, chatRepository))
    chatRoute        <- Resource.pure[F, ChatRoute[F]](ChatRoute[F](chatService))
  } yield BlazeServerBuilder[F]
    .bindHttp(properties.server.port)
    .withHttpWebSocketApp(ws => Router[F]("ws" -> authService.middlware(chatRoute.websocket(ws))).orNotFound)
    .serve
}
