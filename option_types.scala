// Option[A] either returns Some(A) else None
// Some(A) can also be written as Option(A) and None can also be written as Option(null)
object option_types extends App {

  //First Way -- Not so good. Chances of forgetting isDefined method
  val user1 = UserRepository.findById(1)
  if (user1.isDefined) {
    println(user1.get.firstName)
  }

  //Use of getOrElse -- if get value is None, check for else part
  val user2 = user(3, "Diana", "Penty", 20, None)
  println("Gender: " + user2.gender.getOrElse("Gender Not Specified")) // getOrElse accepts parameter by Name and not by Value.
  //user2.gender.get would simply extract the string out of Some(String) or return None.

  //Instead of getOrElse method, we can also use pattern matching since Some is a case class.
  user2.gender match {
    case Some(gender) => println("Gender is: " + gender)
    case None => println("Gender not specified")
  }

  //Performing Side-Effects like foreach, etc. on Option[A]
  UserRepository.findById(1).foreach(user => println(user.firstName)) // foreach don't require get method to extract value from Option[A] container.

  //Mapping Option[A] to Option[B] much like List[A] to List[B]
  val age = UserRepository.findById(1).map(_.age)
  println(age) // age is Some(18) since map on option returns an Option[B] type, where B is type of mapped value.

  val gender = UserRepository.findById(1).map(_.gender) // Option[Option[String]] , where type B = Option[String] i.e. type of mapped value.
  println(gender)

  //to get rid of above chain of options we can use flatMap
  val gender1 = UserRepository.findById(1).flatMap(_.gender)
  println(gender1)

  //Difference between map and flatMap on options
  val names: List[Option[String]] = List(Some("Vikrant"), None, Some("Natalia"))
  println(names.map(_.map(_.toUpperCase)))
  println(names.flatMap(_.map(_.toUpperCase)))

  //filtering an option
  UserRepository.findById(1).filter(_.age > 19) // None, because age is <= 19
  UserRepository.findById(2).filter(_.age > 18) // Some(user), because age is > 30
  UserRepository.findById(3).filter(_.age > 30) // None, because user is already None

  //For Comprehensions -- equivalent to flatMap
  val x = for {
    user <- UserRepository.findAll
    gender <- user.gender
  } yield gender

  println(x)

  //Or we can use pattern matching on left side of generator
  val y = for {
    user(_, _, _, _, Some(gender)) <- UserRepository.findAll
  } yield gender
  
  println(y)
  
  //Chaining Options, much like partial functions
  case class Resource(content: String)
  val resourceFromConfigDir: Option[Resource] = None
  val resourceFromClasspath: Option[Resource] = Some(Resource("I was found on the classpath"))
  val resource = resourceFromConfigDir orElse resourceFromClasspath
  /**This is usually a good fit if you want to chain more than just two options – if you simply want to provide a default value
   *  in case a given option is absent, the getOrElse method may be a better idea.
   */
}

case class user(
  id: Int,
  firstName: String,
  lastName: String,
  age: Int,
  gender: Option[String] // Option(A)
  )

object UserRepository {
  private[this] val users = Map(1 -> user(1, "Vikrant", "Yadav", 18, Some("male")),
    2 -> user(2, "Sarah", "Johnson", 19, None))
  def findById(id: Int): Option[user] = users.get(id) // get returns Option[A]
  def findAll = users.values //Function values on Map returns an iterable(a kind of Pseudo Seq) of mapped elements
}