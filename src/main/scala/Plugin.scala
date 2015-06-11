package com.banno.plantuml
import sbt._
import Keys._
import org.apache.commons.io.FileUtils
import net.sourceforge.plantuml.SourceStringReader

object PlantUMLPlugin extends AutoPlugin {
  lazy val sequenceDiagramExtension = SettingKey[String]("sequence-diagram-extension")
  lazy val sequenceDiagramsLocation = SettingKey[File]("sequence-diagrams-locations")
  lazy val sequenceDiagramsOutput = SettingKey[File]("sequence-diagrams-output")

  lazy val generateSequenceDiagrams = TaskKey[Seq[File]]("generate-sequence-diagrams")

  override lazy val projectSettings = Seq(
    sequenceDiagramExtension := ".diag",
    sequenceDiagramsLocation := baseDirectory.value / "src/main/resources/sequence-diagrams/",
    sequenceDiagramsOutput := baseDirectory.value / "src/main/resources/sequence-diagrams/",
    generateSequenceDiagrams := {
      val diagramExtension = sequenceDiagramExtension.value
      val diagramsInputLocation = sequenceDiagramsLocation.value
      val diagramsOutputLocation = sequenceDiagramsOutput.value

      if (diagramsInputLocation.exists) {
        if (!diagramsOutputLocation.exists) {
          diagramsOutputLocation.mkdirs()
        }

        val sequenceDiagrams = diagramsInputLocation.listFiles().toList

        sequenceDiagrams.filter(_.getName().endsWith(diagramExtension)) map { sequenceDiagramFile =>
          val baseDirectory = ensurePathEndsInSlash(diagramsOutputLocation.getAbsolutePath)
          val filename = dropDiagramFileExtension(diagramExtension, sequenceDiagramFile.getName) + ".png"
          val output = new File(baseDirectory + filename)

          val diagramText = FileUtils.readFileToString(sequenceDiagramFile)
          val reader = new SourceStringReader(diagramText)
          reader.generateImage(output)
          output
        }
      } else {
        Seq.empty
      }
    },
    generateSequenceDiagrams <<= generateSequenceDiagrams triggeredBy (compile in Compile)
  )

  private[this] def ensurePathEndsInSlash(path: String) = if (path.endsWith("/")) path else s"${path}/"
  private[this] def dropDiagramFileExtension(extension: String, filename: String) = filename.replaceAll(extension, "")
}
