# ScalaTest-MoreFunSpec: FunSpec + Subjects and RSpec like features

## Current status

* Under construction

## Usage

```scala
libraryDependencies += "com.todesking" %% "scalatest-morefunspec" % "0.0.0" % "test"
```

```scala
import com.todesking.scalatest.morefunspec.MoreFunSpec

class SeqSpec extends MoreFunSpec with org.scalatest.Matchers {
  describe("Seq") {
    describe("when empty") {
      subject { Seq() }

      it("size should 0") {
        // Refer the subject by call apply()
        subject().size shouldEqual 0
      }

      // RSpec's one-liner syntax like feature
      // (ref: https://relishapp.com/rspec/rspec-core/v/3-1/docs/subject/one-liner-syntax )
      it shouldBe empty

      // Above is same as this:
      it("should be empty") { subject().shouldBe empty }

      describe("when a element added") {
        // Subject can be nested
        // Can refer to outer subject
        subject { subject() :+ 1 }

        it should have size(1)
      }
    }

    describe("+") {
      val seq1 = let { Seq(1, 2, 3) }
      val seq2 = let { Seq(4, 5, 6) }
      subject { seq1() + seq2() }

      it should have size(6)
      it shouldEqual Seq(1, 2, 3, 4, 5, 6)
    }
  }
}
```
