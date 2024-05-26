package com.epam.sceanrios

import com.epam.reusedComponents.BaseHelpers._
import io.gatling.core.Predef._
import io.gatling.core.structure._
import io.gatling.http.Predef._

object PlaceOrder {

  def scnPlaceOrder(): ScenarioBuilder = {
    scenario("Add table to cart")
      .exec(flushHttpCache)
      .exec(flushCookieJar)
      .exitBlockOnFail(
        feed(testDataValues(testDataPath))
          .group("Home page") {
            exec(com.epam.api.home.homePage())
              .exec(timer(1, 3))
          }
          .group("Click on 'Tables' tab") {
            exec(com.epam.api.tabs.tab("tables"))
              .exec(timer())
          }
          .group("Add table to cart") {
            exec(com.epam.api.productPage.tableProduct())
              .exec(timer(2, 3))
              .exec(com.epam.api.productPage.addToCart())
              .exec(timer())
          }
          .exec(
            randomSwitch(
              50d -> exec(scnAddChair()),
              30d -> exec(scnCheckoutOrder())
            )
          )
      )
  }

  def scnAddChair(): ScenarioBuilder = {
    scenario("Add chair to cart")
      .group("Click on 'Chairs' tab") {
        exec(com.epam.api.tabs.tab("chairs"))
          .exec(timer(4, 7))
      }
      .group("Add chair to cart") {
        exec(com.epam.api.productPage.chairProduct())
          .exec(timer(2, 3))
          .exec(com.epam.api.productPage.addToCart())
          .exec(timer())
      }
  }

  def scnCheckoutOrder(): ScenarioBuilder = {
    scenario("Make an order")
      .group("Click on 'Cart' tab") {
        exec(com.epam.api.tabs.tab("cart"))
          .exec(timer())
      }
      .group("Place an order") {
        exec(com.epam.api.tabs.tab("checkout"))
          .exec(timer())
          .exec(com.epam.api.checkout.placeOrder())
          .exec(timer(10, 15))
          .exec(com.epam.api.checkout.checkout())
      }
  }
}
