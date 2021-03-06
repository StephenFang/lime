package org.bdgenomics.lime.cli

import org.apache.spark.SparkContext
import org.bdgenomics.adam.rdd.ADAMContext._
import org.bdgenomics.adam.rdd.ADAMSaveAnyArgs
import org.bdgenomics.utils.cli._
import org.kohsuke.args4j.Argument

object Intersection extends BDGCommandCompanion {
  val commandName = "intersect"
  val commandDescription = "Compute intersection of regions between two inputs"

  def apply(cmdLine: Array[String]) = {
    new Intersection(Args4j[IntersectionArgs](cmdLine))
  }

  class IntersectionArgs extends Args4jBase with ADAMSaveAnyArgs with ParquetArgs {
    @Argument(required = true,
      metaVar = "INPUT1",
      usage = "The first file for intersection",
      index = 0)
    var leftInput: String = null
    @Argument(required = true,
      metaVar = "INPUT2",
      usage = "The second file for intersection",
      index = 1)
    var rightInput: String = null

    override var sortFastqOutput: Boolean = false
    override var asSingleFile: Boolean = false
    override var deferMerging: Boolean = false
    override var outputPath: String = ""
  }

  class Intersection(protected val args: IntersectionArgs) extends BDGSparkCommand[IntersectionArgs] {
    val companion = Intersection

    def run(sc: SparkContext) {
      val leftGenomicRDD = sc.loadBed(args.leftInput)
      val rightGenomicRDD = sc.loadBed(args.rightInput)

      leftGenomicRDD.shuffleRegionJoin(rightGenomicRDD)
      leftGenomicRDD.rdd.collect.foreach(println)
    }
  }
}