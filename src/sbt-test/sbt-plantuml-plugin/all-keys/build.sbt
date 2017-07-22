import com.banno.plantuml.PlantUMLPlugin
import net.sourceforge.plantuml.FileFormat

name := "sbt-plantuml-plugin-all-keys"

version := "0.1"

scalaVersion := "2.10.5"

enablePlugins(PlantUMLPlugin)

diagramExtension := ".ruml"
diagramsSource := file("./my_diags/")
diagramsTarget := file("./target/")
diagramFormat := FileFormat.UTXT


