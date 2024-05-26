package com.epam.api

import com.epam.reusedComponents.BaseHelpers._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.util.Random

object productPage {
  def tableProduct(): ChainBuilder = {
    exec(http("Click on a table product")
      .get(session => baseUrl + s"/products/${session("productUrlAllValues").as[Seq[String]].headOption.getOrElse("NOT_FOUND")}")
      .check(allProductPageChecks(): _*))
  }

  def chairProduct(): ChainBuilder = {
    exec(http("Click on a chair product")
      .get { session =>
        val productUrls = session("productUrlAllValues").as[Seq[String]]
        val randomProduct = Random.shuffle(productUrls.toList).head
        baseUrl + s"/products/$randomProduct"
      }
      .check(allProductPageChecks(): _*))
  }

  def addToCart(): ChainBuilder = {
    exec(http("Add a product to cart")
      .post(baseUrl + "wp-admin/admin-ajax.php")
      .headers(Map("Content-Type" -> "application/x-www-form-urlencoded; charset=UTF-8"))
      .formParam("action", "ic_add_to_cart")
      .formParam("add_cart_data", "current_product=${currentProductValue}&cart_content=${cartContentValue}&current_quantity=${currentQuantityValue}")
      .formParam("cart_widget", "0")
      .formParam("cart_container", "0")
      .check(substring("Added!").exists))
  }
}
