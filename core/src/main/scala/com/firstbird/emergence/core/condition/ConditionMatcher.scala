package com.firstbird.emergence.core.condition

private[condition] trait ConditionMatcher[A <: Condition, B] {
  def matches(condition: A, input: B): MatchResult
}

private[condition] object ConditionMatcher {
  def of[A <: Condition, B](f: (A, B) => MatchResult): ConditionMatcher[A, B] = new ConditionMatcher[A, B] {
    override def matches(condition: A, input: B): MatchResult = f(condition, input)
  }

  object syntax extends syntax

  trait syntax {
    implicit class ConditionMatcherOps[A <: Condition, B](underlying: A) {
      def matches(input: B)(implicit m: ConditionMatcher[A, B]): MatchResult = m.matches(underlying, input)
    }
  }

}
