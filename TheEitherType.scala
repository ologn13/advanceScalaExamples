/*Either[A,B] is also a container like Option and Try. But here, we can get value of any type among A and B.
 * Left and Right are sub-types of Either. If value is of type A, Either is an instance of Left[A] else Right[B]
 * 
 * While using Either[A, B] as Exception Handler, by the convention, Left represents Error case(failure) and Right as Success.
 * */
import scala.io.Source
import java.net.URL

object TheEitherType extends App {
  // URL Blocker
  def getContent(url: URL): Either[String, Source] =
    if (url.getHost.contains("porn") || url.getHost.contains("sex"))
      Left("Requested URL is blocked for the good of the people!")
    else
      Right(Source.fromURL(url))

  getContent(new URL("http://hot-sex-tube.com")) match {
    case Left(msg) => println(msg)
    case Right(source) => source.getLines.foreach(println)
  }

  // Projections: Either is unbiased unlike Option(biased towards Some) and Try(biased towards Success). So, map, flatMap, etc. need projections 
  // to be acted upon, namely left projection(for acting on left instance) and right projection( for acting on right instance)

  //1. Mapping
  val content1: Either[String, Iterator[String]] =
    getContent(new URL("http://www.google.com")).right.map(_.getLines)

  val content2: Either[String, Iterator[String]] =
    getContent(new URL("http://hot-sex-tube.com")).right.map(_.getLines) // it is a left object but still applying right don't have side-effects.

  val content3: Either[Iterator[String], Source] =
    getContent(new URL("http://hot-sex-tube.com")).left.map(Iterator(_))

  val content4: Either[Iterator[String], Source] =
    getContent(new URL("http://www.google.com")).left.map(Iterator(_))

  println(content1)
  println(content2)
  println(content3)
  println(content4)

  // flatMap: To avoid nested Either inside a convoluted structure. Explained by eg. of map and flatmap
  //Calculating Avg. no. of lines on 2 web pages
  val part5 = new URL("http://t.co/UR1aalX4")
  val part6 = new URL("http://t.co/6wlKwTmu")
  val content5: Either[String, Either[String, Int]] =
    getContent(part5).right.map(src1 =>
      getContent(part6).right.map(src2 =>
        (src1.getLines.size + src2.getLines.size) / 2)) // Use joinRight / joinLeft (similar to flatten) here for getting a flatted value Either[String,Int]

  //Now using flatMap
  val content6: Either[String, Int] =
    getContent(part5).right.flatMap(src1 =>
      getContent(part6).right.map(src2 =>
        (src1.getLines.size + src2.getLines.size) / 2))

  println(content6)

  //3.For Comprehensions
  //Writing content6 using for comprehension
  val content7: Either[String, Int] =
    for {
      src1 <- getContent(part5).right
      src2 <- getContent(part6).right
    } yield ((src1.getLines.size + src2.getLines.size) / 2)

  println(content7)

  //Using toOption: if e: Either[A,B] , then e.right.toOption would return Some[B] if e is Right[] instance else None. Similar for Left[].
  //There is also an toSeq

  //fold operation returns same result type by using transforming function
  val content8: Iterator[String] =
    getContent(part5).fold(Iterator(_), _.getLines)

  //Exception Handling with Either
  case class Customer(age: Int)
  class Cigarettes
  case class UnderAgeFailure(age: Int, required: Int)
  def buyCigarettes(customer: Customer): Either[UnderAgeFailure, Cigarettes] =
    if (customer.age < 16) Left(UnderAgeFailure(customer.age, 16))
    else Right(new Cigarettes)

  //Either for processing collections
  type Citizen = String
  case class BlackListedResource(url: URL, visitors: Set[Citizen])

  val blacklist = List(
    BlackListedResource(new URL("https://google.com"), Set("John Doe", "Johanna Doe")),
    BlackListedResource(new URL("http://yahoo.com"), Set.empty),
    BlackListedResource(new URL("https://maps.google.com"), Set("John Doe")),
    BlackListedResource(new URL("http://plus.google.com"), Set.empty))

  //A BlackListedResource represents the URL of a black-listed web page plus the citizens who have tried to visit that page.

  val checkedBlacklist: List[Either[URL, Set[Citizen]]] =
    blacklist.map(resource =>
      if (resource.visitors.isEmpty) Left(resource.url)
      else Right(resource.visitors))
    
   val suspiciousResources = checkedBlacklist.flatMap(_.left.toOption)
   val problemCitizens = checkedBlacklist.flatMap(_.right.toOption).flatten.toSet
   
   println(suspiciousResources)
   println(problemCitizens)

}
 