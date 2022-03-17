package ru.afanasev.conference.domain.auth.repository.model

import ru.afanasev.conference.domain.user.model.User
import java.time.LocalDateTime

final case class TokenBody(jwt: String, refresh: String, expireAt: LocalDateTime)