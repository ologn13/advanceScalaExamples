object extractors_1 extends App {
  
  def printUser(user: Users){
    user match {
      case freeUser @ premiumCandidate() => println("Free User who's now premium account: " + freeUser.name)
      case SpamUser(str) => println(str)
      case FreeUser(name, score,id) => println("Name: " + name + " Score: " + score + " id: "+ id)
      case PremiumUser(name,id,score) => println("Name: " + name + " id: " + id + "Score: " + score)
      case _ => println("No Case Retrieved")
    }
  }
  
  def printFreeUser(user: FreeUser){
    user match {
      case FreeUser(name) => println("Name: " + name)
      case _ => println("No Case Retrieved")
    }
  }
  
  printUser(new FreeUser("Vikrant", 17,27))
  printUser(new PremiumUser("Vikrant", 18,28))

}

trait Users {
  def name: String
  def score: Int
  def id: Int
}

class FreeUser(val name: String, val score: Int, val id: Int) extends Users
class PremiumUser(val name: String, val score: Int,val id: Int) extends Users

/* Companion Objects */
object FreeUser {
  def unapply(user: FreeUser): Option[(String, Int, Int)] = {
    Some(user.name, user.score, user.id)
  }
}

object PremiumUser {
  def unapply(user: PremiumUser): Option[(String, Int, Int)] = {
     Some(user.name,user.id,user.score)
  }
}

object SpamUser{
  def unapply(user: FreeUser): Option[String] = Some("Spam is required for " + user.name)
}

object premiumCandidate{
  def unapply(user: FreeUser): Boolean = user.score > 15
}



