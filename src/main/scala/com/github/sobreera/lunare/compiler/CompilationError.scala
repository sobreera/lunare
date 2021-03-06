package com.github.sobreera.lunare.compiler

sealed trait CompilationError

case class LexerError(location: Location, msg: String) extends CompilationError
case class ParserError(location: Location, msg: String) extends CompilationError
case class BCodeCompilerError(msg: String) extends CompilationError

case class Location(line: Int, column: Int) {
  override def toString: String = s"$line:$column"
}