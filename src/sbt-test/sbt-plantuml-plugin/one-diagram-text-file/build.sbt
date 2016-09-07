import com.banno.plantuml.PlantUMLPlugin
import net.sourceforge.plantuml.FileFormat

name := "sbt-plantuml-plugin-one-diagram-text-file"

version := "0.1"

scalaVersion := "2.10.5"

enablePlugins(PlantUMLPlugin)

sequenceDiagramsOutputFormat := FileFormat.UTXT
