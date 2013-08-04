
/*Futures are read-only and we work with the computed value in Future and thus there must be something that write value in Future.
 * Here comes the Promise !!
 * */

import concurrent.Future
import concurrent.Promise
import concurrent.future
import concurrent.ExecutionContext.Implicits.global
import scala.util.{ Success, Failure }
import concurrent.Await
import scala.concurrent.duration._

object Promises extends App {
  case class TaxCut(reduction: Int)
  // Promises are made by politicians :D , to either fulfill(or complete) in future or get failed(failure) at this.

  //Two ways to create Promise object:
  //1. either give the type as a type parameter to the factory method:
  val taxcut = Promise[TaxCut]()
  //2. or give the compiler a hint by specifying the type of your val:
  val taxcut2: Promise[TaxCut] = Promise()

  //We can get the corresponding Future belonging to a promise by calling future method on it.
  val taxcutF: Future[TaxCut] = taxcut.future
  val taxcutF2: Future[TaxCut] = taxcut.future

  println(taxcutF == taxcutF2)
  //Promise and its corresponding future have one-to-one relation.

  taxcutF onComplete {
    case Success(TaxCut(reductions)) => println(s"tax cut by $reductions")
    case Failure(ex) => println(ex.getMessage)
  }

  taxcutF2 onComplete {
    case Success(TaxCut(reductions)) => println(s"tax cut by $reductions")
    case Failure(ex) => println(ex.getMessage)
  }

  //Once promise is fulfilled its corresponding Future is written either with success or failure, no matter where promise is completed in code.
  taxcut.success(TaxCut(20)) // Promise completed(or fulfilled)

  //Await.ready executes in main thread, hence use Try to continue executing the code if Future doesn't returns the result
  Await.ready(taxcutF, 10 seconds) // Await blocks the threads for futures to get the Future result without moving forward
  Await.ready(taxcutF2, 10 seconds) // Time Out exception as taxcut2 promise is not fulfilled hence taxcutF2 is not written and hence no result.

  /*Usually, the completion of the Promise and the processing of the completed Future will not happen in the same thread. It’s more likely 
   * that you create your Promise, start computing its value in another thread and immediately return the uncompleted Future to the caller.
   * For e.g. as below: 
   * */
  object Government {
    def redeemCampaignPledge(): Future[TaxCut] = {
      val p = Promise[TaxCut]() //promise creation
      future { // different thread from caller's thread
        println("Starting the new legislative period.")
        Thread.sleep(2000)
        p.success(TaxCut(20)) // p is completed here in separate thread and hence p.future contains the result now.
        // for failure: p.failure(LameExcuse("global economy crisis")) , where case class LameExcuse(msg: String) extends Exception(msg)
        println("We reduced the taxes! You must reelect us!!!!1111")
      }
      p.future // uncompleted Future is returned
    }
  }

  val taxcutF3: Future[TaxCut] = Government.redeemCampaignPledge()
  taxcutF3.onComplete {
    case Success(TaxCut(reduction)) =>
      println(s"A miracle! They really cut our taxes by $reduction percentage points!")
    case Failure(ex) =>
      println(s"They broke their promises! Again! Because of a ${ex.getMessage}")
  }

  Await.ready(taxcutF3, 10 seconds)

  /*For manually fixing ExecutionContext for a particular piece of asynchronous code like call to webserver every 3 min always,
   * import java.util.concurrent.Executors
     import concurrent.ExecutionContext
     val executorService = Executors.newFixedThreadPool(4)
     val executionContext = ExecutionContext.fromExecutorService(executorService)
   * */

}