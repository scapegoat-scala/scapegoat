package com.sksamuel.scapegoat

/**
 * @author
 *   Stephen Samuel
 */
sealed trait Level {
  protected def weight: Short

  def higherOrEqualTo(other: Level): Boolean = this.weight >= other.weight
}

object Levels {

  /**
   * Errors indicate code that is potentially unsafe or likely to lead to bugs.
   *
   * An example is use of nulls. Use of nulls can lead to NullPointerExceptions and should be avoided.
   */
  case object Error extends Level {
    override protected def weight: Short = 30
  }

  /**
   * Warnings are reserved for code that has bad semantics. This by itself does not necessarily mean the code
   * is buggy, but could mean the developer made a mistake or does not fully understand the contructs or best
   * practice.
   *
   * An example is an expression as a statement. While this is perfectly legal, it could indicate that the
   * developer meant to assign the result to or otherwise use it.
   *
   * Another example is a constant if. You can do things like if (true) { } if you want, but since the block
   * will always evaluate, the if statement perhaps indicates a mistake.
   */
  case object Warning extends Level {
    override protected def weight: Short = 20
  }

  /**
   * Infos are used for code which is semantically fine, but there exists a more idomatic way of writing it.
   *
   * An example would be using an if statement to return true or false as the last statement in a block. Eg,
   *
   * {{{
   * def foo = {
   *   if (a) true else false
   * }
   * }}}
   *
   * Can be re-written as
   *
   * def foo = a
   */
  case object Info extends Level {
    override protected def weight: Short = 10
  }

  case object Ignore extends Level {
    override protected def weight: Short = 0
  }

  def fromName(name: String): Level =
    name.toLowerCase() match {
      case "error"   => Error
      case "warning" => Warning
      case "info"    => Info
      case "ignore"    => Ignore
      case _         => throw new IllegalArgumentException(s"Unrecognised level '$name'")
    }
}
