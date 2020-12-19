organization in ThisBuild := "org.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.12"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"

lazy val `dectree` = (project in file(".")).aggregate(
  `dectreeApi`, `dectreeImpl`
)
lazy val `dectreeApi` = (project in file ("dectree-api"))
  .settings(version := "1.0-SNAPSHOT",
    libraryDependencies += lagomScaladslApi)

lazy val dectreeImpl = (project in file("dectree-impl"))
  .enablePlugins(LagomScala)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      "ml.combust.mleap" %% "mleap-spark" % "0.16.0"
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(dectreeApi)

lagomCassandraEnabled in ThisBuild := false

lagomKafkaEnabled in ThisBuild := false