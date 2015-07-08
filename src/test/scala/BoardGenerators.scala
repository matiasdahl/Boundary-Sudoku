import scala.util.Random
import Board._
import BoardEntry._
import BoardIndex._
import FullBorderExtras._
import BoardGenerators._

object BoardGenerators {

  /** 
   *  Generate testing board with the following boundary entries (and 0:s in 
   *  the interior):
   * 
   *        1, 2, 3, 4, 5, 6, 7, 8, 9
   *       32                      10
   *       31                      11
   *       30                      12
   *       29                      13
   *       28                      14
   *       27                      15
   *       26                      16
   *       25,24,23,22,21,20,19,18,17
   * 
   *  Note that the encode/decode functions in BoardEntry will not 
   *  decode this board into a string. However, it is useful for testing. 
   */
  def genericBoard: Board = {
    val top = (2 to 8).toArray
    val left = (26 to 32).toArray.reverse
    val right = (10 to 16).toArray
    val bottom = (18 to 24).toArray.reverse
    new Board(1, top, 9, left, right, 25, bottom, 17)
  }

  /**
   *  Return a list of `n` boards with random entries (from 1, .., 9) on the boundary. 
   *  Each side is guaranteed to be a permutation of {1,.., 9}. However, the boards 
   *  need not be valid (isValid need not be true), or have a unique solution.
   *
   *  Adjust the value of `n` for faster/slower test suite.
   */
  def randomBoards(n: Int = 500): Iterable[Board] = {

    val perms: List[FullBorder] = (1 to 9).toArray.permutations.toList

    def randomEntry(aList: List[FullBorder]): FullBorder = {
      assert(aList.length > 0)
      aList(util.Random.nextInt(aList.length))
    }

    def randomBoard(): Board = {
      val top = randomEntry(perms)
      val right = randomEntry(perms.filter(pRight => pRight.head == top.last))
      val bottom = randomEntry(perms.filter(pBottom => (pBottom.last == right.last) &&
        (pBottom.head != top.head)))
      val left = randomEntry(perms.filter(pLeft => (pLeft.head == top.head) &&
        (pLeft.last == bottom.head)))

      Board.emptyBoard
        .withNewTopRow(top)
        .withNewRightSide(right)
        .withNewBottomRow(bottom)
        .withNewLeftSide(left)
    }
    (1 to n).map(i => randomBoard())
  }

  /** Return one random board */
  def randomBoard(): Board = randomBoards(1).head

  /** 
   *  Return a list of 7x7 = 49 strings. Each string represents a random board with an 
   *  entry set in the interior. Together the strings cover all 49 interior points. 
   */
  def randomBoardsWithInteriors(): Iterable[String] = { 
    val b = randomBoard()

    def takenAt(i: BoardRow, j: BoardColumn): String = {
      val intDigit = 1 + Random.nextInt(9) // random digit 1, ..., 9

      def getEntry(r: BoardRow, c: BoardColumn): BoardEntry = {
        if ((i, j) == (r, c)) {
          b.entryAt(r, c) + intDigit
        } else {
          b.entryAt(r, c)
        }
      }
      def row(r: BoardRow): String = BoardIndex.columns.map(c => getEntry(r, c)).mkString("")
      BoardIndex.rows.map(r => row(r)).mkString("")
    }

    for (intRow <- 1 to 7; intCol <- 1 to 7) yield takenAt(intRow, intCol)
  }
  
  /** 
   *  The square has 8 symmetries. Transform a given board `b` into these.
   */
  def transformations(b: Board): List[Board] = {
    List(b, // identity
        b.mirrorAntiDiagonal,
        b.mirrorVertical,
        b.mirrorAntiDiagonal.mirrorVertical, // rotate clockwise 90 deg
        b.mirrorVertical.mirrorAntiDiagonal, // rotate anti-clockwise 90 deg
        b.mirrorVertical.mirrorAntiDiagonal.mirrorVertical, // mirror over diagonal
        b.mirrorAntiDiagonal.mirrorVertical.mirrorAntiDiagonal, // rotate clockwise 180 deg
        b.mirrorAntiDiagonal.mirrorVertical.mirrorAntiDiagonal.mirrorVertical // rotate anti-clockwise 180 deg
        )
  }

}
