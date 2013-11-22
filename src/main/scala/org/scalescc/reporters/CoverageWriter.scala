package org.scalescc.reporters

import scales.Coverage
import java.io.File

/** @author Stephen Samuel */
trait CoverageWriter {
  def write(coverage: Coverage, dir: File)
}
