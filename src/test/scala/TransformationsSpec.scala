import org.scalatest._
import Board._
import BoardGenerators._

class TransformationsSpec extends FlatSpec with Matchers {

  "MirrorVertical" should "give the expected output for genericBoard" in {
    val expectedRows = List(
      List(25, 24, 23, 22, 21, 20, 19, 18, 17),
      List(26, 0, 0, 0, 0, 0, 0, 0, 16),
      List(27, 0, 0, 0, 0, 0, 0, 0, 15),
      List(28, 0, 0, 0, 0, 0, 0, 0, 14),
      List(29, 0, 0, 0, 0, 0, 0, 0, 13),
      List(30, 0, 0, 0, 0, 0, 0, 0, 12),
      List(31, 0, 0, 0, 0, 0, 0, 0, 11),
      List(32, 0, 0, 0, 0, 0, 0, 0, 10),
      List(1, 2, 3, 4, 5, 6, 7, 8, 9))
    assert(genericBoard.mirrorVertical.toIteratedList == expectedRows)
  }

  it should "satisfy mirrorVertical^2 = identity" in {
    assert(genericBoard.mirrorVertical.mirrorVertical == genericBoard)
  }

  "MirrorAntiDiagonal" should "give the expected output for genericBoard" in {
    val expectedRows = List(
      List(17, 16, 15, 14, 13, 12, 11, 10, 9),
      List(18, 0, 0, 0, 0, 0, 0, 0, 8),
      List(19, 0, 0, 0, 0, 0, 0, 0, 7),
      List(20, 0, 0, 0, 0, 0, 0, 0, 6),
      List(21, 0, 0, 0, 0, 0, 0, 0, 5),
      List(22, 0, 0, 0, 0, 0, 0, 0, 4),
      List(23, 0, 0, 0, 0, 0, 0, 0, 3),
      List(24, 0, 0, 0, 0, 0, 0, 0, 2),
      List(25, 26, 27, 28, 29, 30, 31, 32, 1))
    assert(genericBoard.mirrorAntiDiagonal.toIteratedList == expectedRows)
  }

  it should "satisfy mirrorAntiDiagonal^2 = identity" in {
    assert(genericBoard.mirrorAntiDiagonal.mirrorAntiDiagonal == genericBoard)
  }

  /** assert that that 'board' has top row 123456789 and is otherwise empty. */
  def verify(board: Board) {
    assert(board.row(0).toList == (1 to 9).toList &&
      board.row(8).toSet == Set(0) &&
      board.column(0).tail.toSet == Set(0) &&
      board.column(8).tail.toSet == Set(0))
  }

  "withNewTopRow" should "work on an empty board" in {
    val top19 = emptyBoard.withNewTopRow((1 to 9).toArray)
    verify(top19)
  }

  "withNewBottomRow" should "work on an empty board" in {
    val top19 = emptyBoard.withNewBottomRow((1 to 9).toArray)
      .mirrorVertical
    verify(top19)
  }

  "withNewRightSide" should "work on an empty board" in {
    val top19 = emptyBoard.withNewRightSide((1 to 9).toArray)
      .mirrorVertical
      .mirrorAntiDiagonal
    verify(top19)
  }

  "withNewLeftRow" should "work on an empty board" in {
    val top19 = emptyBoard.withNewLeftSide((1 to 9).toArray)
      .mirrorVertical
      .mirrorAntiDiagonal
      .mirrorVertical
    verify(top19)
  }
  
  "permuteMidColumns" should "work for a specific permutation" in {
     val p: Array[Int] = Array(1,0, 4,2,3, 6,5)
     val genPerm = genericBoard.permuteMidColumns(p)
     val expectedRows = List(
           List( 1, 3, 2, 6, 4, 5, 8, 7, 9),
           List(32, 0, 0, 0, 0, 0, 0, 0,10),
           List(31, 0, 0, 0, 0, 0, 0, 0,11),
           List(30, 0, 0, 0, 0, 0, 0, 0,12),
           List(29, 0, 0, 0, 0, 0, 0, 0,13),
           List(28, 0, 0, 0, 0, 0, 0, 0,14),
           List(27, 0, 0, 0, 0, 0, 0, 0,15),
           List(26, 0, 0, 0, 0, 0, 0, 0,16),
           List(25,23,24,20,22,21,18,19,17))
    assert(genPerm.toIteratedList == expectedRows)
  }
  
  it should "abort only if the (2,3,2)-structure is broken" in {
    val perms = (0 to 6).toArray.permutations
        
    for (p <- perms) {
      def preserves232(p: Array[Int]): Boolean = {
        Set(p(0), p(1)) == Set(0, 1) &&
        Set(p(2), p(3), p(4)) == Set(2, 3, 4) &&
        Set(p(5), p(6)) == Set(5, 6) 
      }    

      if (preserves232(p)) {
        // permutations that preserve the (2,3,2)-structure should not fail
        try {
          val tmp = genericBoard.permuteMidColumns(p)
        } catch {
          case _: AssertionError => fail() 
        }
      } else {
        // permutation that breaks the (2,3,2) structure should fail. For 
        // example, if it exchanges the first and last column. This should yield 
        // an exception. There is no guarantee such a permutation yields another
        // valid Sudoku board
        an[AssertionError] should be thrownBy {
          val tmp = genericBoard.permuteMidColumns(p)
        }
      }
    }
  }

}
