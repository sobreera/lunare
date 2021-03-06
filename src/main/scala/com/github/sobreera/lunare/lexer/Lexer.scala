package com.github.sobreera.lunare.lexer

import com.github.sobreera.lunare.compiler.{LexerError, Location}

import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.Positional

object Lexer extends RegexParsers {
  override def skipWhitespace: Boolean = true
  override protected val whiteSpace: Regex = "[ \t\n\r\f]+".r

  def apply(code: String): Either[LexerError, List[Token]] =
    parse(tokens, code) match {
      case NoSuccess(msg, next) =>
        Left(LexerError(Location(next.pos.line, next.pos.column), msg))
      case Success(result, _) => Right(result)
    }

  def tokens: Parser[List[Token]] = {
    phrase(rep1(stringLiteral | intLiteral
        | lpar  | rpar  | lbrc  | rbrc
        | plus  | minus | times | div
        | comma | func
        | identifier)
    )
  }

  private def token[T <: Positional](str: String, returnToken: T): Parser[T] = positioned { str ^^ (_ => returnToken) }

  def lpar: Parser[LPAR] = token("(", LPAR())
  def rpar: Parser[RPAR] = token(")", RPAR())
  def lbrc: Parser[LBRC] = token("{", LBRC())
  def rbrc: Parser[RBRC] = token("}", RBRC())

  def not: Parser[NOT] = token("!", NOT())

  def and: Parser[AND] = token("&&", AND())
  def equ: Parser[EQU] = token("==", EQU())
  def neq: Parser[NEQ] = token("!=", NEQ())
  def leq: Parser[LEQ] = token("<=", LEQ())
  def geq: Parser[GEQ] = token(">=", GEQ())
  def lt: Parser[LT] = token("<", LT())
  def gt: Parser[GT] = token(">", GT())

  def plus: Parser[PLUS] = token("+", PLUS())
  def minus: Parser[MINUS] = token("-", MINUS())
  def times: Parser[TIMES] = token("*", TIMES())
  def div: Parser[DIV] = token("/", DIV())

  def comma: Parser[COMMA] = token(",", COMMA())

  def func: Parser[DEF] = token("def", DEF())

  def stringLiteral: Parser[STRING_LITERAL] = positioned {
    "\".*?\"".r ^^ { value => STRING_LITERAL(value) }
  }

  def intLiteral: Parser[INT_LITERAL] = positioned {
    minus.? ~ "\\d+".r ^^ {
      case None ~ value => INT_LITERAL(value.toInt)
      case _    ~ value => INT_LITERAL(-1 * value.toInt)
    }
  }

  def identifier: Parser[IDENTIFIER] = positioned {
    "[a-zA-Z_][a-zA-Z0-9_]*".r ^^ { value =>
      IDENTIFIER(value)
    }
  }
}
