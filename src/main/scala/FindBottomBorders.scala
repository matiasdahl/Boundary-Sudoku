import Board._
import Reduce._
import BoardIO._

object FindBottomBorders {
  
  /** 
   *  For a board with top and right sides filled in, generate all valid boards
   *  where also the bottom row is filled in.
   */
  def generateBottomBorders(board: Board): List[Board] = {
      (1 to 9).toArray 
        .permutations
        .filter(perm => perm.last == board.entryAt(8, 8))
        .map(perm => board.withNewBottomRow(perm))
        .filter(b => b.isValid)
        .toList
    }

  def main(args: Array[String]): Unit = {  
    println (" --- Running Step 2 ---")
    
    // Load reduced output from FindRightBorders
    val inBoards = loadFile("top-right-reduced.txt")
    assert(inBoards.length == 52)
    
    // Generate all valid configurations for the bottom row. 
    val generatedBoards = inBoards.flatMap(board => generateBottomBorders(board))
    assert(generatedBoards.length == 471346)
    writeToFile(generatedBoards, "top-right-bottom-all.txt")

    // Boards with full top, right, and bottom sides are structurally invariant under a 
    // vertical flip. Use this symmetry to remove redundant entries.
    def symmetries: Board => List[Board] = (b: Board) => List(b, b.mirrorVertical)
    val reducedBoards = reduceBoardList(generatedBoards, symmetries)
    
    // Double-check reduction is valid. 
    assert(reducedBoards.length == 147372)
    verifyReduction(generatedBoards, reducedBoards, symmetries)
    
    // All tests pass. Write output to disk.
    writeToFile(reducedBoards, "top-right-bottom-reduced.txt")
  }
 
}
