package com.epam.api

import com.epam.reusedComponents.BaseHelpers._
import io.gatling.core.Predef._
import io.gatling.core.structure._
import io.gatling.http.Predef._

object home {
  def homePage(): ChainBuilder = {
    exec(http("Open Home Page")
      .get(baseUrl))
  }
}

