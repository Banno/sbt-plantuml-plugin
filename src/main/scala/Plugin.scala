package com.banno.plantuml
import sbt._
import Keys._
import org.apache.commons.io.FileUtils
import net.sourceforge.plantuml.SourceStringReader

object PlantUMLPlugin extends AutoPlugin {
  lazy val sequenceDiagramsLocation = SettingKey[File]("sequence-diagrams-locations")
  lazy val sequenceDiagramsOutput = SettingKey[File]("sequence-diagrams-output")

  lazy val generateSequenceDiagrams = TaskKey[Seq[File]]("generate-sequence-diagrams")

  override lazy val projectSettings = Seq(
    resourceGenerators in Compile += task[Seq[File]] {
      println("Hi2")
      sequenceDiagramsLocation := baseDirectory.value / "src/main/resources/sequence-diagrams/"
      sequenceDiagramsOutput := baseDirectory.value / "src/main/resources/sequence-diagrams/"

      println("Hi3")

      val diagramsInputLocation = sequenceDiagramsLocation.value
      val diagramsOutputLocation = sequenceDiagramsOutput.value

      println("input: " + diagramsInputLocation.getAbsolutePath)
      println("output: " + diagramsOutputLocation.getAbsolutePath)

      println("Hi4")

      if (diagramsInputLocation.exists) {
        if (!diagramsOutputLocation.exists) {
          diagramsOutputLocation.mkdirs()
        }

        val sequenceDiagrams = diagramsInputLocation.listFiles().toList
        sequenceDiagrams.filter(_.getName().endsWith(".diag")) map { sequenceDiagramFile =>
          val output = new File(diagramsOutputLocation.getAbsolutePath + sequenceDiagramFile.getName + ".png")
          val diagramText = FileUtils.readFileToString(sequenceDiagramFile)
          val reader = new SourceStringReader(diagramText)
          reader.generateImage(output)
          output
        }
      } else Seq.empty
    }
  )
}
