class Logger(printLine: Int => String, 
                 indent: Int = 0,
                   step: Int = 5000) {
  val startTime = System.nanoTime
  var counter = 0

  def message(str: String) = {
    val currentTime: Double = (System.nanoTime - startTime) / 1000000.0 / 1000.0
    println(" " * indent + "%6d".format(currentTime.toInt) + "s :" + str)
  }  
  
  def tick() = {
    if (counter % step == 0) message(printLine(counter))
    counter += 1
  }
  
}
