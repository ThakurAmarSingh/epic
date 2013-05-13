package epic.util

/**
 * Optional is just like [[scala.Option]], except that we offer implicit
 * conversion from the Optional to Provided. Mostly for use in optional argument lists
 * @author dlwh
 */
sealed trait Optional[+A] {
  def get: A
  def isEmpty: Boolean = this eq NotProvided
  def size : Int = if(isEmpty) 0 else 1

  def map[B](f: A=>B): Optional[B] = this match {
    case Provided(x) => Provided(f(x))
    case NotProvided => NotProvided
  }

  def foldLeft[B](b:B)(f: (B,A) => B) = if(isEmpty) b else f(b, get)

  def fold[B](ifSome: A => B , ifNone: => B) = if(isEmpty) ifNone else ifSome(get)
}

case class Provided[+A](get: A) extends Optional[A] {
}

case object NotProvided extends Optional[Nothing] {
  def get = throw new NoSuchElementException("NotProvided.get")
}


object Optional extends LowPriorityOptionalImplicit {
  implicit def liftOption[A](o: Option[A]) = o match {
    case Some(a) => Provided(a)
    case None => NotProvided
  }

}

sealed trait LowPriorityOptionalImplicit {
  implicit def liftAnything[A](a: A) = if(a == null) NotProvided else Provided(a)
}