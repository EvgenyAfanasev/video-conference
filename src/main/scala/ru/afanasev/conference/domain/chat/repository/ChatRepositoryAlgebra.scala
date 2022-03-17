package ru.afanasev.conference.domain.chat.repository

import ru.afanasev.conference.domain.chat.repository.model.Message._

trait ChatRepositoryAlgebra[F[_]] {
  
  def save(message: NewMessage): F[ExistingMessage]

  def findByChatId(chatId: Long, limit: Long): F[Seq[ExistingMessage]]
}
