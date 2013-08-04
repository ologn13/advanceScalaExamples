object patternsPlusForComprehensions extends App {
  
  def gameResults(): Seq[(String,Int)] = ("Vikrant", 3500) :: ("Rahul", 13000) :: ("Dps", 7000) :: Nil
  
  /*For Comprehension is different from for loop.
   * See http://stackoverflow.com/a/3754568/1663386 for a good understanding of what for-comprehension translates to.
   * */
  
  /*Three representations of same process*/
  //First --> Translates to Third
  def hallOfFame = for { // for comprehension with curly braces for easy readability
    result <- gameResults()
    (name,score) = result
    if(score > 5000)
  } yield name
  println(hallOfFame)
  
  //Second
  def hallFame = for {
    (name,score) <- gameResults // LHS <- RHS ; <- is generator and LHS is the pattern
    if(score > 5000)
  } yield name
  println(hallFame)
  
  //Third 
  val x = gameResults.flatMap{case (a,b) => if(b > 5000) Seq(a) else Seq()}
  println(x)
  
  
  
  
  /*Filtering Using generator <-
   * As a general rule , if a pattern on the left side of a generator does not match, the respective element is filtered out.
   * */
  val lists = List(1, 2, 3) :: List.empty :: List(5, 3) :: Nil
  /*Now, we'll filter the empty list*/
  /* "@" notation is very handy*/
  val y = for{
    list @ (head :: _) <- lists //or parenthesis can be removed. ::(xs: List[]) is an extractor accepting List as arg and returning Option[(Int,List)]
  } yield list.size
  println(y)
  
  val z = lists.flatMap{x => if(x.isEmpty) Seq() else Seq(x.size)}
  println(z)
  
}