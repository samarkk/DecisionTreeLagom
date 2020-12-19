package org.example.dectree.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait DectreeService extends Service {
  // coverype predictor with a parameter that will have to be provided in the url call
  def cvtpred(dp: String): ServiceCall[NotUsed, DPAndPrediction]
  // calling covert type without arguments
  // in the requuest we wiil provide DataPoint items and send back in response the
  // sequence of strings returned back from CovTypePredictor.predictMultipleItems
  def cvtpredm : ServiceCall[DPItems, Array[DPAndPrediction]]
  def hello(id: String): ServiceCall[NotUsed, String]

  override final def descriptor: Descriptor = {
    import Service._
    named("dectree")
      .withCalls(
        pathCall("/cvtpred/:dp", cvtpred _),
        // we can name the route anything
        pathCall("/predm", cvtpredm _),
        pathCall("/hello/:id", hello _)
      ).withAutoAcl(true)
  }
}
