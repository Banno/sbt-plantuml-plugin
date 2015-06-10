package com.banno.plantuml
import java.io.File
import net.sourceforge.plantuml.SourceStringReader

object SequenceDiagramGenerator {
  def apply(path: String) = new SequenceDiagramGenerator(new File(path))
}

class SequenceDiagramGenerator private (base: File) {
  def generateAndStoreAt(path: String): Unit = {
    val outputLocation: String =
      if (path.startsWith("/")) {
        endPathInPNG(path)
      } else {
        endPathInPNG(ensurePathEndsWithSlash(base.getAbsolutePath) + path)
      }

    val output = new File(outputLocation)
    val reader = new SourceStringReader(base.getAbsolutePath)
    reader.generateImage(output)
  }

  private[this] def ensurePathEndsWithSlash(path: String): String = if (!path.endsWith("/")) s"${path}/" else path
  private[this] def endPathInPNG(path: String) = if (!path.endsWith(".png")) s"${path}.png" else path
}
