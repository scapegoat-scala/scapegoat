package org.scalescc.reporters

import scales.Coverage
import java.io.File

/** @author Stephen Samuel */
trait CoverageWriter {
  /**
   * Write the coverage report out to the given file.
   */
  def write(coverage: Coverage, dir: File)
}
