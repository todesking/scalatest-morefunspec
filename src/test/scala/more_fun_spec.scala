package com.todesking.scalatest_morefunspec

import org.scalatest.Matchers._

class MoreFunSpecSpec extends MoreFunSpec {
  class IntStack {
    private[this] var elms = List.empty[Int]
    def push(i: Int): Unit = elms = i :: elms
    def pop(): Unit = elms = elms.tail
    def peek: Int = elms.head
    def isEmpty = elms.isEmpty
  }

  describe("IntStack") {
    describeSubject("when empty")(new IntStack) { subject =>
      it("should be empty") { subject() should be('empty) }
      describe("then pop()") {
        def theOperation() = subject().pop()
        it("should throw exception") { an[Exception] should be thrownBy { theOperation() } }
      }
      describe("then add a element") {
        before { subject().push(1) }
        it("should not be empty") { subject() shouldNot be('empty) }
        describe("then peek") {
          val top = let { subject().peek }
          it("should return top of stack") { top() shouldEqual 1 }
        }
        describe("then pop()") {
          before { subject().pop() }
          it("should be empty again") { subject() should be('empty) }
        }
      }
    }
  }
}
