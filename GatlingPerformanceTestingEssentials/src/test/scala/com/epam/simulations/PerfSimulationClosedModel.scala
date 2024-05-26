package com.epam.simulations

import com.epam.reusedComponents.BaseHelpers._
import com.epam.sceanrios.PlaceOrder.scnPlaceOrder
import io.gatling.core.Predef._
import scala.concurrent.duration._
import scala.language.postfixOps

class PerfSimulationClosedModel extends Simulation {
  //  run command
  //  mvn clean gatling:test -D"default.simulation.class=com.epam.simulations.PerfSimulationClosedModel" -D"usersNumber_1=10" -D"usersNumber_2=10" -D"usersNumber_3=45" -D"duration_1=15" -D"duration_2=60"

  val usersNumber_1 = System.getProperty("usersNumber_1", "1").toInt
  val usersNumber_2 = System.getProperty("usersNumber_2", "1").toInt
  val usersNumber_3 = System.getProperty("usersNumber_3", "1").toInt
  val duration_1 = System.getProperty("duration_1", "10").toInt
  val duration_2 = System.getProperty("duration_2", "10").toInt


  setUp(
    scnPlaceOrder()
      .inject(
        constantConcurrentUsers(usersNumber_1) during (duration_1 seconds),
        rampConcurrentUsers(usersNumber_2) to usersNumber_3 during (duration_2 seconds)
      )
      .protocols(httpProtocol)
  )

}
