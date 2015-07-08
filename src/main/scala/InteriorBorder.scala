import BoardEntry._
import scala.language.implicitConversions

class InteriorBorderExtras(a7: Array[BoardEntry]) {
  assert(a7.length == 7)

  /** Split (2, 3, 4, 5, 6, 7, 8) -> ((2, 3), (4, 5, 6), (7, 8)) */
  def split232: (Array[BoardEntry], Array[BoardEntry], Array[BoardEntry]) = {
    (a7.slice(0, 2), a7.slice(2, 5), a7.slice(5, 7))
  }

  /** Test if the tuples and the triple returned by split232 are ordered */
  def isOrdered232: Boolean = {
    def isSorted2(a: Array[BoardEntry]): Boolean = a(0) < a(1)
    def isSorted3(a: Array[BoardEntry]): Boolean = a(0) < a(1) && a(1) < a(2)

    val (left2, mid3, right2) = this.split232
    isSorted2(left2) && isSorted3(mid3) && isSorted2(right2)
  }

  /**
   *  Permute `another` so that another.isOrdered232 == true, and apply the same
   *  permutation to the interior border represented by `a7` (=this).
   */
  def sort232Along(another: Array[BoardEntry]): (Array[BoardEntry], Array[BoardEntry]) = {
    val (a1, a2, a3) = this.split232
    val (b1, b2, b3) = (new InteriorBorderExtras(another)).split232

    def sortBySecond(arr1: Array[BoardEntry], arr2: Array[BoardEntry]): (Array[BoardEntry], Array[BoardEntry]) = {
      val (r1, r2) = (arr1 zip arr2).sortBy(_._2).unzip
      (r1.toArray, r2.toArray)
    }

    val (a1permuted, b1sorted) = sortBySecond(a1, b1)
    val (a2permuted, b2sorted) = sortBySecond(a2, b2)
    val (a3permuted, b3sorted) = sortBySecond(a3, b3)
    (a1permuted ++ a2permuted ++ a3permuted, b1sorted ++ b2sorted ++ b3sorted)
  }

  /**
   *  Permutation of entries. The parameter `p` represents the permutation of 
   *  the InteriorBorder. For example,
   *
   *     p = (0, 1, 2, 3, 4, 5, 6)
   *
   *  represents the identity permutation of the 7 entries.
   *
   *  Note: The method aborts if the permutation breaks the (2, 3, 2)-division. 
   *  This division corresponds to the 3x3 block structure of Sudoku boards. 
   *  For example, in a Sudoku one can permute (exchange) the second and third 
   *  row and obtain another Sudoku, but one might not be able to exchange the 
   *  second and, say, 8th row.
   */
  def permute232(p: Array[Int]): Array[BoardEntry] = {
    assert(p.toSet == (0 to 6).toSet)
    val (result: Array[BoardEntry]) = p.map(x => a7(x))

    // check that the permutation does not break the (2, 3, 2)-division.
    val (left2, mid3, right2) = split232
    val (pleft2, pmid3, pright2) = (new InteriorBorderExtras(result)).split232 // explicit conversion
    assert(left2.toSet == pleft2.toSet &&
      mid3.toSet == pmid3.toSet &&
      right2.toSet == pright2.toSet)

    result
  }

}

object InteriorBorderExtras {

  type InteriorBorder = Array[BoardEntry]

  implicit def ArrayToIntBorder(arr7: Array[BoardEntry]) = new InteriorBorderExtras(arr7)

}

import InteriorBorderExtras._
