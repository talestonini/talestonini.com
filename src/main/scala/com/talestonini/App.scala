package com.talestonini

import cats.effect.unsafe.implicits.global
import com.raquo.laminar.api.*
import com.raquo.laminar.api.L.{*, given}
import com.raquo.waypoint.*
import com.talestonini.components.{Logo, Menu, Footer, LoadingHr}
import com.talestonini.db.CloudFirestore
import com.talestonini.db.model.*
import com.talestonini.{Firebase, Prism}
import com.talestonini.pages.*
import com.talestonini.pages.sourcegen.*
import com.talestonini.pages.sourcegen.posts.*
import com.talestonini.utils.scrollToTop
import org.scalajs.dom
import scala.concurrent.Promise
import scala.scalajs.concurrent.JSExecutionContext.queue
import scala.util.{Failure, Success}
import upickle.default.*

@main
def TalesToniniDotCom(): Unit = {
  // bootstraps Laminar by installing a Laminar Element in an existing DOM element
  renderOnDomContentLoaded(
    org.scalajs.dom.document.getElementById("app-container"),
    App()
  )
}

object App {

  // --- UI ------------------------------------------------------------------------------------------------------------

  def apply(): Element = {
    retrievePostsDataFromDb()

    div(
      div(
        className := "w3-content w3-row w3-hide-small",
        div(
          className := "w3-padding-8",
          Logo(),
          Menu()
        ),
        LoadingHr()
      ),
      div(
        className := "w3-content w3-row w3-hide-large w3-hide-medium",
        div(
          className := "w3-padding-8",
          Logo(),
          Menu(isMobile = true)
        ),
        LoadingHr()
      ),
      div(
        className := "w3-content",
        div(
          className := "content w3-padding-16",
          child <-- router.currentPageSignal.map(render)
        ),
        div(
          className := "w3-padding-8 w3-center",
          a(
            className := "back-to-link w3-hover-opacity",
            styleAttr := "text-decoration: none",
            href      := "#",
            "back home"
          ),
          a(
            className := "back-to-link w3-hover-opacity",
            styleAttr := "text-decoration: none",
            onClick --> { _ => scrollToTop() },
            "back to top"
          )
        ),
        LoadingHr()
      ),
      div(
        className := "footer w3-container w3-padding-8 w3-center w3-hide-small",
        Footer()
      ),
      div(
        className := "footer w3-container w3-padding-8 w3-center w3-hide-large w3-hide-medium",
        Footer()
      )
    )
  }

  // --- public --------------------------------------------------------------------------------------------------------

  var allTags: Set[String] = Set.empty

  sealed trait Page derives ReadWriter
  case object HomePage                 extends Page
  case object PostsPage                extends Page
  case object TagsPage                 extends Page
  case object AboutPage                extends Page
  case object LambdaDays24Page         extends Page
  case object DbLayerRefactorPage      extends Page
  case object ScalaDecoratorsPage      extends Page
  case object DockerVimPage            extends Page
  case object MorseCodeChallengePage   extends Page
  case object UrbanForestChallengePage extends Page
  case object FunProgCapstonePage      extends Page

  def navigateTo(page: Page, preNav: Option[() => Unit] = None,
    postNav: Option[() => Unit] = None): Binder[HtmlElement] = Binder { el =>
    val isLinkElement = el.ref.isInstanceOf[dom.html.Anchor]
    if (isLinkElement) {
      el.amend(href(router.absoluteUrlForPage(page)))
    }

    // If element is a link and user is holding a modifier while clicking:
    //  - Do nothing, browser will open the URL in new tab / window / etc. depending on the modifier key
    // Otherwise:
    //  - Perform regular pushState transition
    (onClick
      .filter(ev => !(isLinkElement && (ev.ctrlKey || ev.metaKey || ev.shiftKey || ev.altKey)))
      .preventDefault
      --> (_ => {
        if (preNav.isDefined)
          preNav.get()
        val res = router.pushState(page)
        if (postNav.isDefined)
          postNav.get()
        res
      })).bind(el)
  }

  def navigateByPostResource(resource: String) =
    navigateTo(postMap(resource).page)

  // --- private -------------------------------------------------------------------------------------------------------

  private lazy val currentHomeElement: Element = lambdaDays24Element

  // this is the URL path
  private lazy val pagePathMap: Map[Page, String] = Map(
    HomePage                 -> "", // do not change the home/root page path
    PostsPage                -> "posts",
    TagsPage                 -> "tags",
    AboutPage                -> "about",
    LambdaDays24Page         -> "lambdaDays24",
    DbLayerRefactorPage      -> "dbLayerRefactor",
    ScalaDecoratorsPage      -> "scalaDecorators",
    DockerVimPage            -> "dockerVim",
    MorseCodeChallengePage   -> "morseCodeChallenge",
    UrbanForestChallengePage -> "urbanForestChallenge",
    FunProgCapstonePage      -> "funProgCapstone"
  )

