# Website README

The website (https://laminar.dev) is a [Docusaurus 1.x](https://v1.docusaurus.io/) app.
It's built and published directly with npm — no sbt / mdoc involved. Run all commands from this `website` directory.

Install dependencies (first time only):

    npm install

Start a local dev server with live reload:

    npm run start

Build the static site into `website/build/`:

    npm run build

Publish to GitHub Pages (`gh-pages` branch):

    GIT_USER=<your-github-username> USE_SSH=true npm run publish-gh-pages
