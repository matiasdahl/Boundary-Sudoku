import org.scalatest._
import BoardEntry._

class BoardEntrySpec extends FlatSpec with Matchers {

  "Encoding 0, .., 9" should "produce '.', '1', ..., '9' and the encoding should be be invertible" in {

    def digits = List[BoardEntry](0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    def chars = List[Char]('.', '1', '2', '3', '4', '5', '6', '7', '8', '9')
  
    val digits2 = chars.map(char => BoardEntry.decodeChar(char))    
    val chars2 = digits.map(digit => BoardEntry.encodeToChar(digit))    
    assert((chars == chars2) && (digits == digits2))
  }
  
  "encodeToChar" should "should fail on digits < 0" in {
    an [AssertionError] should be thrownBy BoardEntry.encodeToChar(-1)
  }

  it should "should fail on digits > 9" in {
    an [AssertionError] should be thrownBy BoardEntry.encodeToChar(10)
  }

  "decodeChar" should "should fail on char 'x'" in {
    an [AssertionError] should be thrownBy BoardEntry.decodeChar('x')
  }
 
  it should "should fail on newline" in {
    an [AssertionError] should be thrownBy BoardEntry.decodeChar('\n')
  }

  it should "should fail on space" in {
    an [AssertionError] should be thrownBy BoardEntry.decodeChar(' ')
  }

  it should "should fail on '0'" in {
    an [AssertionError] should be thrownBy BoardEntry.decodeChar('0')
  }

}
