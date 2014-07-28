Scapegoat
==========

Scapegoat is a Scala static code anaylzer, what is more colloquially known as a code lint tool. Scapegoat works in a similar vein to Java's findbugs or checkstyles, or Scala's Scalastyle.

A static code anaylzer is a tool that flag suspicious langage usage in code. This can include behavior likely to lead or bugs, non idiomatic usage of a language, or just code that doesn't conform to specified style guidelines.

### Usage
Scapegoat is developed as a scala compiler plugin, which can then be used inside your build tool.

See: [sbt-scapegoat](https://github.com/sksamuel/sbt-scapegoat)

### Inspections

The currently implemented descriptions are as follows.

|Name|Description|
|----|-----------|
| As instance of | todo |
| Broken oddness | todo |
| Catching NPE | todo |
| Collection promotion to any | todo |
| Comparing floating point types | todo |
| Comparison with self | todo |
| Constant if | todo |
| Either get | todo |
| Empty Catch Block | todo |
| Empty If Block | todo |
| Empty Synchronized Block | todo |
| Empty Method | todo |
| Empty Try Block | todo |
| Expresson as statement | todo |
| Java conversions use | todo |
| Null use | todo |
| Option Get| todo |
| Parameterless method returns unit | todo |
| Unused method parameter | todo |
| Var use | todo |

