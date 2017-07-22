# sbt-plantuml-plugin

> An sbt plugin to generate sequence diagrams from text files.

## Usage

Add the following to your project definition, below is an example in a `build.sbt` file. They are generated from the `*.diag` files under the default of `src/main/resources/sequence-diagrams/` and outputted to the same directory.

```scala
import com.banno.plantuml.PlantUMLPlugin

name := "your-awesome-project-name"

enablePlugins(PlantUMLPlugin)
```

With the following added to your `project/plugins.sbt`

```scala
resolvers += Resolver.url("bintray-banno-oss-releases", url("https://dl.bintray.com/banno/oss"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.banno" % "sbt-plantuml-plugin" % "1.1.1")
```

#### Custom input/output directories and file extension

```scala
import com.banno.plantuml.PlantUMLPlugin

name := "your-awesome-project-name"

enablePlugins(PlantUMLPlugin)

PlantUMLPlugin.sequenceDiagramExtension := "*.seq"

PlantUMLPlugin.sequenceDiagramsLocation := file("./other/path/sequence-diagrams/")

PlantUMLPlugin.sequenceDiagramsOutput := file("./other/path/sequence-diagrams/")
```

## Contributing

Fork away, commit, and send a pull request. Make sure that the tests pass before you submit your pull request.
