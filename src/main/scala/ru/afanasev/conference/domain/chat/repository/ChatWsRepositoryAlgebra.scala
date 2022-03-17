package ru.afanasev.conference.domain.chat.repository

import cats.effect.std.Queue
import org.http4s.websocket.WebSocketFrame
import fs2.concurrent.Topic
import ru.afanasev.conference.domain.chat.repository.model.Chat

trait ChatWsRepositoryAlgebra[F[_]] {
  def findChatWsById(id: Long): F[Chat[F]]

  def addUser(chatId: Long, userId: Long): F[Chat[F]]

  def removeUser(chatId: Long, userId: Long): F[Chat[F]]
}
