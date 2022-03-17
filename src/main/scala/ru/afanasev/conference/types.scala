package ru.afanasev.conference

import fs2._
import org.http4s.websocket.WebSocketFrame

object types {
  type FromClient[F[_]] = Pipe[F, WebSocketFrame, Unit]
  type ToClient[F[_]] = Stream[F, WebSocketFrame]
}
