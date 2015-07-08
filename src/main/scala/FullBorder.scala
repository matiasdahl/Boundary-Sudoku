import BoardEntry._
import InteriorBorderExtras._
import scala.language.implicitConversions

class FullBorderExtras(a9: Array[BoardEntry]) {
  assert(a9.length == 9)
      
  /**
   *  Helper function to split a full border (with 9 entries) into the 2 border elements and
   *  the interior array of 7 entries. 
   * 
   *     Array(1, 2, 3, 4, 5, 6, 7, 8, 9) -> Array(1, (2, 3, 4, 5, 6, 7, 8), 9)
   * 
   */
  def split171: (BoardEntry, InteriorBorder, BoardEntry) = (a9.head, a9.slice(1, 8), a9.last)
  
}


object FullBorderExtras {  

  type FullBorder = Array[BoardEntry]

  implicit def ArrayToFullBorder(array9: Array[BoardEntry]) = new FullBorderExtras(array9)

}

import FullBorderExtras._
