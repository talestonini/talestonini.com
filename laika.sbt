import laika.api.{MarkupParser, Renderer}
import laika.format.{HTML, Markdown}

// ---------------------------------------------------------------------------------------------------------------------
// Laika Library API setup (laika-sbt does not support sbt 2.x, so we drive laika-core directly)
// Task to generate scala classes from MarkDown pages
// ---------------------------------------------------------------------------------------------------------------------
lazy val laikaPrep = taskKey[Unit]("Generates Scala source files from MarkDown pages using the Laika Library API")

laikaPrep := {
  val log = streams.value.log

  val pagesDir          = sourceDirectory.value / "main/resources/pages"
  val postsDir          = pagesDir / "posts"
  val sourcegenDir      = sourceDirectory.value / "main/scala/com/talestonini/pages/sourcegen"
  val sourcegenPostsDir = sourcegenDir / "posts"

  val parser   = MarkupParser.of(Markdown.GitHubFlavor).withRawContent.build
  val renderer = Renderer.of(HTML).build

  val placeholderClassName = "${class.name}"
  val placeholderContent   = "${cursor.currentDocument.content}"

  // indents every line of `content`, except the first, by `indent` spaces; the first line
  // is left untouched because it follows the indentation already present in the template
  def indentContinuationLines(content: String, indent: String): String =
    content
      .split("\n", -1)
      .zipWithIndex
      .map { case (line, i) => if (i == 0) line else indent + line }
      .mkString("\n")

  def substitute(template: String, className: String, content: String): String = {
    val withClassName = template.replace(placeholderClassName, className)

    val lines = withClassName.split("\n", -1)
    val idx   = lines.indexWhere(_.contains(placeholderContent))
    if (idx < 0) {
      withClassName
    } else {
      val line          = lines(idx)
      val indent        = " " * line.indexOf(placeholderContent)
      val indentedContent = indentContinuationLines(content, indent)
      lines(idx) = line.replace(placeholderContent, indentedContent)
      lines.mkString("\n")
    }
  }

  def generate(mdFile: File, templateFile: File, outDir: File): Unit = {
    val input = IO.read(mdFile)
    parser.parse(input) match {
      case Left(err) =>
        sys.error(s"Laika failed to parse ${mdFile.getName}: $err")
      case Right(document) =>
        document.config.get[String]("class.name") match {
          case Left(err) =>
            sys.error(s"Laika failed to read class.name from ${mdFile.getName}: $err")
          case Right(className) =>
            renderer.render(document) match {
              case Left(err) =>
                sys.error(s"Laika failed to render ${mdFile.getName}: $err")
              case Right(html) =>
                val template = IO.read(templateFile)
                val output   = substitute(template, className, html)
                val outFile  = outDir / s"$className.scala"
                IO.write(outFile, output)
                log.info(s"Generated $outFile")
            }
        }
    }
  }

  def mdFiles(dir: File): Seq[File] =
    Option(dir.listFiles()).getOrElse(Array.empty).filter(_.getName.endsWith(".md")).toSeq.sortBy(_.getName)

  mdFiles(pagesDir).foreach(md => generate(md, pagesDir / "default.template.html", sourcegenDir))
  mdFiles(postsDir).foreach(md => generate(md, postsDir / "default.template.html", sourcegenPostsDir))
}
