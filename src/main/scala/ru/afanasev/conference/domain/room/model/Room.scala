package ru.afanasev.conference.domain.room.model

trait Room 

object Room {
  case class NewRoom() extends Room
  case class ExistingRoom() extends Room
}
