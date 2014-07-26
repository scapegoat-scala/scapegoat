package com.sksamuel.scapegoat

import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

/** @author Stephen Samuel */
object ScapegoatConfig extends App {
  def inspections: Seq[Inspection] = {
    def names: Seq[String] = {
      val conf = ConfigFactory.load()
      conf.getObject("scapegoat").keySet.asScala.toSeq
    }
    def instance(name: String): Inspection = Class.forName(name).newInstance.asInstanceOf[Inspection]
    names.map(instance)
  }
}
