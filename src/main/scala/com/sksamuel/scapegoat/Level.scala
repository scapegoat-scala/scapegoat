package com.sksamuel.scapegoat

/** @author Stephen Samuel */
trait Level
object Levels {

  /**
   * Major level warnings are for code that is potentially unsafe or incorrect. Major indicates
   * that the flagged code may have buggy behaviour.
   *
   * An example is use of nulls. Use of nulls can lead to NullPointerExceptions and should be avoided.
   *
   * An Error will stop the build.
   */
  case object Error extends Level

  /**
   * Medium level warnings are reserved for code that has bad semantics or usage
   * which won't by itself cause a bug but could indicate a misunderstanding or a mistake.
   *
   * An example is an empty finally block. While this is perfectly legal, it is also pointless. It
   * could indicate the developer meant to add code to the finalizer which is missing, or it could
   * mean the developer does not understand what a finally block is for.
   *
   * A warning will not stop the build.
   */
  case object Warning extends Level

  case object Info extends Level
}