package ru.afanasev.conference.domain.user

class UserService[F[_]] {
  
}

object UserService {
  def apply[F[_]] =
    new UserService[F]    
}
