//"Partially Applied Functions"(Partial no. of parameters are applied) are different from 
//"Partially Defined Functions"(not defined for all I/P values)

object currying extends App {
  case class Email(
    subject: String,
    text: String,
    sender: String,
    recipient: String)

  type EmailFilter = Email => Boolean

  type IntPairPred = (Int, Int) => Boolean
  def SizeConstraint(pred: IntPairPred, n: Int, email: Email) = pred(email.text.size, n) // size is compared to n using pred

  val gt: IntPairPred = _ > _
  val ge: IntPairPred = _ >= _
  val lt: IntPairPred = _ < _
  val le: IntPairPred = _ <= _
  val eq: IntPairPred = _ == _

  val minimumSize: (Int, Email) => Boolean = SizeConstraint(ge, _: Int, _: Email)

  //We can also omit middle parameters
  val constr20: (IntPairPred, Email) => Boolean = SizeConstraint(_: IntPairPred, 20, _: Email)
  
  //Keeping all parameters omitted -> Changing method to Function Object
  val SizeConstraintFn: (IntPairPred, Int, Email) => Boolean = SizeConstraint _
  
  //Producing email Filters
  val min20: EmailFilter = minimumSize(20, _:Email) // or constr20(ge, _:Email)
  
  //Here comes Currying (named after Haskell Curry)!! - a cool way to utilize Partially Applied Functionality -- more than 1 list of parameters
  def sizeConstr(pred: IntPairPred)(n: Int)(email: Email): Boolean = pred(email.text.size, n)
  
  //Signature of Currying in Function Object
  val sizeConstrFn : IntPairPred => Int => Email => Boolean = sizeConstr _

  val minSize: Int => Email => Boolean = sizeConstr(ge)
  val Min20 : Email => Boolean = minSize(20)
  
  val MIN20: Email => Boolean = sizeConstrFn(ge)(20)
  
  //Currying Existing Functions
  val sum:(Int, Int) => Int = _ + _
  val sumCurried: Int => Int => Int = sum.curried
}