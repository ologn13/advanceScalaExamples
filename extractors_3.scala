object extractors_3 extends App {
  /*
   *  An identifier that begins with a lowercase letter in a pattern represents a new variable that will be bound if the pattern matches.
   *  For eg. Player(_,Score) would give error if Score is not an already declared variable.
   */
  
  
  def message(player: Player) = player match {
    case Player(_,core) if core > 90 => "Go, Get a job!!" 
    case Player(ame, _) => "please Mr.'/Ms. " + ame + ",go to home!"
  }
  
  def printmessage(player: Player) = println(message(player))
  
  val test1 = Player("Vikrant", 100)
  val test2 = Player("Rahul", 89)
  
  printmessage(test1)
  printmessage(test2)
  
  /*Patterns in Value Definitions. Here, patterns are on left side of definitions*/
  def getPlayer() = Player("Vikrant",95)
  
  val Player(ame,_) = getPlayer() // Make sure the pattern always matches the RHS otherwise there would be a runtime exception.
  println("Player name is " + ame)
  
  /*Using pattern matching in tuples*/
  def gameResult() = ("Vikrant",89)
  val (me,core) = gameResult()
  println(me + " " + core)
  
}

case class Player(name: String, score: Int)