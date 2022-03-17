scalaVersion := "2.13.8"
name := "hello-world"
organization := "ru.afanasev.conference"
version := "1.0"

val doobieVersion  = "1.0.0-RC2"
val circeVersion   = "0.14.1"
val http4sVersion  = "0.23.10"
val jwtVersion     = "9.0.4"
val configVersion  = "0.17.1"
val loggingVersion = "3.9.4"
val logbackVersion = "1.2.10"

libraryDependencies += "org.http4s"                 %% "http4s-dsl"          % http4sVersion
libraryDependencies += "org.http4s"                 %% "http4s-blaze-server" % http4sVersion
libraryDependencies += "org.http4s"                 %% "http4s-circe"        % http4sVersion
libraryDependencies += "org.http4s"                 %% "http4s-blaze-client" % http4sVersion
libraryDependencies += "ch.qos.logback"             % "logback-classic"      % logbackVersion
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"       % loggingVersion
libraryDependencies += "org.tpolecat"               %% "doobie-core"         % doobieVersion
libraryDependencies += "org.tpolecat"               %% "doobie-postgres"     % doobieVersion
libraryDependencies += "org.tpolecat"               %% "doobie-hikari"       % doobieVersion
libraryDependencies += "com.github.pureconfig"      %% "pureconfig"          % configVersion
libraryDependencies += "io.circe"                   %% "circe-generic"       % circeVersion
libraryDependencies += "io.circe"                   %% "circe-literal"       % circeVersion
libraryDependencies += "io.circe"                   %% "circe-parser"        % circeVersion
libraryDependencies += "com.github.jwt-scala"       %% "jwt-core"            % jwtVersion
libraryDependencies += "com.github.jwt-scala"       %% "jwt-circe"           % jwtVersion