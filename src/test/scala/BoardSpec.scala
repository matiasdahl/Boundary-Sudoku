import Board._
import BoardGenerators._
import org.scalatest._
import scala.collection.mutable.{Set => MutableSet}
import scala.collection.immutable.{Set => ImmutableSet}

class BoardSpec extends FlatSpec with Matchers {

  "A pair of equal Boards" should "have equality == true" in {
    val s = genericBoard
    val t = genericBoard
    assert(s==t && s.equals(t))
  }
  
  it should "have equal hash codes" in {
    val s = genericBoard
    val t = genericBoard
    assert(s.hashCode == t.hashCode)
  }

  it should "should make a set of size 1" in {
    val set0 = ImmutableSet[Board](genericBoard, genericBoard)
    val set1 = MutableSet[Board]()
    set1.add(genericBoard)
    set1.add(genericBoard)
    assert(set0 == set1 && set0 == Set[Board](genericBoard))
  }

  "A pair of distinct Borders" should "have equality != true." in {
    val s = genericBoard
    val t = s.mirrorVertical
    assert(s != t && !s.equals(t))
  }
  
  it should "can have distinct hashCodes" in {
    val s = genericBoard
    val t = s.mirrorVertical
    assert(s.hashCode != t.hashCode)
  }

  it should "should make a set of size 2" in {
    val set0 = ImmutableSet[Board](genericBoard, genericBoard.mirrorVertical)
    val set1 = MutableSet[Board]()
    for(i <- 1 to 5) {
      set1.add(genericBoard)
      set1.add(genericBoard.mirrorVertical)
    }
    assert(set0 == set1 && set0.size == 2)
  }
  
  "A set of boards" should "detect duplicates" in {  
    val boardSet1 = MutableSet[Board]()
    val boardSet2 = MutableSet[Board]()
    
    for (aRandomBoard <- randomBoards()) {
        boardSet1.add(aRandomBoard)
        boardSet2.add(aRandomBoard)
        boardSet2.add(aRandomBoard) // this should do nothing
    }        
    assert(boardSet1 == boardSet2)
   }

  "The empty board" should "contain only zeros (computed row-wise)" in {
    val genRowEntries = BoardIndex.rows.toList
                                  .flatMap(r => emptyBoard.row(r).toList)
    assert(genRowEntries.toSet == Set(0))
  }

  it should "contain only zeros (computed column-wise)" in {
    val genColEntries = BoardIndex.columns.toList
                                  .flatMap(c => emptyBoard.column(c).toList)
    assert(genColEntries.toSet == Set(0))
  }

  it should "contain only zeros (computed with allEntries)" in {
    assert(emptyBoard.allBorderEntries.toSet == Set(0))
  }
    
  "The generic board" should "have corrent content (computed row-wise using toIteratedList)" in {  
    val genRows = genericBoard.toIteratedList
    val expectedRows = List(
           List( 1, 2, 3, 4, 5, 6, 7, 8, 9),
           List(32, 0, 0, 0, 0, 0, 0, 0,10),
           List(31, 0, 0, 0, 0, 0, 0, 0,11),
           List(30, 0, 0, 0, 0, 0, 0, 0,12),
           List(29, 0, 0, 0, 0, 0, 0, 0,13),
           List(28, 0, 0, 0, 0, 0, 0, 0,14),
           List(27, 0, 0, 0, 0, 0, 0, 0,15),
           List(26, 0, 0, 0, 0, 0, 0, 0,16),
           List(25,24,23,22,21,20,19,18,17))
    assert(genRows == expectedRows)
   }
  
   it should "corrent content (computed column-wise)" in {  
    val genColumns = BoardIndex.columns  
                               .toList
                               .map(c => genericBoard.column(c).toList)
    val expectedColumns = List(
           List( 1,32,31,30,29,28,27,26,25),
           List( 2, 0, 0, 0, 0, 0, 0, 0,24),
           List( 3, 0, 0, 0, 0, 0, 0, 0,23),
           List( 4, 0, 0, 0, 0, 0, 0, 0,22),
           List( 5, 0, 0, 0, 0, 0, 0, 0,21),
           List( 6, 0, 0, 0, 0, 0, 0, 0,20),
           List( 7, 0, 0, 0, 0, 0, 0, 0,19),
           List( 8, 0, 0, 0, 0, 0, 0, 0,18),
           List( 9,10,11,12,13,14,15,16,17))
    assert(genColumns == expectedColumns)
   }
   
  it should "have its border consisiting of entries {1, ..., 32} (computed with allBorderEntries)" in {
    assert(genericBoard.allBorderEntries.toSet == (1 to 32).toSet)
  }
   
  "Random boards" should "have all sides made of permutations of {1, ..., 9}" in {
    val set19 = (1 to 9).toSet
    for (randomBoard <- randomBoards()) {
      assert(randomBoard.column(0).toSet == set19 &&
           randomBoard.column(8).toSet == set19 &&
           randomBoard.row(0).toSet == set19 &&
           randomBoard.row(8).toSet == set19)  
    }
  }
  
  it should "should contain distinct boards" in {
    assert(randomBoards(10).toSet.size > 1) 
  }
  
  "Symmetries of the square" should "should give 8 distinct boards" in {
    val board = emptyBoard.withNewTopRow((1 to 9).toArray)
    assert(transformations(board).toSet.size == 8)
  }
 
}
