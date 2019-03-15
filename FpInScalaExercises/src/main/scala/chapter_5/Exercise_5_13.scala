package chapter_5

object Exercise_5_13 {

  def unFold[A, S](z: S)(f: S => Option[(A, S)]): Stream[A] = f(z) match {
    case None => Stream.empty[A]
    case Some((a, s)) => Stream.cons(a, unFold(s)(f))
  }

  implicit class MyStreamEx[A](as: Stream[A]) {

    // todo: take, takeWhile, zipAll
    def myMap[B](f: A => B): Stream[B] = as match {
      case Stream.Empty => Stream.empty[B]
      case aHead #:: aTail => Stream.cons(f(aHead), aTail.myMap(f))
    }

    def myZipWith[B](bs: Stream[B]): Stream[(A, B)] = unFold((as, bs)) {
      case (Stream.Empty, _) => None
      case (_, Stream.Empty) => None
      case (a #:: aTail, b #:: bTail) => Some(((a, b), (aTail, bTail)))
    }

    def myTails: Stream[Stream[A]] = unFold(as) {
      case Stream.Empty => None
      case a #:: t => Some((Stream.cons(a, t), t))
    }

    def stringStream(n: Int): String = as.take(n).toList.mkString("Stream(", ",", ")") // test only method
  }

  /**
    * See the right solution from Cale Gibbard at http://lambda-the-ultimate.org/node/1277#comment-14313
    * "isSubstringOf x y = any (isPrefixOf x) (tails y)"
    */
  def hasSubsequence[A](whole: Stream[A], part: Stream[A]): Boolean = whole.myTails.exists(_.startsWith(part))

  def hasSubsequence_0[A](whole: Stream[A], part: Stream[A]): Boolean = whole match {
    case Stream.Empty =>
      false

    case _ #:: wTail =>
      if (whole.myZipWith(part).forall { case (w, p) => w == p })
        true
      else
        hasSubsequence(wTail, part)
  }

  def testMap(whole: Stream[Int]): Unit = {
    println(s"the original: ${whole.stringStream(40)}")
    println(s" after myMap: ${whole.myMap(_ + 10).stringStream(40)}")
  }

  def testTails(s: Stream[Int]): Unit = {
    println(s"the stream: ${s.stringStream(40)}")
    println(s"the tails: ${s.myTails.stringStream(40)}")
  }

  def testHasSubsequence[A](whole: Stream[A], part: Stream[A]): Unit = {
    print("hasSubsequence is ")
    print(hasSubsequence(whole, part))
    print(" for (part,whole): ")
    println(part.stringStream(20), whole.stringStream(40)
    )
  }

  def main(args: Array[String]): Unit = {
    val whole = Stream(1, 2, 3, 4, 5, 6, 7, 8, 9)
    testMap(whole)
    println
    testTails(whole)
    println
    testHasSubsequence(whole, Stream(4, 5))
    testHasSubsequence(whole, Stream(1, 2, 9, 4, 5))
  }
}