  private lazy val postsElement                = Posts()
  private lazy val tagsElement                 = Tags()
  private lazy val aboutElement                = About()
  private lazy val lambdaDays24Element         = LambdaDays24()
  private lazy val dbLayerRefactorElement      = DbLayerRefactor()
  private lazy val scalaDecoratorsElement      = ScalaDecorators()
  private lazy val dockerVimElement            = DockerVim()
  private lazy val morseCodeChallengeElement   = MorseCodeChallenge()
  private lazy val urbanForestChallengeElement = UrbanForestChallenge()
  private lazy val funProgCapstoneElement      = FunProgCapstone()

  private def pageElement(page: Page): Element =
    page match {
      case HomePage                 => currentHomeElement
      case PostsPage                => postsElement
      case TagsPage                 => tagsElement
      case AboutPage                => aboutElement
      case LambdaDays24Page         => lambdaDays24Element
      case DbLayerRefactorPage      => dbLayerRefactorElement
      case ScalaDecoratorsPage      => scalaDecoratorsElement
      case DockerVimPage            => dockerVimElement
      case MorseCodeChallengePage   => morseCodeChallengeElement
      case UrbanForestChallengePage => urbanForestChallengeElement
      case FunProgCapstonePage      => funProgCapstoneElement
    }

  private def buildRoute(page: Page) =
    if (page == HomePage)
      Route.static(HomePage, root / endOfSegments, basePath = Route.fragmentBasePath)
    else
      Route.static(
        page,
        root / pagePathMap.get(page).getOrElse(s"missing page path building route for page $page") / endOfSegments,
        basePath = Route.fragmentBasePath
      )

  private val router = new Router[Page](
    routes = pagePathMap.keySet.map(p => buildRoute(p)).toList,
    getPageTitle = _.toString,                 // mock page title (displayed in the browser tab next to favicon)
    serializePage = page => write(page),       // serialize page data for storage in History API log
    deserializePage = pageStr => read(pageStr) // deserialize the above
  )(
    popStateEvents = L.windowEvents(_.onPopState), // this is how Waypoint avoids an explicit dependency on Laminar
    owner = L.unsafeWindowOwner                    // this router will live as long as the window
  )

  private def render(page: Page): Element = {
    Firebase.gaViewing(pagePathMap.get(page).getOrElse(throw new Exception(s"missing page path rendering page $page")))
    Prism.prismHighlightAll() // in lieu of '<body onhashchange=...' as Waypoint does not trigger the hashchange event
    scrollToTop()             // without this, new page is rendered and scrolled to the position of the previous page
    pageElement(page)
  }

  // maps post resource names to corresponding page and promise
  // (the post promise, which is fulfilled when posts data is retrieved from the database)
  private case class PostEntry(page: Page, promise: Promise[Doc[Post]])
  private val postMap: Map[String, PostEntry] = Map(
    "lambdaDays24"         -> PostEntry(LambdaDays24Page, LambdaDays24.postDocPromise),
    "dbLayerRefactor"      -> PostEntry(DbLayerRefactorPage, DbLayerRefactor.postDocPromise),
    "scalaDecorators"      -> PostEntry(ScalaDecoratorsPage, ScalaDecorators.postDocPromise),
    "dockerVim"            -> PostEntry(DockerVimPage, DockerVim.postDocPromise),
    "morseCodeChallenge"   -> PostEntry(MorseCodeChallengePage, MorseCodeChallenge.postDocPromise),
    "urbanForestChallenge" -> PostEntry(UrbanForestChallengePage, UrbanForestChallenge.postDocPromise),
    "funProgCapstone"      -> PostEntry(FunProgCapstonePage, FunProgCapstone.postDocPromise)
  )

  // only posts with enabledFor <= MAX_ENABLED_FOR are retrieved
  // (for local development, increase this to EnabledFor.Dev)
  private val MAX_ENABLED_FOR = EnabledFor.Prod

  private def retrievePostsDataFromDb(): Unit = {
    val retrievingPosts = "retrievingPosts"
    LoadingHr.start(retrievingPosts)
    CloudFirestore
      .getPosts()
      .unsafeToFuture()
      .onComplete({
        case s: Success[Docs[Post]] =>
          for (
            postDoc <- s.get
            if postDoc.fields.enabledFor.getOrElse(EnabledFor.Default).ordinal <= MAX_ENABLED_FOR.ordinal
          ) {
            val resource = postDoc.fields.resource.get

            // to build the posts page, with the list of posts
            if (postMap.keySet.contains(resource))
              Posts.posts.update(data => data :+ postDoc)

            // to build each post page
            postMap
              .get(resource)
              .getOrElse(
                throw new Exception(s"missing entry in postsMap for $resource")
              )
              .promise success postDoc // fulfills the post promise

            allTags = allTags ++ postDoc.fields.tags.get.map(t => t.tag).toSet
          }
          LoadingHr.stop(retrievingPosts)
          WordCloud(Tags.wordCloudElementSelector(), allTags.toList)
        case f: Failure[Docs[Post]] =>
          println(s"failed getting posts: ${f.exception.getMessage()}")
          LoadingHr.stop(retrievingPosts)
      })(queue)
  }

}
