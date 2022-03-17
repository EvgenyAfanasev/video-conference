package ru.afanasev.conference.domain.chat

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser.decode

import org.http4s.dsl.Http4sDsl
import org.http4s._

import fs2._
import fs2.concurrent._

import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame._
import org.http4s.server.websocket.WebSocketBuilder2

import cats._
import cats.implicits._

import cats.effect.std._
import cats.effect.kernel._

import com.typesafe.scalalogging.Logger

import ru.afanasev.conference.domain.chat.repository._
import ru.afanasev.conference.types._
import ru.afanasev.conference.domain.chat.repository.model.Message._
import ru.afanasev.conference.domain.user.model.User

import com.typesafe.scalalogging.LazyLogging

import org.http4s.AuthedRoutes

class ChatRoute[F[_]: Async](chatService: ChatService[F]) extends LazyLogging {

  implicit val dsl = Http4sDsl.apply[F]
  import dsl._

  def websocket(ws: WebSocketBuilder2[F]): AuthedRoutes[User, F] = AuthedRoutes.of[User, F] {

    case req @ GET -> Root / "chat" / chatId as user => {

      val toClient: ToClient[F] = Stream.force(
        chatService.subscribe(chatId.toLong, user.id)
      )

      val fromClient = (stream: Stream[F, WebSocketFrame]) => Stream.force {
        chatService.publishMessage(chatId.toLong, user.id).map { publisher =>
          stream.evalTap {
            case Text(value, _ ) => 
              decode[NewMessage](value).traverse { message =>
                chatService.saveMessage(user, message)
              }.void
            case _: Close => 
              chatService.unsubscribe(chatId.toLong, user.id).void
          }.through(publisher)
        }
      }

      ws.build(toClient, fromClient)
    }
  }
}

object ChatRoute {
  def apply[F[_]: Async](chatService: ChatService[F]) = 
    new ChatRoute[F](chatService)
}
