import BoardEntry._
import BoardIndex._
import FullBorderExtras._
import InteriorBorderExtras._

class Board(corner0: BoardEntry,       // top-left corner
            top: InteriorBorder,       // top 7 mid-entries
            corner1: BoardEntry,       // top-right corner
            left: InteriorBorder,      // left 7 mid-entries
            right: InteriorBorder,     // right 7 mid-entries
            corner2: BoardEntry,       // bottom-left corner
            bottom: InteriorBorder,    // bottom 7 mid-entries
            corner3: BoardEntry)       // bottom-right corner
            {

  assert(top.length == 7)
  assert(right.length == 7)
  assert(bottom.length == 7)
  assert(left.length == 7)

  /** Note: possible 0:s/entries in the interior are not included in the below list. */
  def allBorderEntries: List[BoardEntry] = {
    List(corner0, corner1, corner2, corner3) ++ (top ++ left ++ right ++ bottom).toList
  }

  override def equals(other: Any): Boolean = other match {
    case p: Board => this.allBorderEntries.sameElements(p.allBorderEntries)
    case _        => false
  }

  override def hashCode = this.allBorderEntries.toList.hashCode

  /** Mainly used to print boards when tests fail. */
  override def toString: String = this.toIteratedList.toString

  /**
   *  Output board as a standard string listing entries row-wise. 
   *  This assumes entries have digits 0, 1, ..., 9 and will generate 
   *  an assert-error for generalBoard.
   */
  def toStandardString: String = {
    BoardIndex.rows
      .flatMap(rowIndex => this.row(rowIndex).toList)
      .map((digit: BoardEntry) => encodeToChar(digit))
      .mkString("")
  }

  /**
   *  Return the entry at row (0, ..., 8) and column (0, ..., 8).
   */
  def entryAt(row: BoardRow, column: BoardColumn): BoardEntry = {
    assert(row >= 0 && row <= 8 && column >= 0 && column <= 8)
    (row, column) match {
      case (0, 0) => corner0
      case (0, 8) => corner1
      case (8, 0) => corner2
      case (8, 8) => corner3
      case (0, c) => top(c - 1)     // c != 0, 8
      case (8, c) => bottom(c - 1)
      case (r, 0) => left(r - 1)    // r != 0, 8
      case (r, 8) => right(r - 1)
      case (_, _) => 0              // interior point
    }
  }

  def row(row: BoardRow): Array[BoardEntry] = BoardIndex.columns.map(c => entryAt(row, c))

  def column(column: BoardColumn): Array[BoardEntry] = BoardIndex.rows.map(r => entryAt(r, column))

  def toIteratedList: List[List[BoardEntry]] = BoardIndex.rows.toList.map(r => row(r).toList)

  /**
   *  isValid is used to exclude boards that cannot be valid. It will return false 
   *  if any of the below conditions are met
   * 
   *    - the same digit appears twice on same row/same column,
   *    - any of the corner 3x3 blocks contains the same digit twice.
   *   
   *  (Zeros represent empty positions and these can of course appear twice.)
   *   
   *  isValid==true does not guarantee that the board has a unique solution/any 
   *  solutions.
   */
  def isValid: Boolean = {
    
    // No row/column should contain the same digit twice
    def noRowColPairs: Boolean = {
      def pairIsOK(in: (BoardEntry, BoardEntry)): Boolean = {
        in match {
          case (0, _) => true
          case (_, 0) => true
          case (a, b) => a != b
        }
      }
      
      def arrayPairIsOK(array1: Array[BoardEntry], array2: Array[BoardEntry]): Boolean = {
        (array1 zip array2).filter(pair => !pairIsOK(pair)).isEmpty
      }
      arrayPairIsOK(column(0), column(8)) && arrayPairIsOK(row(0), row(8))
    }
    
    // No 3x3 block should contain the same digit twice
    def cornersOK: Boolean = {
      def validateCorner(in: Array[BoardEntry],
                         corner: BoardEntry,
                         out: Array[BoardEntry]): Boolean = {
        assert(in.length == 2 && out.length == 2)
        val entries: Array[BoardEntry] = (in ++ Array(corner) ++ out).filter(x => x != 0)
        entries.toSet.size == entries.length
      }
  
      val (topLeft, _, topRight) = top.split232
      val (leftTop, _, leftBottom) = left.split232
      val (rightTop, _, rightBottom) = right.split232
      val (bottomLeft, _, bottomRight) = bottom.split232
      validateCorner(leftTop, corner0, topLeft) &&
        validateCorner(topRight, corner1, rightTop) &&
        validateCorner(leftBottom, corner2, bottomLeft) &&
        validateCorner(bottomRight, corner3, rightBottom) 
    }
    
    noRowColPairs && cornersOK
  }

  /**
   *  Note: The entries in `p` refers to the mid-columns on the board so that 
   *  p(i)=0 refers to the second column.
   */
  def permuteMidColumns(p: Array[Int]): Board = {
    val topPermuted = top.permute232(p)
    val bottomPermuted = bottom.permute232(p)
    new Board(corner0, topPermuted, corner1, left, right, corner2, bottomPermuted, corner3)
  }

  /** 
   *  By permutations of the columns one can generate 24 = 2*6!*2 variants of a board.
   *  All of these will have the top row "123456789". If there is symmetry some of these
   *  might coincide.
   */
  def allNormalForms: Iterator[Board] = {
    for (
      p23 <- Array(0, 1).permutations;
      p456 <- Array(2, 3, 4).permutations;
      p78 <- Array(5, 6).permutations
    ) yield {
      permuteMidColumns(p23 ++ p456 ++ p78).toNormalForm
    }
  }

  /** 
   *  A board is by definition in **normal form** if the entries are such 
   *  that the top row is "123456789" and rows (2,3), rows (4,5,6) and rows (7,8)
   *  are such that right.isOrdered232 == true. That is:
   *  
   *     right(0) < right(1)
   *     right(2) < right(3) < right(4)
   *     right(5) < right(6)
   *     
   *  Any board can be put into normal form.
   */
  def isInNormalForm: Boolean = (this.row(0).toList == (1 to 9).toList) && right.isOrdered232

  /** Relabel the entries so that the first row is 123456789 */
  def normalizeTopRow: Board = {
    assert(row(0).toSet == (1 to 9).toSet)

    // create a map that maps 0 -> 0 and the first row to (1, ..., 9). See
    //  http://stackoverflow.com/questions/3323651/scala-how-to-build-an-immutable-map-from-a-collection-of-tuple2s
    val mapRelation = row(0).toList zip (1 to 9).toList
    val mapRelation0 = mapRelation ::: List((0, 0)) // add 0 -> 0
    val perm = Map(mapRelation0: _*)

    new Board(perm(corner0), top.map(perm), perm(corner1), 
              left.map(perm), right.map(perm), 
              perm(corner2), bottom.map(perm), perm(corner3))
  }

  def normalizeRowOrder: Board = {
    val (leftPermuted, rightSorted) = left.sort232Along(right)
    new Board(corner0, top, corner1, leftPermuted, rightSorted, corner2, bottom, corner3)
  }

  /** Note: normalizeTopRow and normalizeRowOrder need to be applied in the below order. */
  def toNormalForm: Board = this.normalizeTopRow.normalizeRowOrder

  /**
   *  Mirror entries over the vertical (|)-axis
   *
   *    0 1   =>   2 3
   *    2 3        0 1
   */
  def mirrorVertical: Board = {
    new Board(corner2, bottom, corner3, left.reverse, right.reverse, corner0, top, corner1)
  }

  /**
   *  Mirror entries over the '/'-diagonal.
   *
   *    0 1   =>   3 1
   *    2 3        2 0
   */
  def mirrorAntiDiagonal: Board = {
    new Board(corner3, right.reverse, corner1, bottom.reverse, top.reverse, corner2, left.reverse, corner0)
  }

  def withNewTopRow(newTop: FullBorder): Board = {
    Board.canReplaceBorders(row(0), newTop)
    val (newCorner0, newTop7, newCorner1) = newTop.split171
    new Board(newCorner0, newTop7, newCorner1, left, right, corner2, bottom, corner3)
  }

  def withNewRightSide(newRight: FullBorder): Board = {
    Board.canReplaceBorders(column(8), newRight)
    val (newCorner1, newRight7, newCorner3) = newRight.split171
    new Board(corner0, top, newCorner1, left, newRight7, corner2, bottom, newCorner3)
  }

  def withNewBottomRow(newBottom: FullBorder): Board = {
    Board.canReplaceBorders(row(8), newBottom)
    val (newCorner2, newBottom7, newCorner3) = newBottom.split171
    new Board(corner0, top, corner1, left, right, newCorner2, newBottom7, newCorner3)
  }

  // not used in the search
  def withNewLeftSide(newLeft: FullBorder): Board = {
    Board.canReplaceBorders(column(0), newLeft)
    val (newCorner0, newLeft7, newCorner2) = newLeft.split171
    new Board(newCorner0, top, corner1, newLeft7, right, newCorner2, bottom, corner3)
  }

}

