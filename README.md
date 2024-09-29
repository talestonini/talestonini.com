# TalesTonini.com

This is my personal website, where I add posts about varied stuff. It is developed in Scala.js and hosted with Firebase
Hosting.

## Pre-requisites

### Java

Attention with the Java version in use.  Java 17 and 20 have allowed SBT 1.8 to launch fine, whereas 21 or 22 have not
until I updated to SBT 1.10.  So make sure to have an SBT version that is compatible with the JDK in use, and make sure
that JDK is compatible with the Scala version of the project.

### SBT

Launch SBT with the provided script `sbt.sh` for more memory.

## Running Unit Tests

In SBT:

    test

*Note:* [`sbt-scoverage`](https://github.com/scoverage/sbt-scoverage) is not configured for this project as it only
supports ScalaJS 2 at the time of writing (8 Aug 2024).

## Developing with Auto-Reload

In SBT:

    ~fastLinkJS

In another terminal, execute `dev_local.sh`, which essentially does:

    ./prep_public.sh public
    npm run dev

## Building for Deployment

These are the steps to build the final (optimised) version of the website for testing before deployment to Firebase
Hosting (ie, Vite preview).  For deployment per-se, they are not necessary, as everything is automated in script
`deploy.sh`.

In SBT:

    ~fullLinkJS

In another terminal:

    ./prep_public.sh public   <-- must be before `npm run build`, as that
    npm run build             <-- places `public` artifacts in `dist`
    npm run preview

*Note:* Access to the database and the word cloud will not work in the preview.

## Deploying

    ./deploy.sh

## Firebase Util

To get a Firebase token for headless CI:

    firebase login:ci

Then create an environment variable named `FIREBASE_TOKEN` with the token value.

## TODO

### New features
- ~~Lazily build posts' `Element`s (improve performance loading images)~~
- ~~Update the about page~~
- ~~Mastodon~~
- ~~Likes~~: not to do anymore, as I removed the login feature
- ~~Tweet/LinkedIn a post~~
- ~~Tags (with word cloud)~~
- ~~JS bundler~~: makes no sense, as the website does not depend on any npm library (that is not delivered by *Firebase
Hosting*)
- ~~Laika~~
  - ~~code with braces -> escape braces~~
- ~~Home content~~
- ~~Version number in footer~~

### Issues
- ~~Missing a page with terms and conditions / privacy policy~~
- ~~Improve the about page with sections about me and the website~~
- ~~Fix loading wheel when incognito~~
- ~~About page -> layout not good for desktop~~
- ~~About page with duplicate content when flipping mobile horizontally~~

### Nice to have
- ~~Open-source the website~~
- ~~Improve delivery of scripts/styles from `index.html` (Firebase ones are fine, I mean all others)~~
- ~~Auto-deploy? with [Deploy to Firebase Hosting](https://github.com/marketplace/actions/deploy-to-firebase-hosting)?
no; actually, configure deploy via GitHub Actions with a workflow of my own~~
