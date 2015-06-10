package com.banno.plantuml
import java.io.File
import org.specs2.mutable.Specification
import net.sourceforge.plantuml.SourceStringReader

class SequenceDiagramGeneratorSpec extends Specification {

  "Should be able to generate a png" in {
    val output = new File("output.png")
    val src = """@startuml
Bob -> Alice : hello
@enduml"""

    val rdr  = new SourceStringReader(src)
    val desc = rdr.generateImage(output)
    desc must not(beNull)
  }

  "use our wrapper class to generate" in {
    val generator = SequenceDiagramGenerator("")
    val output = "src/test/resources/example.png"

    locally {
      val outputFile = new File(output)
      if (outputFile.exists) {
        outputFile.delete
        outputFile.exists must beFalse
      }
    }

    generator.generateAndStoreAt(output)
    (new File(output)).exists must beTrue
  }
}
