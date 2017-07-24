package com.banno.plantuml

import java.io.FileOutputStream
import java.nio.charset.Charset

import sbt._
import Keys._
import net.sourceforge.plantuml.core.DiagramDescription
import org.apache.commons.io.FileUtils
import net.sourceforge.plantuml.{FileFormat, FileFormatOption, SourceStringReader}

object PlantUMLPlugin extends AutoPlugin {

  object autoImport {
    lazy val diagramExtension = SettingKey[String]("diagram-extension")
    lazy val diagramsSource = SettingKey[File]("diagrams-source")
    lazy val diagramsTarget = SettingKey[File]("diagrams-target")
    lazy val diagramFormat = SettingKey[FileFormat]("diagrams-format")

    lazy val generateDiagrams = TaskKey[Seq[File]]("generate-diagrams")
  }

  import autoImport._

  private val DefaultExtension = ".puml"
  private val DefaultSource = "src/main/resources/diagrams/"
  private val DefaultTarget = DefaultSource
  private val DefaultFormat = FileFormat.PNG

  override lazy val projectSettings = Seq(
    diagramExtension := DefaultExtension,
    diagramsSource := baseDirectory.value / DefaultSource,
    diagramsTarget := baseDirectory.value / DefaultTarget,
    diagramFormat := DefaultFormat,
    generateDiagrams := {
      val extension = diagramExtension.value
      val source = diagramsSource.value
      val target = diagramsTarget.value
      val format = diagramFormat.value

      if (!source.exists) {
        Seq.empty
      } else {
        if (!target.exists) {
          target.mkdirs()
        }

        val diagrams = source.listFiles().toList

        diagrams.filter(_.getName().endsWith(extension)).map { diagramFile => {
          val baseDirectory = ensurePathEndsInSlash(target.getAbsolutePath)
          val filename = dropDiagramFileExtension(extension, diagramFile.getName) + format.getFileSuffix
          val output = new File(baseDirectory + filename)

          val diagramText = FileUtils.readFileToString(diagramFile, Charset.defaultCharset())

          val diagramDescription = renderDiagramToFile(diagramText, output, format)
          output
        }}
      }
    },
    generateDiagrams := (generateDiagrams triggeredBy (compile in Compile)).value
  )

  private[plantuml] def renderDiagramToFile(source: String, output: File, format: FileFormat): DiagramDescription = {
    val fos = new FileOutputStream(output)
    val reader = new SourceStringReader(source)
    val desc = reader.outputImage(fos, new FileFormatOption(format))
    fos.close()
    desc
  }

  private[this] def ensurePathEndsInSlash(path: String) = if (path.endsWith("/")) path else s"${path}/"
  private[this] def dropDiagramFileExtension(extension: String, filename: String) = filename.replaceAll(extension, "")
}
