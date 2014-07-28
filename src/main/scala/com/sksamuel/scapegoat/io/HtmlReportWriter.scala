package com.sksamuel.scapegoat.io

import com.sksamuel.scapegoat.{Levels, Reporter}

import scala.xml.Unparsed

/** @author Stephen Samuel */
object HtmlReportWriter {

  def header =
    <head>
      <title>Scapegoat Inspection Reporter</title>{Unparsed(
      "<link href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css\" rel=\"stylesheet\">")}
    </head>

  def body(reporter: Reporter) =
    <body>
      <h1>Scapegoat Inspections
        Errors
        {reporter.warnings(Levels.Error).size.toString}
        Warnings
        {reporter.warnings(Levels.Warning).size.toString}
      </h1>{warnings(reporter)}
    </body>

  def warnings(reporter: Reporter) = reporter.warnings.map(warning =>
    <div class="warning">
      <div class="title">
        {warning.text}
      </div>
      <div class="snippet">
        {warning.snippet.orNull}
      </div>
      <div class="source">
        {warning.sourceFile}
        :
        {warning.line.toString}
      </div>
      <div class="level">
        {warning.level match {
        case Levels.Warning => <span class="label label-info">Info</span>
        case Levels.Warning => <span class="label label-warning">Warning</span>
        case Levels.Warning => <span class="label label-danger">Error</span>
      }}
      </div>
    </div>
  )

  def generate(reporter: Reporter) = <html>
    {header}{body(reporter)}
  </html>
}
