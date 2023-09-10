import org.scalajs.linker.interface.ModuleSplitStyle

val circeVersion  = "0.15.0-M1"
val http4sVersion = "1.0.0-M32" // proving hard to upgrade this dependency (23 Aug 2023)

lazy val ttDotCom = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .enablePlugins(BuildInfoPlugin, LaikaPlugin)
  .settings(
    scalaVersion := "3.3.0",
    version      := "1.0.0",

    // Tell Scala.js that this is an application with a main method
    scalaJSUseMainModuleInitializer := true,

    /* Configure Scala.js to emit modules in the optimal way to
     * connect to Vite's incremental reload.
     * - emit ECMAScript modules
     * - emit as many small modules as possible for classes in the "com.talestonini" package
     * - emit as few (large) modules as possible for all other classes
     *   (in particular, for the standard library)
     */
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("com.talestonini"))
        )
    },

    // BuilfInfoPlugin
    buildInfoKeys    := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.talestonini",

    // Dependencies
    libraryDependencies ++= Seq(
      /* Depend on the scalajs-dom library.
       * It provides static types for the browser DOM APIs.
       */
      "org.scala-js" %%% "scalajs-dom" % "2.6.0",

      // Depend on Laminar (reactive web application pages and routing)
      "com.raquo" %%% "laminar"  % "16.0.0",
      "com.raquo" %%% "waypoint" % "7.0.0", // routing, independent of Laminar

      // For serialising page data for storage in History API log
      // (used by the Wayoint routing code)
      "com.lihaoyi" %%% "upickle" % "3.1.2",

      // XML (for the pages with content generated by Laika)
      "org.scala-lang.modules" %%% "scala-xml" % "2.2.0",

      // Http4s (backend and database stuff)
      "io.circe"   %%% "circe-core"      % circeVersion,
      "io.circe"   %%% "circe-generic"   % circeVersion,
      "io.circe"   %%% "circe-parser"    % circeVersion,
      "org.http4s" %%% "http4s-circe"    % http4sVersion,
      "org.http4s" %%% "http4s-client"   % http4sVersion,
      "org.http4s" %%% "http4s-dom"      % http4sVersion,
      "io.monix"   %%% "monix-execution" % "3.4.1",

      // Java Time for Scala.js
      "io.github.cquiroz" %%% "scala-java-time"      % "2.5.0",
      "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.5.0",

      // Testing framework
      "org.scalameta" %%% "munit"               % "1.0.0-M8"    % Test,
      "org.scalatest" %%% "scalatest"           % "3.3.0-SNAP4" % Test,
      "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7"       % Test
    )
  )

// Test setup
Test / jsEnv := new org.scalajs.jsenv.selenium.SeleniumJSEnv(new org.openqa.selenium.firefox.FirefoxOptions())

// LaikaPlugin
Laika / sourceDirectories := Seq(sourceDirectory.value / "main/resources/pages")
laikaSite / target        := sourceDirectory.value / "main/scala/com/talestonini/pages/sourcegen"
laikaTheme                := laika.theme.Theme.empty
laikaExtensions           := Seq(laika.markdown.github.GitHubFlavor)
laikaConfig               := LaikaConfig.defaults.withRawContent

lazy val laikaHTML2Scala = taskKey[Unit]("Renames Laika's .html outputs to .scala")
laikaHTML2Scala := {
  renameHtmlToScala(sourceDirectory.value / "main/scala/com/talestonini/pages/sourcegen")
  renameHtmlToScala(sourceDirectory.value / "main/scala/com/talestonini/pages/sourcegen/posts")
}

def renameHtmlToScala(dir: File) = {
  file(dir.getAbsolutePath)
    .listFiles()
    .map(f => {
      val filename = f.getAbsolutePath()
      if (filename.endsWith("html")) {
        val prefix = filename.substring(0, filename.lastIndexOf("."))
        f.renameTo(new File(prefix + ".scala"))
      }
    })
}

lazy val laikaPrep = taskKey[Unit]("Runs all Laika-related tasks at once.")
laikaPrep         := Def.sequential(laikaHTML, laikaHTML2Scala).value
Compile / compile := ((Compile / compile) dependsOn laikaPrep).value
