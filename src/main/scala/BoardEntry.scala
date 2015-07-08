object BoardEntry {

  type BoardEntry = Int

  /**
   *  Encode a BoardEntry (in 0, 1, ... , 9) into a character for output.
   *
   *    0 -> "." (an empty board position),
   * 
   *    1 -> "1", ... , 9 -> "9" (digits on the Sudoku board).
   *      
   *  Representing empty positions with '.' follows the JSolve.c solver.     
   */
  def encodeToChar(inInt: BoardEntry): Char = {
    assert((inInt >= 0) && (inInt <= 9))
    ".123456789"(inInt)
  }

  /** Inverse of encodeToChar */
  def decodeChar(inCharacter: Char): BoardEntry = {
    val result: BoardEntry = inCharacter match {
      case '.' => 0
      case in  => in.toInt - '0'.toInt
    }
    assert(result >= 0 && result <= 9 && inCharacter != '0') 
    result
  }

}

import BoardEntry._
