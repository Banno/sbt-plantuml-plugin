package com.banno.plantuml.sbt

import org.specs2.mutable.Specification
import net.sourceforge.plantuml.SourceStringReader
import java.io.File

class SeqDiagramGeneratorSpec extends Specification {
  "Should be able to generate a png" in {
    val output = new File("output.png")
    val src = """@startuml
Bob -> Alice : hello
@enduml"""

    val rdr  = new SourceStringReader(src)
    val desc = rdr.generateImage(output)
    desc must not(beNull)
  }
}
