Scapegoat
==========

Scapegoat is a Scala static code anaylzer, what is more colloquially known as a code lint tool. Scapegoat works in a similar vein to Java's findbugs or checkstyles, or Scala's Scalastyle.

A static code anaylzer is a tool that flag suspicious langage usage in code. This can include behavior likely to lead or bugs, non idiomatic usage of a language, or just code that doesn't conform to specified style guidelines.

**What's the difference between this project and Scalastyle?**

Scalastyle is a similar linting tool which focuses mostly on enforcing style/code standards. Scapegoat focuses mostly on static analysis at compile time. This means both projects are complimentary as they can detect different problems, although you might find both projects occassionally have a similar inspection.

### Usage
Scapegoat is developed as a scala compiler plugin, which can then be used inside your build tool.

See: [sbt-scapegoat](https://github.com/sksamuel/sbt-scapegoat) for SBT integration

### Screenshot

Here is a screen shot of the type of report scapegoat generates.

![screenshot](https://raw.githubusercontent.com/sksamuel/scapegoat/master/screenshot1.png)

### Inspections

The currently implemented inspections are as follows. Most of the descriptions need to be completed, but they should be self explanatary anyway from the name.

|Name|Description|
|----|-----------|
| As instance of | description needed |
| Broken oddness | description needed |
| Catching NPE | description needed |
| Collection promotion to any | description needed |
| Comparing floating point types | description needed |
| Comparison with self | description needed |
| Constant if | description needed |
| Either get | description needed |
| Empty Catch Block | description needed |
| Empty If Block | description needed |
| Empty Synchronized Block | description needed |
| Empty Method | description needed |
| Empty Try Block | description needed |
| Expresson as statement | description needed |
| Java conversions use | description needed |
| Null use | description needed |
| Option Get| description needed |
| Parameterless method returns unit | description needed |
| Unused method parameter | description needed |
| Var use | description needed |