object Board {

  /** Generate board where all entries are zero (no hints). */
  def emptyBoard: Board = {
    val list70 = Array.fill(7)(0: BoardEntry)
    new Board(0, list70, 0, list70, list70, 0, list70, 0)
  }

  /**
   *  Sanity check used by 'withNewBottomRow' etc above. Check if `oldBorder` can
   *  be replaced by `newBorder`. If everything works as expected we should not
   *  replace full borders, and the old and new borders should match at the first
   *  and last entries (or be zero/empty). The new border need not be a valid board.
   */
  def canReplaceBorders(oldBorder: FullBorder, newBorder: FullBorder): Unit = {
    assert(newBorder.length == 9)
    assert(newBorder.toSet == (1 to 9).toSet)
    val (old1a, old7, old1b) = oldBorder.split171
    val (new1a, new7, new1b) = newBorder.split171
    assert(old7.toSet == Set(0))
    if (old1a != 0) assert(old1a == new1a)
    if (old1b != 0) assert(old1b == new1b)
  }

  /**
   *  Take a string listing all entries in a Sudoku row-wise and return a
   *  Board. Such a string has 9*9=81 characters with no new lines.
   *  
   *  Note: it is assumed that the input Sudoku has only entries on the 
   *  32 border entries.
   */
  def fromStandardString(inTxt: String): Board = {
    assert(inTxt.length == 9 * 9)

    val inputByLine: Array[String] = inTxt.grouped(9).toArray
    val arr: Array[FullBorder] = inputByLine.map(x => x.map(decodeChar).toArray)
    def inColumn(col: BoardIndex): InteriorBorder = (1 to 7).toArray.map(r => arr(r)(col))

    // assert that the interior in the text string is empty
    assert((1 to 7).flatMap(c => inColumn(c)).toSet == Set(0))

    val (inCorner0, inTop, inCorner1) = arr(0).split171
    val (inCorner2, inBottom, inCorner3) = arr(8).split171
    new Board(inCorner0, inTop, inCorner1, inColumn(0), inColumn(8), inCorner2, inBottom, inCorner3)
  }
  
}
