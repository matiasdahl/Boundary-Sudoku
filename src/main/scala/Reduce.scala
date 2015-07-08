import Board._
import Logger._
import scala.collection.mutable.{ Set => MutableSet }
import scala.collection.immutable.{ Set => ImmutableSet }

package object Reduce {

  /**
   *  Return a list of boards that generate all the boards in `inList` using
   *  the given symmetries.
   */
  def reduceBoardList(inList: List[Board],
                      symmetries: Board => Iterable[Board]): List[Board] = {
    var generators = MutableSet[Board]()

    // Logging
    def status(currentCount: Int): String = {
      " Processing. Reduced " + currentCount +
        " configurations (out of " + inList.size + ") to " +
        generators.size + " by symmetry"
    }
    val log = new Logger(status, indent = 12)

    for (b <- inList) {
      log.tick()
      val seen = symmetries(b)
        .flatMap(board => board.allNormalForms) // all transformations of `b`
        .toSet // .. as a set
        .intersect(generators) // .. intersect with `generators` set.

      // - If `seen` is empty it means that the board `b` can not be transformed 
      //    (using the given symmetries) into an entry in the `generators` list.
      //
      // - If `seen` is non-empty, the border `b` can be transformed into an entry 
      //    in the `generators` set. 
      //            
      if (seen.isEmpty) generators.add(b.toNormalForm)
    }
    println("       Done. Reduced " + inList.length + " configurations to " + generators.size)

    generators.toList
  }

  /**
   *  Double check that the entries in `reducedList` generate all the entries in `inList`.
   */
  def verifyReduction(inList: Iterable[Board],
                      reducedList: Iterable[Board],
                      symmetries: Board => Iterable[Board]) = {
    val reducedSet = reducedList.toSet

    // Logging
    def logStatus(count: Int): String = " Double checking reduction. At " + 
                                          count + " of " + inList.size
    val log = new Logger(logStatus, indent = 12)

    for (b <- inList) {
      log.tick()
      val transformationsOfB = symmetries(b).flatMap(b0 => b0.allNormalForms).toSet
      assert(!transformationsOfB.intersect(reducedSet).isEmpty)    
    }
 
    log.message(" Verified all " + inList.toList.length +
      " entries. All can be reduced to the given " +
      reducedList.toList.length + " entries")
  }

}
