package com.sksamuel.scapegoat

import com.sksamuel.scapegoat.inspections._
import com.sksamuel.scapegoat.inspections.collections._
import com.sksamuel.scapegoat.inspections.controlflow.WhileTrue
import com.sksamuel.scapegoat.inspections.empty._
import com.sksamuel.scapegoat.inspections.equality.{ComparingFloatingPointTypes, ComparingUnrelatedTypes, ComparisonWithSelf}
import com.sksamuel.scapegoat.inspections.inference.ProductWithSerializableInferred
import com.sksamuel.scapegoat.inspections.matching.{PartialFunctionInsteadOfMatch, RepeatedCaseBody, RepeatedCasePattern}
import com.sksamuel.scapegoat.inspections.math._
import com.sksamuel.scapegoat.inspections.option.{EitherGet, ImpossibleOptionSizeCondition, OptionGet, OptionSize}
import com.sksamuel.scapegoat.inspections.string._
import com.sksamuel.scapegoat.inspections.style.{IncorrectlyNamedExceptions, ParameterlessMethodReturnsUnit, SimplifyBooleanExpression}
import com.sksamuel.scapegoat.inspections.unneccesary._
import com.sksamuel.scapegoat.inspections.unsafe._

/** @author Stephen Samuel */
object ScapegoatConfig extends App {

  def inspections: Seq[Inspection] = Seq(
    new ArraysInFormat,
    new ArraysToString,
    new AvoidSizeEqualsZero,
    new AvoidSizeNotEqualsZero,
    new AsInstanceOf,
    new BigDecimalDoubleConstructor,
    new BrokenOddness,
    new CatchNpe,
    new CatchThrowable,
    new CollectionNamingConfusion,
    new CollectionNegativeIndex,
    new CollectionPromotionToAny,
    new ComparingFloatingPointTypes,
    new ComparingUnrelatedTypes,
    new ComparisonWithSelf,
    new ConstantIf,
    new DivideByOne,
    new DuplicateMapKey,
    new DuplicateSetValue,
    new EitherGet,
    new EmptyCaseClass,
    new EmptyCatchBlock,
    new EmptyIfBlock,
    new EmptyInterpolatedString,
    new EmptyMethod,
    new EmptySynchronizedBlock,
    new EmptyTryBlock,
    new ExistsSimplifableToContains,
    new ExpressionAsStatement,
    new FilterDotHead,
    new FilterDotHeadOption,
    new FilterDotIsEmpty,
    new FilterOptionAndGet,
    new FilterDotSize,
    new FinalizerWithoutSuper,
    new FindDotIsDefined,
    new IllegalFormatString,
    new IncorrectlyNamedExceptions,
    new IncorrectNumberOfArgsToFormat,
    new IntToInt,
    new InvalidRegex,
    new ImpossibleOptionSizeCondition,
    new IsInstanceOf,
    new JavaConversionsUse,
    new ListAppend,
    new ListSize,
    new ModOne,
    new NanComparison,
    new NegationIsEmpty,
    new NegationNonEmpty,
    new NullUse,
    new OptionGet,
    new OptionSize,
    new ParameterlessMethodReturnsUnit,
    new PartialFunctionInsteadOfMatch,
    new PreferSeqEmpty,
    new PreferSetEmpty,
    new ProductWithSerializableInferred,
    new RedundantFinalizer,
    new RepeatedCaseBody,
    new SimplifyBooleanExpression,
    new SubstringZero,
    new SwapSortFilter,
    new TraversableHead,
    new TryGet,
    new UnnecessaryIf,
    new UnnecessaryReturnUse,
    new RepeatedCasePattern,
    new UnsafeContains,
    new UnusedMethodParameter,
    new UseSqrt,
    new VarUse,
    new WhileTrue,
    new ZeroNumerator)

}
