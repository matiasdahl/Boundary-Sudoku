import FullBorderExtras._
import scala.util.Random
import org.scalatest._

class FullBorderSpec extends FlatSpec with Matchers {

  "split171" should "split (1, ..., 9) correctly" in {
    val (l, mid, r) = (1 to 9).toArray.split171
    assert(l==1 && mid.toList == List(2, 3, 4, 5, 6, 7, 8) && r == 9)
  }
    
}
