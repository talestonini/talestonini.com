// Test Setup (must be before sbt-scalajs)
libraryDependencies += "org.scala-js"          %% "scalajs-env-selenium"    % "1.1.1"
libraryDependencies += "io.github.gmkumar2005" %% "scala-js-env-playwright" % "0.1.18"

addSbtPlugin("org.scala-js" % "sbt-scalajs"   % "1.22.0")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.13.1")

// laika-sbt does not support sbt 2.x yet, so we use the Laika Library API directly, driven from a
// custom task defined in laika.sbt, instead of using the plugin.
libraryDependencies += "org.typelevel" %% "laika-core" % "1.2.0"

