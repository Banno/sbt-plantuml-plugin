# sbt-plantuml-plugin

> An sbt plugin to generate sequence diagrams from text files.

## Usage

Add the following to your project definition, below is an example in a `build.sbt` file.

```scala
import com.banno.plantuml.PlantUMLPlugin

name := "your-awesome-project-name"

enablePlugins(PlantUMLPlugin)
```

## Upgrading plantuml

Run the `download-plantuml-source` script from the root project directory.

**Note:** Make sure you have `dos2unix` installed, it's installable via `brew install dos2unix`.

```shell
~/src/banno/sbt-plantuml-plugin $ ./download-plantuml-source
Downloading sources
Cleaning
Unpackaging the archive
Removing unneeded files from archive.
Converting dos line endings to linux
All Done!
```

## Contributing

Fork away, commit, and send a pull request. Make sure that the tests pass before you submit your pull request.
