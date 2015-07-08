import org.scalatest._
import Board._
import BoardGenerators._

class SudokuSpec extends FlatSpec with Matchers {
  
  "isValid" should "detect multiple entries in corner blocks" in {
    // not valid: lower right corner has two digits 3
    val inTxt = "647128359" + 
                "2.......6" + 
                "3.......8" + 
                "9.......1" + 
                "7.......4" + 
                "1.......5" + 
                "8.......7" + 
                "5.......3" + 
                "491857632"
    val inBoard = Board.fromStandardString(inTxt)
    for (t <- transformations(inBoard)) assert(!t.isValid)
  }
  
  it should "detect identical entries on opposite sides" in {
    // sixth row contains two '1's
    val inTxt = "647128359" + 
                "2.......6" + 
                "3.......8" + 
                "9.......5" + 
                "7.......3" + 
                "1.......1" + 
                "8.......7" + 
                "5.......4" + 
                "491857632"
    val inBoard = Board.fromStandardString(inTxt)
    for (t <- transformations(inBoard)) assert(!t.isValid)
  }
  
  it should "be preserved by toNormalForm" in {  
    for (board <- randomBoards()) {
      assert(board.isValid == board.toNormalForm.isValid)
    }  
  }  

  it should "be preserved by mirrorAntiDiagonal" in {  
    for (board <- randomBoards()) {
      assert(board.isValid == board.mirrorAntiDiagonal.isValid)
    }  
  }  

  it should "be preserved by mirrorVertical" in {  
    for (board <- randomBoards()) {
      assert(board.isValid == board.mirrorVertical.isValid)
    }  
  }  
    
  "toNormalForm" should "have expected intermediate steps for one specific example" in {  
      val inputBoard = fromStandardString(
           "129387564" + 
           "4.......1" + 
           "6.......7" +
           "2.......5" + 
           "5.......8" + 
           "3.......2" + 
           "7.......9" + 
           "9.......3" + 
           "851973426")
      // this board and all its transformations should be valid
      for (t <- transformations(inputBoard)) assert(t.isValid)
      
      //
      // To put this into normal form, first map digits as follows:
      //    1->1, 2->2, 3->4, 4->9, 5->7, 6->8, 7->6, 8->5, 9->3
      //
      val inputRelabeled = fromStandardString(  
           "123456789" + 
           "9.......1" + 
           "8.......6" +
           "2.......7" + 
           "7.......5" + 
           "4.......2" + 
           "6.......3" + 
           "3.......4" + 
           "571364928")
      assert(inputRelabeled.isValid)           
      assert(inputBoard.normalizeTopRow == inputRelabeled)           
  
      //
      // As a last step reorder the rows so in the right column: 
      //     (row 2, row 3) increase
      //     (row 4, row 5, row 6) increase
      //     (row 7, row 8) increase
      //
      val inputRelabeledRowShuffle = fromStandardString(  
           "123456789" + 
           "9.......1" +  // 1 < 6
           "8.......6" +
           "4.......2" +  // 2 < 5 < 7
           "7.......5" + 
           "2.......7" + 
           "6.......3" +  // 3 < 4
           "3.......4" + 
           "571364928")
      assert(inputRelabeledRowShuffle.isValid)           
      assert(inputBoard.toNormalForm == inputRelabeledRowShuffle)
      assert(inputBoard.allNormalForms.toSet.size == 24) // no symmetry reduction takes place
  }
  
  it should "do nothing a second time" in {  
    for (board <- randomBoards()) {
      if (board.isValid) assert(board.toNormalForm == board.toNormalForm.toNormalForm)
    }  
  }
  
  it should "generate boards with isInNormalForm == true" in {
    for (board <- randomBoards()) {
      if (board.isValid) assert(board.toNormalForm.isInNormalForm)
    }
  }
  
  "allNormalForms" should "generate boards with isInNormalForm == true" in {
    for (board <- randomBoards()) {
      if (board.isValid) {
        for (b <- board.allNormalForms) {
          assert(b.isValid)
          assert(b.isInNormalForm)   
        }
      }
    }
  }
  
  it should "generate identical boards when only top row is filled in" in {
    val board19 = emptyBoard.withNewTopRow((1 to 9).toArray)
    val perms = List(
        Array(1,2,3,4,5,6,7,8,9),
        Array(9,8,7,6,5,4,3,2,1),
        Array(3,2,4,5,6,7,9,8,1),
        Array(2,1,6,7,5,3,4,9,8))

    for (p <- perms) {
      val board = emptyBoard.withNewTopRow(p)
      val normalForms = board.allNormalForms
      assert(normalForms.toSet == Set(board19))
    }
  }
    
}
