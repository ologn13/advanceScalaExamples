/*Refer to map and filter in PatternMatchingAnonFunctions
 * map and filter accepts a function of type Function1[A,B](also written as (A) => B)
 * Inheritance: PartialFunction[-A,+B] extends type (A) => B 
 * Pattern Matching Anonymous Functions are of type PartialFunctions, defined only for certain input values.
 * Thus Pattern Matching Anonymous Function passed to map or filter must match every case, otherwise there would be a MatchError.
 * 
 * 
 * The filtering and mapping in PatternMatchingAnonFunctions.scala can be done in other way using partial function.
 * 
 * def collect[B](f: PartialFunction[A,B]) meet out the requirement. This method returns a new sequence by
 * applying the given partial function to all of its elements
 * 
 * The partial function is applied( hence, method apply()) to all the elements of the collection for which it is defined(hence, method 
 * isDefinedAt). Hence, there is no possibility of MatchError.
 * 
 * In scala API ,
 * trait PartialFunction[-A,+B] extends Function1[A,B] {
 *  def apply(x : A) : B //Apply the body of this function to the argument.
 *  def isDefinedAt(x: A): Boolean //Checks if a value is contained in the function's domain
 * }
 * 
 * */
object partialFunctions extends App {
  
 val wordFrequencies = ("habitual", 6) :: ("and", 56) :: ("consuetudinary", 2) ::
  ("additionally", 27) :: ("homely", 5) :: ("society", 13) :: Nil
  
 val pf: PartialFunction[(String, Int), String] = {
   case(word, freq) if freq > 3 && freq < 25 => word  // Pattern Matching Anonymous Function
 }
 
 /*Without pattern matching anonymous function, we can also declare explicity as follows but it would be quite large in size
  * 
  * val pf = new PartialFunction[(String,Int),Int]{
  *  def apply(wordFreq: (String,Int)) = {
  *   case(word, freq) if(freq > 3 && freq < 25) => word
  *  }
  *  def isDefinedAt((wordFreq: (String, Int))) = {
  *   case (word, freq) if freq > 3 && freq < 25 => true
  *   case _ => false
  *  }
  * }
  * 
  * */
 
 // Now wordFrequencies.map(pf) or flatMap(pf) or filter(pf), etc. will compile fine but throw a runtime MatchError as pf is not defined for all
 // possible input values
 
 val ans = wordFrequencies.collect(pf)
 println(ans)
 
 /*The benefit of using collect over separate filter and map is that it selects word at the same time while filtering, hence, saving CPU cycles.
  * Whereas in separate filter and map , first a filtered seq is returned and that it is mapped, which is costly and also, not concise.
  * */
 // In single Line , wordFrequencies.collect { case (word, freq) if freq > 3 && freq < 25 => word }
 
 /*Chaining partial functions.
  * Method orElse is also very crucial with syntax as follows
  * 
  * def orElse(that: PartialFunction[A,B]): PartialFunction[A,B]
  * 
  * that is called "fallback function". If "this" is not defined at certain input that fallback function is checked and applied for it.
  * 
  * e.g. from Scala API 
  * 
  * val sample = 1 to 10
    val isEven: PartialFunction[Int, String] = {
    case x if x % 2 == 0 => x+" is even"
    }

   // the method collect can use isDefinedAt to select which members to collect
   val evenNumbers = sample collect isEven

   val isOdd: PartialFunction[Int, String] = {
   case x if x % 2 == 1 => x+" is odd"
   }

   // the method orElse allows chaining another partial function to handle
   // input outside the declared domain
  val numbers = sample map (isEven orElse isOdd)
  * 
  * 
  * */
}

