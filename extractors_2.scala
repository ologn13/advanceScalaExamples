object extractors_2 extends App {
 
  def greetings(name: String) = name match {
    case Names(firstName, lastName, _*) => println("Case 1: Well, Hello " + firstName + " " + lastName + "!")
    case GivenNames(firstName, _*) => println("Case 2: Well, Hello " + firstName + "!")
    case _ => println("Case 3: Enter your name Master :) ")
  }
  
  greetings("  ")
  
}

//Extractor with variables number of parameters
object GivenNames{
  def unapplySeq(name: String): Option[Seq[String]] = {
    val names = name.trim.split(" ") // trim removes ending blank spaces
    if(names.forall(_.isEmpty)) None else Some(names)
    // forall(p: A => Boolean) returns true only if p(x) holds true for all elements of given collection
    // exists(p: A => Boolean) returns true if p(x) holds true for at least 1 element of given collection
  }
}

//Extractor with both fixed and variable number of parameters. For e.g Given name must have a firstname and lastname, rest doesn't matter.
object Names{
  def unapplySeq(name: String): Option[(String,String,Seq[String])] = {
    val names = name.trim.split(" ")
    if(names.size < 2) None
    else Some((names.head,names.last,names.drop(1).dropRight(1))) // firstName = names.head , lastName = names.last
    // drop(n) selects all elements except first n ones and returns a string
    // dropRight(n) selects last n elements
  }
}