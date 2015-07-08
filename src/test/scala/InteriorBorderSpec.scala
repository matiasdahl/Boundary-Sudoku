import Board._
import BoardEntry._
import InteriorBorderExtras._
import org.scalatest._
import scala.util.Random

class InteriorBorderSpec extends FlatSpec with Matchers {

  val permutationsOf17 = (1 to 7).toArray.permutations.toArray

  def randomPermutationOf17() = permutationsOf17(util.Random.nextInt(permutationsOf17.length))

  "split232" should "handle all permutations of {1, ..., 7}" in {
    def testPermutation(p: Array[BoardEntry]): Unit = {
      val (r1, r2, r3) = p.split232
      assert(r1.length == 2 && 
             r2.length == 3 && 
             r3.length == 2)
      assert((r1 ++ r2 ++ r3).toList == p.toList)
    }
    permutationsOf17.map(p => testPermutation(p))
  }

  "isOrdered232" should " work for some specific examples" in {
    val testTable = List(
      (Array(1, 2, 3, 4, 5, 6, 7), true),
      (Array(6, 7, 3, 4, 5, 1, 2), true),
      (Array(7, 6, 3, 4, 5, 1, 2), false),
      (Array(6, 7, 4, 3, 5, 1, 2), false),
      (Array(6, 7, 3, 5, 4, 1, 2), false),
      (Array(6, 7, 1, 2, 3, 5, 4), false),
      (Array(3, 4, 5, 6, 7, 2, 1), false),
      (Array(5, 4, 3, 2, 1, 20, 30), false),
      (Array(2, 6, 3, 4, 7, 1, 5), true))
    for ((input, result) <- testTable) assert(input.isOrdered232 == result)
  }

  "sort232Along" should "work for a specific example" in {
    val (inExtra, inToSort) =   (Array(1, 2, 3, 4, 5, 6, 7), Array(2, 1, 5, 3, 6, 7, 4))
    val (expPermd, expSorted) = (Array(2, 1, 4, 3, 5, 7, 6), Array(1, 2, 3, 5, 6, 4, 7))
    val (extraPermd, outSorted) = inExtra.sort232Along(inToSort)

    assert(outSorted.toList == expSorted.toList &&
           expPermd.toList == extraPermd.toList)
  }

  it should "be reversible by sorting twice on the different inputs" in {
    val arr = (1 to 7).toArray 
    for (p <- permutationsOf17) {
      val (permOf17thatSortP, pSorted) = arr.sort232Along(p)
      val (recoverP, recover17) = pSorted.sort232Along(permOf17thatSortP)
      
      assert(recoverP.toList == p.toList && 
             recover17.toList == arr.toList)
    }
  }

  it should "produce output with isOrdered232 == true" in {
    for (p <- permutationsOf17) {
      val (extraPermd, pSorted) = randomPermutationOf17().sort232Along(p)
      assert(pSorted.isOrdered232)
    }
  }

}
