package com.sksamuel.scapegoat

import java.io.InputStream

import com.sksamuel.scapegoat.inspections._
import com.sksamuel.scapegoat.inspections.collections._
import com.sksamuel.scapegoat.inspections.empty._
import com.sksamuel.scapegoat.inspections.equality.{ComparingFloatingPointTypes, ComparingUnrelatedTypes, ComparisonWithSelf}
import com.sksamuel.scapegoat.inspections.math._
import com.sksamuel.scapegoat.inspections.option.{EitherGet, OptionGet, OptionSize}
import com.sksamuel.scapegoat.inspections.string._
import com.sksamuel.scapegoat.inspections.style.{IncorrectlyNamedExceptions, ParameterlessMethodReturnsUnit}
import com.sksamuel.scapegoat.inspections.unneccesary._
import com.sksamuel.scapegoat.inspections.unsafe._

/** @author Stephen Samuel */
object ScapegoatConfig extends App {

  private def inspections: Seq[Inspection] = Seq(
    new ArraysToString,
    new AsInstanceOf,
    new BigDecimalDoubleConstructor,
    new BrokenOddness,
    new CatchNpe,
    new CollectionNamingConfusion,
    new CollectionNegativeIndex,
    new CollectionPromotionToAny,
    new ComparingFloatingPointTypes,
    new ComparingUnrelatedTypes,
    new ComparisonWithSelf,
    new ConstantIf,
    new DivideByOne,
    new DuplicateMapKey,
    new EitherGet,
    new EmptyCatchBlock,
    new EmptyIfBlock,
    new EmptyInterpolatedString,
    new EmptyMethod,
    new EmptySynchronizedBlock,
    new EmptyTryBlock,
    new ExpressionAsStatement,
    new FilterHeadOption,
    new FilterIsEmpty,
    new FilterOptionAndGet,
    new FilterSize,
    new FindIsDefined,
    new IllegalFormatString,
    new IncorrectlyNamedExceptions,
    new IncorrectNumberOfArgsToFormat,
    new InvalidRegex,
    new IsInstanceOf,
    new JavaConversionsUse,
    new ListSize,
    new ModOne,
    new NanComparison,
    new NullUse,
    new OptionGet,
    new OptionSize,
    new ParameterlessMethodReturnsUnit,
    new PreferSeqEmpty,
    new PreferSetEmpty,
    new RedundantFinalizer,
    new TraversableHead,
    new TryGet,
    new UnnecessaryIf,
    new UnnecessaryReturnUse,
    new UnsafeContains,
    new UnusedMethodParameter,
    new UseSqrt,
    new VarUse,
    new WhileTrue)

  private def load(is: InputStream): Seq[String] = scala.io.Source.fromInputStream(is).getLines().toSeq
  lazy private val disabled = Option(getClass.getResourceAsStream("/disabled_inspections")).map(load).getOrElse(Nil)
  def enabledInspections = inspections.filterNot(disabled.contains)
}
