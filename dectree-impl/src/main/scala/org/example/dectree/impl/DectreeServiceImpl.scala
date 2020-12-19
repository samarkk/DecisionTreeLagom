package org.example.dectree.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import javax.inject.Inject

import scala.concurrent.ExecutionContext
import org.example.dectree.api.{DPAndPrediction, DPItems, DectreeService}

import scala.concurrent.Future
import play.api.Configuration
import util.CovTypePredictionService

class DectreeServiceImpl @Inject()(config: Configuration)(implicit ec: ExecutionContext) extends DectreeService {
  val mleapFileLocation: String = config.get[String]("floc")
  val ctps = new CovTypePredictionService(mleapFileLocation)
  // http://localhost:9000/cvtpred/2596,51,3,258,0,510,221,232,148,6279,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0

  override def cvtpred(dp: String): ServiceCall[NotUsed, DPAndPrediction] = ServiceCall {
    request =>
      val dpAndPred = ctps.makePredictionForOneRecord(dp)
      Future.successful(dpAndPred)
  }

  //  curl -X POST -H "Content-Type: application/json" -d '{"dps":["2596,51,3,258,0,510,221,232,148,6279,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0", "2785,155,18,242,118,3090,238,238,122,6211,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0"]}' localhost:9000/predm
  // a GET will not work in the above method
  override def cvtpredm: ServiceCall[DPItems, Array[DPAndPrediction]] = ServiceCall {
    request =>
      println("checking datapoints received from request dps array of string are " + request.dps.mkString(","))
      println("DectreeserviceImp cvtpredm hit")
      val predictions = ctps.makePredictionsForMultipleRecords(request.dps)
      Future(predictions)
  }

  override def hello(id: String): ServiceCall[NotUsed, String] = ServiceCall {
    - => Future("Hello " + id)
  }
}
