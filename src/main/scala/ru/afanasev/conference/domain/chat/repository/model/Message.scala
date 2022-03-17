package ru.afanasev.conference.domain.chat.repository.model

import ru.afanasev.conference.domain.user.model.User
import java.time.LocalDateTime
import cats.implicits._

trait Message

object Message {
  final case class NewMessage(text: String, chatId: Long, authorId: Option[Long]) {
    def withUser(user: User) = {
      this.copy(authorId = user.id.some)
    }
  }

  final case class ExistingMessage(id: Long, text: String, dateCreated: LocalDateTime, dateupdated: LocalDateTime) 
}

