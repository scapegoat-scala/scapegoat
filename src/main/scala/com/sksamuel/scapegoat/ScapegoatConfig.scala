package com.sksamuel.scapegoat

import com.sksamuel.scapegoat.inspections._

/** @author Stephen Samuel */
object ScapegoatConfig extends App {
  def inspections: Seq[Inspection] = Seq(
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
    new DuplicateMapKey,
    new EitherGet,
    new EmptyCatchBlock,
    new EmptyIfBlock,
    new EmptyMethod,
    new EmptySynchronizedBlock,
    new EmptyTryBlock,
    new ExpressionAsStatement,
    new FilterHeadOption,
    new FilterIsEmpty,
    new FilterSize,
    new IllegalFormatString,
    new IncorrectlyNamedExceptions,
    new IncorrectNumberOfArgsToFormat,
    new InvalidRegex,
    new IsInstanceOf,
    new JavaConversionsUse,
    new ModOne,
    new IncorrectlyNamedExceptions,
    new NullUse,
    new OptionGet,
    new OptionSize,
    new ParameterlessMethodReturnsUnit,
    new PreferSeqEmpty,
    new PreferSetEmpty,
    new RedundantFinalizer,
    new ReturnUse,
    new TraversableHead,
    new TryGet,
    new UnncessaryIf,
    new UnsafeContains,
    new UnusedMethodParameter,
    new UseExistsNotFindAndIsDefined,
    new VarUse,
    new WhileTrue)
  //  def names: Seq[String] = {
  //    val conf = ConfigFactory.load()
  //    conf.getObject("scapegoat").keySet.asScala.toSeq
  //  }
  //  def instance(name: String): Inspection = Class.forName(name).newInstance.asInstanceOf[Inspection]
  //  def inspections: Seq[Inspection] = names.map(instance)
}
