import scala.tools.nsc._
import scala.tools.nsc.plugins.{Plugin, PluginComponent}
import scala.tools.nsc.transform.{Transform, TypingTransformers}

class ScoveragePlugin(val global: Global) extends Plugin {

  override val name: String = "scapegoat"
  override val description: String = "scapegoat find bugs compiler plugin"

  val component = new ScapegoatComponent(global)
  override val components: List[PluginComponent] = List(component)

  override def processOptions(opts: List[String], error: String => Unit) {
  }

  override val optionsHelp: Option[String] = Some(Seq(
    "-P:scoverage:dataDir:<pathtodatadir>                  where the coverage files should be written\n"
  ).mkString("\n"))
}

class ScapegoatComponent(val global: Global) extends PluginComponent with TypingTransformers with Transform {

  import global._

  override val phaseName: String = "scapegoat"
  override val runsAfter: List[String] = List("parser")
  override val runsBefore = List[String]("namer")

  override def newPhase(prev: scala.tools.nsc.Phase): Phase = new Phase(prev) {
    override def run(): Unit = {
      println("[scapegoat]: Begin anaylsis")
      super.run()
      println("[scapegoat]: Anaylsis complete")
    }
  }

  protected def newTransformer(unit: CompilationUnit): Transformer = new Transformer(unit)
  class Transformer(unit: global.CompilationUnit) extends TypingTransformer(unit) {
    override def transform(tree: Tree) = tree
  }
}