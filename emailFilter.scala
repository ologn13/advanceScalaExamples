// The higher-order functions have 3 forms:- 
/*1. One or more of its parameters is a function, and it returns some value
 *2. It returns a function, but none of its parameters is a function.
 *3. Both of the above: One or more of its parameters is a function, and it returns a function
 * 
 * map , filter, flatMap are higher-order functions of 1st type above.
 * */

object emailFilter extends App {

  case class Email(
    subject: String,
    text: String,
    sender: String,
    recipient: String)

  type EmailFilter = Email => Boolean // Predicate, which when true makes the email to be accepted.

  def newMailsForUser(mails: Seq[Email], f: EmailFilter) = mails.filter(f)

  //Configuring Email Filters to the user's liking
  val sentByOneOf: Set[String] => EmailFilter =
    senders => email => senders.contains(email.sender)
  //above can also be written as:
  // val sentByOneOf = (senders: Set[String]) => (email: Email) => senders.contains(email.sender)
  val notSentByAnyOf: Set[String] => EmailFilter = senders => email => !senders.contains(email.sender)
  val minimumSize: Int => EmailFilter = n => email => email.text.size >= n
  val maximumSize: Int => EmailFilter = n => email => email.text.size <= n

  val emailFilter: EmailFilter = notSentByAnyOf(Set("johndoe@example.com"))
  val mails = Email(
    subject = "It's me again, your stalker friend!",
    text = "Hello my friend! How are you?",
    sender = "johndoe@example.com",
    recipient = "me@example.com") :: Nil
  newMailsForUser(mails, emailFilter) // returns an empty list

  //Now, we'll follow DRY(Don't Repeat Yourself principle)
  //i.e. Reusing existing functions
  type SizeChecker = Int => Boolean
  val SizeConstraint: SizeChecker => EmailFilter = f => email => f(email.text.size)

  val minSize: Int => EmailFilter = n => SizeConstraint(_ >= n)
  val maxSize: Int => EmailFilter = n => SizeConstraint(_ <= n)

  //Functional Composition:-
  /*Given two functions f and g, f.compose(g) returns a new function that, when called, will first call g 
   * and then apply f on the result of it. Similarly, f.andThen(g) returns a new function that, when called, will apply g to the result of f.
   * */
  // let us define a complement function to delve into the cases sentByOneOf and notSentByOneOf
  def complement[A](predicate: A => Boolean) = (a: A) => !predicate(a)
  //However, sentByAnyOf is not a predicate, but it returns one, namely an EmailFilter
  val notSentByAnyof = sentByOneOf andThen (g => complement(g)) // gof , andThen (complement(_)) is also correct , andThen(complement[A]) also 
  // works.

  //Composing Predicates: Adding multiple filters in emailfilter
  //for any , none, every of predicates hold true:
  def any[A](predicates: (A => Boolean)*): A => Boolean = a => predicates.exists(pred => pred(a))
  def none[A](predicates: (A => Boolean)*): A => Boolean = complement(any(predicates: _*))
  def every[A](predicates: (A => Boolean)*): A => Boolean = none(predicates.view.map(complement(_)): _*)

  val filter: EmailFilter = every(
    notSentByAnyOf(Set("johndoe@example.com")),
    minSize(100),
    maxSize(10000))

  // Composing a transformation pipeline:-
  val addMissingSubject = (email: Email) =>
    if (email.subject.isEmpty) email.copy(subject = "No subject")
    else email

  val checkSpelling = (email: Email) =>
    email.copy(text = email.text.replaceAll("your", "you're"))

  val removeInappropriateLanguage = (email: Email) =>
    email.copy(text = email.text.replaceAll("dynamic typing", "**CENSORED**"))

  val addAdvertismentToFooter = (email: Email) =>
    email.copy(text = email.text + "\nThis mail sent via Super Awesome Free Mail")

  val pipeline = Function.chain(Seq( //We could also had used andThen instead of chaining
    addMissingSubject,
    checkSpelling,
    removeInappropriateLanguage,
    addAdvertismentToFooter))
    
   //Lifting partial Functions
    //pf.lift converts pf to Function defined for all input values returning Option container.
    //Function.unlift(f) convers function to pf.
}