package org.example.dectree.impl

import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceLocator}
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire.wire
import org.example.dectree.api.DectreeService
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents

class DectreeLoader extends  LagomApplicationLoader{
  override  def loadDevMode(context: LagomApplicationContext) =
    new DectreeApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication = new DectreeApplication(context) {
    override def serviceLocator: ServiceLocator = ServiceLocator.NoServiceLocator
  }

  override def describeService: Option[Descriptor] = Some(readDescriptor[DectreeService])
}

abstract  class DectreeApplication (context: LagomApplicationContext)
extends  LagomApplication(context)
with AhcWSComponents{
  override  lazy val lagomServer: LagomServer = serverFor[DectreeService](wire[DectreeServiceImpl])
}
