package com.todesking.scalatest_morefunspec

trait MoreFunSpec extends org.scalatest.FunSpec {
  import org.scalatest.{Tag, Status, Args}

  // definition-time
  private[this] var subjectStack = Seq.empty[SubjectAccess[_]]
  // test-time
  private[this] var currentSubjects = new ThreadLocal[Seq[SubjectAccess[_]]]

  class SubjectAccess[A](val generate: () => A) {
    val store = new ThreadLocal[Any]

    def reload(): Unit =
      store.set(generate())

    def apply(): A =
      store.get.asInstanceOf[A]
  }

  protected def let[A](generate: => A): SubjectAccess[A] = {
    val access = new SubjectAccess[A](() => generate)
    subjectStack :+= access
    access
  }

  protected def scoping(fun: => Unit) = {
    val oldStack = subjectStack
    fun
    subjectStack = oldStack
  }

  protected def withSubject[A](subject: => A)(fun: SubjectAccess[A] => Unit): Unit = {
    val generator = {() => subject}
    val access = new SubjectAccess[A](generator)
    scoping {
      subjectStack :+= access
      fun(access)
    }
  }

  class ItWordWithSubject extends ItWord {
    override def apply(specText: String, testTags: Tag*)(testFun: => Unit) {
      val subjects = subjectStack
      super.apply(specText, testTags:_*) {
        subjects.foreach(_.reload())
        currentSubjects.set(subjects)
        testFun
      }
    }
  }

  protected override val it = new ItWordWithSubject

  protected def describeSubject[A](description: String)(newSubject: => A)(fun: SubjectAccess[A] => Unit): Unit = {
    withSubject(newSubject) { subject =>
      describe(description) {
        fun(subject)
      }
    }
  }

  protected override def describe(description: String)(fun: => Unit) {
    scoping {
      super.describe(description)(fun)
    }
  }

  protected def before(fun: => Unit): Unit =
    let(fun)

  // test-time method
  protected def challange(n: Int)(fun: Int => Unit): Unit = {
    (1 to n).foreach { i =>
      currentSubjects.get.foreach(_.reload())
      fun(i)
    }
  }

}
