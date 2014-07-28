package com.sksamuel.scapegoat.io

import com.sksamuel.scapegoat.{Levels, Reporter}

import scala.xml.Unparsed

/** @author Stephen Samuel */
object HtmlReportWriter {

  val css =
    """
      |      body {
      |        font-family: 'Ubuntu', sans-serif;
      |        padding: 0 15px;
      |      }
      |      h1 {
      |      	color: #515151;
      |      	font-weight: 700;
      |      }
      |      h3 {
      |      	color: #8a8a8a;
      |      	font-weight: 400;
      |      }
      |      .warning {
      |      	  background :#F1F3F2;
      |      	  border-bottom-left-radius: 6px;
      |		      border-bottom-right-radius: 6px;
      |      	  margin-bottom: 3px;
      |      	  padding: 12px;
      |      }
      |      .title {
      |      	color: #717171;
      |      	font-size: 16px;
      |      }
      |      .source {
      |        float: right;
      |      	font-style: italic;
      |      }
      |
    """.stripMargin

  def header =
    <head>
      <title>Scapegoat Inspection Reporter</title>{Unparsed(
      "<link href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css\" rel=\"stylesheet\">")}{Unparsed(
      """<link href='http://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700,300italic,400italic,500italic,700italic' rel='stylesheet' type='text/css'>""")}<style>
      {css}
    </style>
    </head>

  def body(reporter: Reporter) =
    <body>
      <h1>Scapegoat Inspections</h1>
      <h3>
        Errors
        {reporter.warnings(Levels.Error).size.toString}
        Warnings
        {reporter.warnings(Levels.Warning).size.toString}
      </h3>{warnings(reporter)}
    </body>

  def warnings(reporter: Reporter) = {
    reporter.warnings.map {
      case warning =>
        val source = warning.sourceFile + ":" + warning.line
        <div class="warning">
          <div class="source">
            {source}
          </div>
          <div class="title">
            {warning.text}
          </div>{warning.level match {
          case Levels.Info => <span class="label label-info">Info</span>
          case Levels.Warning => <span class="label label-warning">Warning</span>
          case Levels.Error => <span class="label label-danger">Error</span>
        }}<div class="snippet">
          {warning.snippet.orNull}
        </div>
        </div>
    }
  }

  def generate(reporter: Reporter) = <html>
    {header}{body(reporter)}
  </html>
}
