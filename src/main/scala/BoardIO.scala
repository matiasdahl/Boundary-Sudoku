import Board._
import java.io._
import scala.io.Source

package object BoardIO {
  
  def writeToFile(boardList: List[Board], filename: String): Unit = {
    println(" - Writing " + boardList.length + 
                " configurations to file '" + filename + "'...")    
       
    val txtContent = boardList.map(board => board.toStandardString).mkString("\n")
    
    val file = new PrintWriter(new File(filename))
    file.write(txtContent)
    file.close
    
    println(" - Done. ")
  }

  def loadFile(filename: String): List[Board] = {
    println(" - Loading '" + filename + "'...")    
    val source = Source.fromFile(filename)
    val result = source.getLines.toList.map(line => Board.fromStandardString(line))
    source.close
    println(" - Done. Loaded " + result.length + " configurations.")    
    result
  }
  
}
