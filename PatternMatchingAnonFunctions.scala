/*Pattern Matching Anonymous Functions*/
object PatternMatchingAnonFunctions extends App {
  val songTitles = List("The White Hare", "Childe the Hunter", "Take no Rogues")
  val x = songTitles.map(t => t.toLowerCase) // where t => t.toLowerCase is anonymous function. Can also be written in normalized form _.toLowerCase
  println(x)
  
  /*Filtering tuples using 2nd parameter and returning list of first element of filtered pairs*/
  //Words with their frequencies.
  val wordFrequencies = ("habitual", 6) :: ("and", 56) :: ("consuetudinary", 2) ::
  ("additionally", 27) :: ("homely", 5) :: ("society", 13) :: Nil
  
  def wordfilter1(wordPairsList: Seq[(String,Int)]) = wordPairsList.filter(w => w._2 > 3 && w._2 < 25) map(_._1) //Quite tedious to write ._1 , ._2
  
  /*Now using pattern matching in anonymous functions*/
  def wordfilter2(wordPairsList: Seq[(String,Int)]) = wordPairsList.filter {case (_, w) => w > 3 && w < 25} map {case(z,_) => z} // where {} is pattern matching anonymous function block
  
  println(wordfilter1(wordFrequencies))
  println(wordfilter2(wordFrequencies))
  
  /*Or we can define explicitly
   * val predicate: (String, Int) => Boolean = { case (_, f) => f > 3 && f < 25 }
   * val transformFn: (String, Int) => String = { case (w, _) => w }
   * and the use wordPairsList.filter(predicate) map (transfromFn)
   * */
}

