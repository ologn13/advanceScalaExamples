object errorHandling extends App {
  case class Customer(age: Int)
  class Cigarettes
  case class UnderAgeException(message: String) extends Exception(message)

  def buyCigarettes(customer: Customer): Cigarettes =
    if (customer.age < 16)
      throw UnderAgeException(s"Customer must be older than 16 but was ${customer.age}")
    else new Cigarettes

  val youngCustomer = Customer(15)
  // Exceptions thrown in try blocks are caught in catch block
  try {
    buyCigarettes(youngCustomer)
    println("go, cigarette man, go !!")
  } catch {
    case UnderAgeException(msg) => println(msg)
  }

  /*Try[A] is similar in semantics to Option[A]. If code executes successfully Try[A] = Success[A] wrapping the value of type A
   * else Try[A] = Failure[A], wrapping a Throwable, i.e. an exception or other kind of error.
   * 
   */
  import scala.util.Try
  import java.net.URL

  def parseURL(url: String): Try[URL] = Try(new URL(url)) // we could also use partialFuncion ,i.e. Try{new URL(url)}

  /*To achieve this, we are using the apply factory method on the Try companion object. This method expects a by-name parameter of type A 
   *(here, URL). For our example, this means that the "new URL(url)" is executed inside the apply method of the Try object.  
   *Inside that method, non-fatal exceptions are caught, returning a Failure containing the respective exception
   * 
   */

  println(parseURL("http://www.google.com")) // prints Success(http://www.google.com), an instance of Success[String] class
  println(parseURL("garbage")) // prints Failure(java.net.MalformedURLException: no protocol: garbage), can be removed by adding http://

  //Working with Try Values is similar to Option. 
  /*We can check if a Try is a success by calling isSuccess on it and then conditionally retrieve the wrapped value 
   * by calling get on it. But there aren’t many situations where we will want to do that.
     It’s also possible to use getOrElse to pass in a default value to be returned if the Try is a Failure:
  * */

  //URL redirection can be done using getOrElse
  val url = parseURL(Console.readLine("URL: ")) getOrElse new URL("http://www.google.com") // here google.com is the fallback URL
  println(url)

  //Chaining Operations on Try similar to Option

  //1.flatMap and map
  val x = parseURL(Console.readLine("URL: ")).map(_.getProtocol()) // x is of type Try[String]
  println(x) // Prints Success(URL) on success and Failure(java.net.MalformedURLException: no protocol: ddvjdvk) on failure

  //chaining multiple map operations results in nested Try structure, which is not recommended to use.
  import java.io.InputStream
  def InputStreamForURL(url: String): Try[Try[Try[InputStream]]] = parseURL(url).map { u => Try(u.openConnection).map(conn => Try(conn.getInputStream)) }

  //We resolve this awkward situation using flatMap
  def IPStream(url: String): Try[InputStream] = parseURL(url).flatMap { u => Try(u.openConnection).flatMap(conn => Try(conn.getInputStream)) }

  //2. filter and foreach
  def parseHttpURL(url: String) = parseURL(url).filter(_.getProtocol == "http")
  parseHttpURL("http://apache.openmirror.de") // results in a Success[URL]

  parseHttpURL("ftp://mirror.netcologne.de/apache.org") // results in a Failure[URL]

  parseHttpURL("http://danielwestheide.com").foreach(println) // Here, only one URL hence only one time println will be called

  //For Comprehensions -- equivalent to flatMap
  import scala.io.Source
  def getURLContent(url: String): Try[Iterator[String]] =
    for {
      url <- parseURL(url)
      connection <- Try(url.openConnection())
      is <- Try(connection.getInputStream)
      source = Source.fromInputStream(is)
    } yield source.getLines() // A good way to do this is Source.fromURL(url) , which includes by default everything like opening conn, etc.
   
   //Pattern Matching on Try
    import scala.util.Success
    import scala.util.Failure
    
    getURLContent("http://www.google.com") match {
      case Success(lines) => lines.foreach(println)
      case Failure(ex) => println(s"Problem rendering URL content: ${ex.getMessage}") //getMessage from an exception
    }
    
    //Recovering from a failure
    import java.net.MalformedURLException
    import java.io.FileNotFoundException
    
    val content = getURLContent("garbage") recover {
    case e: FileNotFoundException => Iterator("Requested page does not exist")
    case e: MalformedURLException => Iterator("Please make sure to enter a valid URL")
    case _ => Iterator("An unexpected error has occurred. We are so sorry!")
    }
    content.get.foreach(println) 
    /*
    recover expects a partial function and returns another Try. If recover is called on a Success instance, 
    that instance is returned as is. Otherwise, if the partial function is defined for the given Failure instance, 
    its result is returned as a Success.
    * 
    */
    
    //recoverWith and transform are two other functions on Try. Take a look here :
    //http://www.scala-lang.org/archives/downloads/distrib/files/nightly/docs/library/index.html#scala.util.Try
    
    
}