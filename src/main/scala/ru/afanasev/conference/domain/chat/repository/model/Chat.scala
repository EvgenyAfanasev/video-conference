package ru.afanasev.conference.domain.chat.repository.model

import fs2.concurrent.Topic
import org.http4s.websocket.WebSocketFrame

final case class Chat[F[_]](topic: Topic[F, WebSocketFrame], users: Set[Long])