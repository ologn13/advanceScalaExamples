import scala.concurrent.Future
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random
import scala.concurrent.Await

object composingFutures extends App {

  type CoffeeBeans = String
  type GroundCoffee = String
  case class Water(temperature: Int)
  type Milk = String
  type FrothedMilk = String
  type Espresso = String
  type Cappuccino = String

  case class GrindingException(msg: String) extends Exception(msg)
  case class FrothingException(msg: String) extends Exception(msg)
  case class WaterBoilingException(msg: String) extends Exception(msg)
  case class BrewingException(msg: String) extends Exception(msg)

  def grind(beans: CoffeeBeans): Future[GroundCoffee] = future {
    println("..starts grinding.." + Thread.currentThread.getId)
    Thread.sleep(2000)
    if (beans == "baked beans") throw GrindingException("are you joking?")
    println("..finished grinding.." + Thread.currentThread.getId)
    s"ground coffee of $beans"
  }

  def heatWater(water: Water): Future[Water] = future {
    println("heating the water now")
    Thread.sleep(Random.nextInt(2000))
    if (water.temperature > 90) throw WaterBoilingException("Heater Exploded!!")
    println("hot, it's hot!")
    water.copy(temperature = 85)
  }

  def frothMilk(milk: Milk): Future[FrothedMilk] = future {
    println("milk frothing system engaged!")
    Thread.sleep(Random.nextInt(2000))
    println("shutting down milk frothing system")
    s"frothed $milk"
  }

  def brew(coffee: GroundCoffee, heatedWater: Water): Future[Espresso] = future {
    println("happy brewing :)")
    Thread.sleep(Random.nextInt(2000))
    println("it's brewed!")
    "espresso"
  }

  def combine(espresso: Espresso, frothedMilk: FrothedMilk): Cappuccino = "cappuccino"
  //Since, Future is a container like Option, Try, Either, etc. it also allows us to be mapped over, use flatMap, for comprehension, etc.

  //Mapping the future : map is not called until we get the result out of heatWater(water), i.e. Future[Water]
  def temperatureOkay(water: Water): Future[Boolean] = heatWater(water).map { wt =>
    println("We are in Future")
    (80 to 85).contains(wt.temperature)
  }
  //if heatWater(water) returns a Failure then map will not be called and Future[Boolean] would contain a Failure

  val x = temperatureOkay(Water(72))

  Await.ready(x, 10 seconds)

  import scala.util.{ Success, Failure }

  x.onComplete {
    case Success(bool) => println(bool)
    case Failure(ex) => println(ex)
  }

  // Using flatMap to avoid nested return type of future, i.e. Future[Future[Boolean]]
  def tempOk(water: Water): Future[Boolean] = future {
    (80 to 85).contains(water.temperature)
  }

  val nestedFuture: Future[Future[Boolean]] = heatWater(Water(25)) map {
    water => tempOk(water)
  }

  val flattedFuture: Future[Boolean] = heatWater(Water(25)) flatMap {
    water => tempOk(water)
  }

  Await.ready(nestedFuture, 10 seconds)
  Await.ready(flattedFuture, 10 seconds)

  nestedFuture.onComplete {
    case Success(bool) => println(bool)
    case Failure(ex) => println(ex)
  }

  flattedFuture.onComplete {
    case Success(bool) => println(bool)
    case Failure(ex) => println(ex)
  }

  //For comprehensions: compute the futures outside of for{} as for{} translates to f1.flatMap{a => f2...so on}
  // So, if f1 is not completed we don't get a and hence no moving forward to f2. Therefore, it is sequential execution instead of
  // parallel execution. To make it parallel, define f1, f2 as val outside so as to compute them out and then match inside for{}

  //For e.g. , following 2 e.g. are sequential execution -- NOT RECOMMENDED
  val acceptable: Future[Boolean] = for {
    heatedWater <- heatWater(Water(25))
    ok <- tempOk(heatedWater)
  } yield ok

  def prepareCappuccinoSequentially(): Future[Cappuccino] = {
    for {
      ground <- grind("arabica beans")
      water <- heatWater(Water(20))
      foam <- frothMilk("milk")
      espresso <- brew(ground, water)
    } yield combine(espresso, foam)
  }

  //following is parallel execution and is STRONGLY RECOMMENDED
  def prepareCappuccino(): Future[Cappuccino] = {
    val groundCoffee = grind("arabica beans")
    val heatedWater = heatWater(Water(20))
    val frothedMilk = frothMilk("milk")
    val x = for {
      ground <- groundCoffee
      water <- heatedWater
      foam <- frothedMilk
      espresso <- brew(ground, water) //wait until ground and water are there
    } yield combine(espresso, foam) // wait until espresso is there
    
   val y = Await.ready(x, 10 seconds)
   x
  }
  //Failure projections
  /*You will have noticed that Future[T] is success-biased, allowing you to use map, flatMap, filter etc. under the assumption that
   *  it will complete successfully. Sometimes, you may want to be able to work in this nice functional way for the timeline in which 
   *  things go wrong. By calling the failed method on an instance of Future[T], you get a failure projection of it, which is a 
   *  Future[Throwable]. Now you can map that Future[Throwable], for example, and your mapping function will only be executed if the 
   *  original Future[T] has completed with a failure.
   * */
}