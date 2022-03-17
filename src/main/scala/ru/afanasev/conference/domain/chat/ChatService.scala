package ru.afanasev.conference.domain.chat

import ru.afanasev.conference.domain.chat.repository.ChatWsRepositoryAlgebra
import cats.implicits._
import cats.effect.kernel.Async
import org.http4s.websocket.WebSocketFrame
import com.typesafe.scalalogging.LazyLogging
import fs2._
import ru.afanasev.conference.domain.chat.repository.ChatRepositoryAlgebra
import ru.afanasev.conference.domain.chat.repository.model.Message._
import cats.effect.kernel.Sync
import ru.afanasev.conference.domain.user.model.User

class ChatService[F[_]: Async](
    chatWsRepository: ChatWsRepositoryAlgebra[F], 
    chatRepository: ChatRepositoryAlgebra[F]
  ) extends LazyLogging {

  def subscribe(chatId: Long, userId: Long) = for {
    chat <- chatWsRepository.addUser(chatId, userId)
    _    <- Sync[F].delay(logger.debug(s"[subscriber $userId] subscribe to chat $chatId"))
  } yield chat.topic.subscribe(1000)

  def publishMessage(chatId: Long, userId: Long): F[Pipe[F, WebSocketFrame, Unit]] = for {
    chat <- chatWsRepository.findChatWsById(chatId)
    _    <- Sync[F].delay(logger.debug(s"[subscriber $userId] send new message to chat $chatId"))
  } yield chat.topic.publish

  def unsubscribe(chatId: Long, userId: Long) = 
    chatWsRepository.removeUser(chatId, userId).flatTap { _ => Sync[F].delay {
        logger.debug(s"[subscriber $userId] unsubscribe from chat $chatId")
      }
    }

  def saveMessage(user: User, message: NewMessage) = 
    chatRepository.save(message.withUser(user)).flatTap { _ => Sync[F].delay {
        logger.debug(s"[subscriber ${user.id}] save new message to chat ${message.chatId}")
      }
    }
}

object ChatService {
  def apply[F[_]: Async](chatWsRepository: ChatWsRepositoryAlgebra[F], chatRepository: ChatRepositoryAlgebra[F]) = 
    new ChatService[F](chatWsRepository, chatRepository)
}