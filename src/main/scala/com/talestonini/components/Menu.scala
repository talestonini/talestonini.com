package com.talestonini.components

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

object Menu {

  private case class MenuItem(label: String, hash: String)

  private val menuItems: Seq[MenuItem] = Seq(
    MenuItem("Posts", "#/posts"),
    MenuItem("Tags", "#/tags"),
    MenuItem("About", "#/about")
  )

  def apply(isMobile: Boolean = false) = {
    val menuElems =
      div(
        className := "w3-rest w3-hide-small",
        div(className := "menu", menu())
      )

    val mobileMenuElems =
      div(
        div(
          className := "w3-hide-large w3-hide-medium",
          div(
            className := "hamburger",
            a(className := "w3-button w3-xxxlarge fa fa-bars", onClick --> toggleSidebar())
          )
        ),
        div(
          idAttr        := "sidebar",
          className     := "w3-sidebar w3-bar-block w3-animate-top mobile-menu",
          display       := "none",
          paddingTop.px := 8,
          mobileMenu()
        ),
        div(
          idAttr    := "overlay",
          className := "w3-overlay",
          onClick --> toggleSidebar(),
          cursor := "pointer"
        )
      )

    if (!isMobile) menuElems else mobileMenuElems
  }

  private def menu() = {
    val classes = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black w3-hide-small menu-item"
    for (mi <- menuItems)
      yield a(
        href      := mi.hash,
        className := classes,
        mi.label
      )
  }

  private def mobileMenu() = {
    val commonClasses = "w3-bar-item w3-button w3-bold"

    val close = a(
      className        := s"$commonClasses w3-xxxlarge w3-right-align fa fa-close",
      paddingBottom.px := 23,
      onClick --> toggleSidebar()
    )

    def w3Color(n: Int): String =
      if (n % 2 == 0) "w3-white" else "w3-light-grey"

    val items =
      for ((mi, i) <- menuItems.zipWithIndex)
        yield a(
          href      := mi.hash,
          className := s"$commonClasses w3-xlarge ${w3Color(i)}",
          onClick --> toggleSidebar(),
          mi.label
        )

    close +: items
  }

  @js.native
  @JSGlobal
  def toggleSidebar(): Unit = js.native

}