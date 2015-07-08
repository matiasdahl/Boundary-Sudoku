import BoardEntry._

object BoardIndex {
  
  type BoardColumn = Int
  type BoardRow = Int
  type BoardIndex = Int

  def columns: Array[BoardColumn] = (0 to 8).toArray

  def rows: Array[BoardRow] = (0 to 8).toArray

}

import BoardIndex._
