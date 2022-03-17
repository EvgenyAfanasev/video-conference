package ru.afanasev.conference.domain.chat.repository

import cats.effect.kernel.Async
import ru.afanasev.conference.domain.chat.repository.model.Message._
import java.time.LocalDateTime
import java.sql.Timestamp
import doobie.implicits._
import doobie.implicits.javatime._

import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import cats._
import cats.data._
import cats.effect._
import cats.implicits._

class ChatRepositoryInterpreter[F[_]: Async](transactor: Transactor[F]) extends ChatRepositoryAlgebra[F] {

  private val table = "video.chat"

  override def save(message: NewMessage): F[ExistingMessage] = {
    sql"""
      INSERT INTO $table (
        text, date_created, date_updated, user_id, chat_id
      ) VALUES (
        ${message.text} ${now} ${now} ${message.authorId} ${message.chatId}}
      ) RETURNING id, text, date_created, date_updated, user_id, chat_id
    """
    .query[ExistingMessage]
    .unique
    .transact(transactor)
  }

  def findById(messageId: Long) = {
    sql"""
      SELECT * FROM $table WHERE id = $messageId
    """.query[ExistingMessage].option.transact(transactor)
  }


  override def findByChatId(chatId: Long, limit: Long): F[Seq[ExistingMessage]] = ???

  def now = Timestamp.valueOf(LocalDateTime.now())

}

object ChatRepositoryInterpreter {
  def apply[F[_]: Async](transactor: Transactor[F]) = 
    new ChatRepositoryInterpreter[F](transactor)
}
