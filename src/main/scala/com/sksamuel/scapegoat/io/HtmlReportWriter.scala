package com.sksamuel.scapegoat.io

import com.sksamuel.scapegoat.{Levels, Reporter}

/** @author Stephen Samuel */
object HtmlReportWriter {

  def header =
    <head>
      <title>Scapegoat Inspection Reporter</title>
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
        {warning.snippet}
      </div>
      <div class="line">
        {warning.line.toString}
      </div>
      <div class="level">
        {warning.level.toString}
      </div>
      <div class="file">
        {warning.sourceFile}
      </div>
    </div>
  )

  def generate(reporter: Reporter) = <html>
    {header}{body(reporter)}
  </html>
}
