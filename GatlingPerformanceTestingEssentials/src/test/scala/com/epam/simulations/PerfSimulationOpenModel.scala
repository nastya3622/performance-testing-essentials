package com.epam.simulations

import io.gatling.core.Predef._
import com.epam.reusedComponents.BaseHelpers._
import com.epam.sceanrios.PlaceOrder.scnPlaceOrder
import scala.concurrent.duration._
import scala.language.postfixOps

class PerfSimulationOpenModel extends Simulation {
  // run command
  // mvn clean gatling:test -DusersNumber_1=5 -DusersNumber_2=15 -DusersNumber_3=2 -DusersNumber_4=30 -DusersNumber_5=1 -DusersNumber_6=13 -DusersNumber_7=7 -Dduration_1=30 -Dduration_2=10 -Dduration_3=50 -Dduration_4=10 -Dduration_5=20 -Dduration_6=50

  val usersNumber_1 = System.getProperty("usersNumber_1", "1").toInt
  val usersNumber_2 = System.getProperty("usersNumber_2", "1").toInt
  val usersNumber_3 = System.getProperty("usersNumber_3", "1").toInt
  val usersNumber_4 = System.getProperty("usersNumber_4", "1").toInt
  val usersNumber_5 = System.getProperty("usersNumber_5", "1").toInt
  val usersNumber_6 = System.getProperty("usersNumber_6", "1").toInt
  val usersNumber_7 = System.getProperty("usersNumber_7", "1").toInt
  val duration_1 = System.getProperty("duration_1", "1").toInt
  val duration_2 = System.getProperty("duration_2", "1").toInt
  val duration_3 = System.getProperty("duration_3", "1").toInt
  val duration_4 = System.getProperty("duration_4", "1").toInt
  val duration_5 = System.getProperty("duration_5", "1").toInt
  val duration_6 = System.getProperty("duration_6", "1").toInt

  setUp(
    scnPlaceOrder()
      .inject(
        atOnceUsers(usersNumber_1),
        rampUsers(usersNumber_2) during (duration_1 seconds),
        constantUsersPerSec(usersNumber_3) during (duration_2 seconds),
        rampUsers(usersNumber_4) during (duration_3 seconds),
        constantUsersPerSec(usersNumber_5) during (duration_4 seconds),
        rampUsers(usersNumber_6) during (duration_5 seconds),
        stressPeakUsers(usersNumber_7) during (duration_6 seconds)
      ))
    .protocols(httpProtocol)
}
