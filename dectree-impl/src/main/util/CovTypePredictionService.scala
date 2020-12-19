package util

import javax.inject.{Inject, Singleton}
import ml.combust.bundle.BundleFile
import ml.combust.bundle.dsl.Bundle
import ml.combust.mleap.core.types.{ScalarType, StructField, StructType}
import ml.combust.mleap.runtime.MleapSupport.MleapBundleFileOps
import ml.combust.mleap.runtime.frame.{DefaultLeapFrame, Transformer, Row => MleapRow}
import org.example.dectree.api.{DPAndPrediction, DPItems, DectreeService}
import play.api.Configuration
import resource.managed

class CovTypePredictionService(val mleapFileLocation: String) {
  val bundle: Bundle[Transformer] = (for (bf <- managed(BundleFile(mleapFileLocation))) yield {
    bf.loadMleapBundle().get
  }).tried.get

  val mlRFTransformer: Transformer = (for (bf <- managed(BundleFile(mleapFileLocation))) yield {
    bf.loadMleapBundle().get.root
  }).tried.get

  val mleapSchema: StructType = StructType(
    StructField("Elevation", ScalarType.Int),
    StructField("Aspect", ScalarType.Int),
    StructField("Slope", ScalarType.Int),
    StructField("Horizontal_Distance_To_Hydrology", ScalarType.Int),
    StructField("Vertical_Distance_To_Hydrology", ScalarType.Int),
    StructField("Horizontal_Distance_To_Roadways", ScalarType.Int),
    StructField("Hillshade_9am", ScalarType.Int),
    StructField("Hillshade_Noon", ScalarType.Int),
    StructField("Hillshade_3pm", ScalarType.Int),
    StructField("Horizontal_Distance_To_Fire_Points", ScalarType.Int),
    StructField("wilderness", ScalarType.Double),
    StructField("soil", ScalarType.Double)).get

  def transformLineToMLRow(line: String): MleapRow = {
    val tarr = line.split(",")
    MleapRow(
      tarr(0).toInt,
      tarr(1).toInt, tarr(2).toInt, tarr(3).toInt,
      tarr(4).toInt, tarr(5).toInt, tarr(6).toInt,
      tarr(7).toInt, tarr(8).toInt, tarr(9).toInt,
      tarr.slice(10, 14).indexOf("1").toDouble,
      tarr.slice(14, 54).indexOf("1").toDouble)
  }

  def makePredictionForOneRecord(ctp: String): DPAndPrediction = {
    val data = Seq(transformLineToMLRow(ctp))
    val frame = DefaultLeapFrame(mleapSchema, data)
    val frameRF = mlRFTransformer.transform(frame).get
    //    println(frameRF.collect().mkString(","))
    val prediction = frameRF.select("prediction").get.collect().map(x => x.getAs[Double](0))
    DPAndPrediction(ctp, prediction.head)
  }

  def makePredictionsForMultipleRecords(ctps: Array[String]): Array[DPAndPrediction] = {
    val framesWithPrediction = ctps.map { x =>
      mlRFTransformer.transform(
        DefaultLeapFrame(mleapSchema, Seq(transformLineToMLRow(x)))).get
    }
    framesWithPrediction.collect { case x => x }.foreach(x =>
      x.select("prediction").get.show)
    val zippedPredictions = framesWithPrediction.flatMap(x => x.select("prediction").get.collect().map(
      x => x.getAs[Double](0))).zipWithIndex.zip(ctps)
    zippedPredictions.map { case ((pred, idx), ctp) => DPAndPrediction(ctp, pred) }
  }
}