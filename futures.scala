object futures extends App {
  import scala.concurrent.Future
  import scala.concurrent.future
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._
  import scala.util.Random
  import scala.concurrent.Await

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

  /*def future[T](body: => T)(implicit execctx: ExecutionContext): Future[T] in scala API
   * ExecutionContext is something that can execute our future, like thread pool. It is by default global.
   * So remaining body is the only input we have to provide.
   * */

  //The following functions are executed concurrently and return Future immediately instead of computing their result in blocking way.

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

  //callbacks for simple situations
  //Input parameter for onSuccess and onFailure is the computed value
  //Input parameter for onComplete is a Try which is matched with Success/Failure pattern.
  val x = grind("arabica beans")
  val y = grind("baked beans")
  import scala.util.{ Success, Failure }
  val z = grind("baked beans")
  import scala.util.Try
  
   println("main thread: " + Thread.currentThread().getId())


  x.onComplete { 
    case Success(ground) => println(s"got my $ground " + Thread.currentThread.getId)
    case Failure(ex) => println("This grinder needs a replacement, seriously! " + Thread.currentThread.getId)
  }
  
  y.onComplete {
    case Success(ground) => println(s"got my $ground " + Thread.currentThread.getId)
    case Failure(ex) => println("This grinder needs a replacement, seriously! " + Thread.currentThread.getId)
  }
  
  z.onComplete {
    case Success(ground) => println(s"got my $ground " + Thread.currentThread.getId)
    case Failure(ex) => println("This grinder needs a replacement, seriously! " + Thread.currentThread.getId)
  }
  
  println("main thread: " + Thread.currentThread.getId)
  
  val r1 = Try(Await.result(x, 10 seconds))
  val r2 = Try(Await.result(y, 10 seconds))
  val r3 = Try(Await.result(z, 10 seconds))
}