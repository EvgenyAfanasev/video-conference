package ru.afanasev.conference.domain.auth.repository.model

final case class Jwt(userId: Long)

final case class Pair(jwt: String, refresh: String)