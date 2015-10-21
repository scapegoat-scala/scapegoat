# Writing custom inspections

Scapegoat supports custom inspections, for example to guard against project- or library-specific
bugs.

## Build setup

Your inspections need to be compiled before the code in the rest of your project is
compiled (like macros).

Inspections are detected by `scapegoat` using the ServiceLoader API. You must add
the fully-qualified class name of your Inspection to a resource file named
`META-INF/services/com.sksamuel.scapegoat.Inspection`.

See the instructions in the
[sbt-scapegoat](https://github.com/sksamuel/sbt-scapegoat) Readme for setting up
an SBT build with custom inspections.

To use them manually, see the options help.

## Existing examples

Take a look at the source code for
[the inspections which are included in the scalac-scapegoat-plugin project](https://github.com/sksamuel/scalac-scapegoat-plugin/tree/master/src/main/scala/com/sksamuel/scapegoat/inspections).

## Testing custom inspections

You will generally need to add the `org.scalatest.OneInstancePerTest` trait to your `scalatest` tests,
or call `isolated` in the constructor of your `Specs2` tests. (This is because the compiler uses
global state.)

Take a look at the source code for
[the inspection tests which are included in the scalac-scapegoat-plugin project](https://github.com/sksamuel/scalac-scapegoat-plugin/tree/master/src/test/scala/com/sksamuel/scapegoat/inspections).

