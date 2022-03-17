package ru.afanasev.conference.domain.chat.repository
import scala.collection.concurrent._
import cats.effect.std.Queue
import org.http4s.websocket.WebSocketFrame
import cats.effect.kernel.Async
import cats.implicits._
import com.typesafe.scalalogging.Logger
import fs2.concurrent.Topic
import com.typesafe.scalalogging.LazyLogging
import ru.afanasev.conference.domain.chat.repository.model.Chat
import cats.effect.kernel.Sync

class ChatWsRepositoryInterpreter[F[_]: Async]extends ChatWsRepositoryAlgebra[F] with LazyLogging {

  type ChatRepository = Map[Long, Chat[F]]

  val repository: ChatRepository = TrieMap.empty

  override def findChatWsById(chatId: Long): F[Chat[F]] = 
    Topic[F, WebSocketFrame].map(topic => {
      repository.getOrElseUpdate(chatId, Chat[F](topic, Set.empty))
    })

  override def addUser(chatId: Long, userId: Long): F[Chat[F]] = 
    findChatWsById(chatId).map(chat => {
      chat.copy(users = chat.users + userId)
    }).flatTap(chat => Sync[F].delay {
      repository.put(chatId, chat)
    })

  override def removeUser(chatId: Long, userId: Long): F[Chat[F]] = 
    findChatWsById(chatId).map(chat => 
      chat.copy(users = chat.users - userId)
    ).flatTap(chat => Sync[F].delay {
      if(chat.users.isEmpty) repository.remove(chatId)
      else repository.put(chatId, chat)
    })
}

object ChatWsRepositoryInterpreter {
  def apply[F[_]: Async] = 
    new ChatWsRepositoryInterpreter[F]
}
