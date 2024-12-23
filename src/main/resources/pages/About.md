{%
  class.name = About
%}
#About me

<div class="aside">
  <table class="w3-hide-small" style="width:100%">
    <tr>
      <td style="padding-right: 15px; width: 30%;"><img src="/img/talestonini.jpg" /></td>
      <td>
        <p>Hi!, my name is <strong>Tales Tonini</strong>. I'm a software engineer interested in Functional Programming, Distributed Systems and the Scala language.</p>
        <p>For the past 6 years I've been working at <a href="https://mantelgroup.com.au/">Mantel Group</a> delivering backend API's and client applications for Spark and Kafka, platforms that I really enjoy to program in. I've also extensive experience programming in Java and other JVM languages like Kotlin and Groovy.</p>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        I live in Melbourne, Australia, but am originally from Brazil. Aside from programming, I like to spend time with family and friends, swimming and the outdoors.
      </td>
    </tr>
  </table>
  <table class="w3-hide-large w3-hide-medium" style="width:100%">
    <tr>
      <td style="padding-right: 10px; width: 30%;"><img src="/img/talestonini.jpg" /></td>
      <td>
        Hi!, my name is <strong>Tales Tonini</strong>. I'm a software engineer interested in Functional Programming, Distributed Systems and the Scala language.
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <p>For the past 6 years I've been working at <a href="https://mantelgroup.com.au/">Mantel Group</a> delivering backend API's and client applications for Spark and Kafka, platforms that I really enjoy to program in. I've also extensive experience programming in Java and other JVM languages like Kotlin and Groovy.</p>
        <p>I live in Melbourne, Australia, but am originally from Brazil. Aside from programming, I like to spend time with family and friends, swimming and the outdoors.</p>
      </td>
    </tr>
  </table>
</div>

#About my website

I started this website to share my interests and learnings and as a way to play around with Scala.js. These are some of
the technologies and libraries that I use to build it:

- [Scala 3](https://www.scala-lang.org/)
- [Scala.js](https://www.scala-js.org/)
- [Laminar](https://laminar.dev/)
- [Vite](https://vitejs.dev/)
- [Laika](https://typelevel.org/Laika/)
- [Firebase](https://firebase.google.com/)
- [Cloud Firestore](https://firebase.google.com/docs/firestore)
- [W3.CSS](https://www.w3schools.com/w3css/default.asp)
- [Prism](https://prismjs.com/index.html)
- [Jason Davies' Word Cloud Generator](https://www.jasondavies.com/wordcloud/) using [D3](https://d3js.org/)

The **source code is open** [in my GitHub account](https://github.com/talestonini/talestonini.com) and I would gladly
receive feedback about it.

As you can see, I built a little engine to generate Scala.js code for the posts I write in
[Markdown](https://en.wikipedia.org/wiki/Markdown).

#Release notes

###0.2.x
- Replaced [RösHTTP](https://github.com/hmil/RosHTTP) for [http4s-dom](https://http4s.github.io/http4s-dom/) due to the
former not being maintained anymore and to give me a reason to play with [Cats](https://typelevel.org/cats/). This is at
the database layer, implementing API calls to Cloud Firestore.
- Packaging the app with [scalajs-bundler](https://scalacenter.github.io/scalajs-bundler/).

###0.3.x
- Added links that allow for sharing a post via LinkedIn and Twitter, and also for copying a post URL to the clipboard.

###0.4.x
- Refactored database package to remove usage of **Future** in favour of **Cats IO**.

###0.5.x
- Code cleanup.

###1.0.x
- Replaced [ThoughtWorks Binging](https://github.com/ThoughtWorksInc/Binding.scala) for [Laminar](https://laminar.dev/),
meaning the whole website UI was rewritten.
- Packaging the app with [Vite](http://vitejs.dev/).
- Added tags to posts and the Tags page.
- Dropped sharing to Twitter in favour of Mastodon.

###1.1.x
- Made Tags the home page.
- Configured deployment via GitHub Actions, Dependabot and Scala Steward.
- Bug fixes.

###1.2.x
- Added links above the footer to jump back home and to the top of the page.

###1.3.x
- Improved toggle mechanism to develop and publish new posts.
- Made most recent post the home page again.
