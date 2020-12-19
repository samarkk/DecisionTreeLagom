package org.example.dectree.api

import play.api.libs.json.{Json, OFormat}

final case class DPAndPrediction(dp: String, prediction: Double)
object DPAndPrediction{
  implicit val DPAndPredictionFormat: OFormat[DPAndPrediction] = Json.format[DPAndPrediction]
}
final case class DPItems(dps: Array[String])
object DPItems{
  implicit  val DpItemsFormat: OFormat[DPItems] = Json.format[DPItems]
}