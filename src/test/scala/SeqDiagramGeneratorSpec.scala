package com.banno.plantuml

import org.specs2.mutable.Specification
import java.io.File

import net.sourceforge.plantuml.FileFormat

class SeqDiagramGeneratorSpec extends Specification {

  val pngFileName = "output.png"
  test(pngFileName)
  val svgFileName = "output.svg"
  test(svgFileName)
  val txtFileName = "output.utxt"
  test(txtFileName)

  def test(fileName: String) = {
    s"Should be able to generate a $fileName" in {
      val output = new File(fileName)
      val src = """@startuml
                  |Bob -> Alice : hello
                  |@enduml""".stripMargin

      val desc = PlantUMLPlugin.renderDiagramToFile(src, output, FileFormat.UTXT)
      desc must not(beNull)
    }
    step {
      printDiagram(fileName)
      cleanUp(fileName)
    }
  }

  def cleanUp(name: String) = {
    val output = new File(name)
    output.exists() must beTrue
    output.delete()
  }

  def printDiagram(fileName: String) = {
    if(fileName.endsWith("txt")) {
      val source = scala.io.Source.fromFile(fileName)
      try println(source.mkString) finally source.close()
    }
  }
}
