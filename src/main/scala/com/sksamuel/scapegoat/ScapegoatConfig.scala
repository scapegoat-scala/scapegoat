package com.sksamuel.scapegoat

import com.typesafe.config.ConfigFactory

import scala.reflect.runtime._
import scala.collection.JavaConverters._

/** @author Stephen Samuel */
object ScapegoatConfig extends App {

  def inspections: Seq[Inspection] = {
    val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
    def names: Seq[String] = {
      val conf = ConfigFactory.load()
      conf.getObject("scapegoat").keySet.asScala.toSeq
    }
    def obj(name: String): Inspection = {
      val module = runtimeMirror.staticModule(name)
      runtimeMirror.reflectModule(module).instance.asInstanceOf[Inspection]
    }
    names.map(obj)
  }

  println(inspections)
}
