import Board._
import BoardIO._
import Reduce._

object FindRightBorders {

  /**
   *  For a board `top` with only a top row, return a list all possible completions
   *  for the right side. Each board in the list is guaranteed to have isValid == true.
   */
  def generateRightBorders(top: Board): List[Board] = {
    (1 to 9).toArray
            .permutations
            .filter(perm => (perm.head == 9)) // (last entry of top row = 9)
            .map(perm => top.withNewRightSide(perm))
            .filter(board => board.isValid)
            .toList
  }

  def main(args: Array[String]): Unit = {
    println (" --- Running Step 1 ---")
    // By renaming entries, the top row can be assumed to be 123456789.
    val topBorder: Board = emptyBoard.withNewTopRow((1 to 9).toArray)

    // Generate all valid configurations for the right border of the board.
    val borders = generateRightBorders(topBorder)
    assert(borders.length == 21600)
    writeToFile(borders, "top-right-all.txt")
    
    // Boards with full top and right side are structurally invariant under a diagonal flip. 
    // Use this symmetry to remove redundant entries. 
    def topRightSymmetries: Board => List[Board] = (b: Board) => List(b, b.mirrorAntiDiagonal)
    val reducedBorders = reduceBoardList(borders, topRightSymmetries)
    assert(reducedBorders.length == 52)
    
    // Double check that reduction is ok. Write reduced boards to file.
    verifyReduction(borders, reducedBorders, topRightSymmetries) 
    writeToFile(reducedBorders, "top-right-reduced.txt")
  }
  
}
