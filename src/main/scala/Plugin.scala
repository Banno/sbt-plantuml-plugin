package com.banno.plantuml
import java.io.FileOutputStream

import sbt._
import Keys._
import org.apache.commons.io.FileUtils
import net.sourceforge.plantuml.{FileFormat, FileFormatOption, SourceStringReader}

object PlantUMLPlugin extends AutoPlugin {

  object autoImport {
    lazy val sequenceDiagramExtension = SettingKey[String]("sequence-diagram-extension")
    lazy val sequenceDiagramsLocation = SettingKey[File]("sequence-diagrams-locations")
    lazy val sequenceDiagramsOutput = SettingKey[File]("sequence-diagrams-output")
    lazy val sequenceDiagramsOutputFormat = SettingKey[FileFormat]("sequence-diagrams-output-format")

    lazy val generateSequenceDiagrams = TaskKey[Seq[File]]("generate-sequence-diagrams")
  }
  import autoImport._

  override lazy val projectSettings = Seq(
    sequenceDiagramExtension := ".diag",
    sequenceDiagramsLocation := baseDirectory.value / "src/main/resources/sequence-diagrams/",
    sequenceDiagramsOutput := baseDirectory.value / "src/main/resources/sequence-diagrams/",
    sequenceDiagramsOutputFormat := FileFormat.PNG,
    generateSequenceDiagrams := {
      val diagramExtension = sequenceDiagramExtension.value
      val diagramsInputLocation = sequenceDiagramsLocation.value
      val diagramsOutputLocation = sequenceDiagramsOutput.value
      val diagramsOutputFormat = sequenceDiagramsOutputFormat.value

      if (diagramsInputLocation.exists) {
        if (!diagramsOutputLocation.exists) {
          diagramsOutputLocation.mkdirs()
        }

        val sequenceDiagrams = diagramsInputLocation.listFiles().toList

        sequenceDiagrams.filter(_.getName().endsWith(diagramExtension)) map { sequenceDiagramFile =>
          val baseDirectory = ensurePathEndsInSlash(diagramsOutputLocation.getAbsolutePath)
          val filename = dropDiagramFileExtension(diagramExtension, sequenceDiagramFile.getName) + diagramsOutputFormat.getFileSuffix
          val output = new File(baseDirectory + filename)

          val diagramText = FileUtils.readFileToString(sequenceDiagramFile)

          renderDiagramToFile(diagramText, output, diagramsOutputFormat)
          output
        }
      } else {
        Seq.empty
      }
    },
    generateSequenceDiagrams <<= generateSequenceDiagrams triggeredBy (compile in Compile)
  )

  private[plantuml] def renderDiagramToFile(source: String, output: File, format: FileFormat) = {
    val fos = new FileOutputStream(output)
    val reader = new SourceStringReader(source)
    val desc = reader.generateImage(fos, new FileFormatOption(format))
    fos.close()
    desc
  }
  private[this] def ensurePathEndsInSlash(path: String) = if (path.endsWith("/")) path else s"${path}/"
  private[this] def dropDiagramFileExtension(extension: String, filename: String) = filename.replaceAll(extension, "")
}
