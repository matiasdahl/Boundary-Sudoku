import org.scalatest._
import Board._
import BoardIndex._
import BoardEntry._
import BoardGenerators._
import scala.util.Random

class StandardStringSpec extends FlatSpec with Matchers {

  "fromStandardString " should "work correctly on one specific input" in {
    val inTxt = "647128359" + 
                "2.......6" + 
                "3.......8" + 
                "9.......1" + 
                "7.......4" + 
                "1.......5" + 
                "8.......7" + 
                "5.......3" + 
                "491857632"
    val expectedBoard = List(
           List(6,4,7,1,2,8,3,5,9),
           List(2,0,0,0,0,0,0,0,6),
           List(3,0,0,0,0,0,0,0,8),
           List(9,0,0,0,0,0,0,0,1),
           List(7,0,0,0,0,0,0,0,4),
           List(1,0,0,0,0,0,0,0,5),
           List(8,0,0,0,0,0,0,0,7),
           List(5,0,0,0,0,0,0,0,3),
           List(4,9,1,8,5,7,6,3,2))
    assert(expectedBoard == Board.fromStandardString(inTxt).toIteratedList)
  }
  
  it should "be inverse of toStandardString for random boards" in {
    for (board <- randomBoards()) {
      assert(board == Board.fromStandardString(board.toStandardString))
    }
  }

  it should "fail on strings unless length is 9x9" in {
    val txt = randomBoard().toStandardString
    val txt2 = txt + txt
    for (inLen <- 0 to txt2.length) {
      if (inLen != 9 * 9) {
        an[AssertionError] should be thrownBy {
          Board.fromStandardString(txt2.substring(0, inLen))
        }
      }
    }
  }

  it should "fail on input strings with interior hints" in {
    for (boardTxt <- randomBoardsWithInteriors()) {
      an[AssertionError] should be thrownBy Board.fromStandardString(boardTxt)
    }
  }

}
