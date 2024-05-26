package com.epam.api

import com.epam.reusedComponents.BaseHelpers._
import io.gatling.core.Predef._
import io.gatling.core.structure._
import io.gatling.http.Predef._

object checkout {
  def placeOrder(): ChainBuilder = {
    exec { session =>
      val allCartContents = session("cartContentAllValues").as[List[String]]
      val allP_Id = session("productIdAllValues").as[List[String]]
      val allP_Quantity = session("productQuantityAllValues").as[List[String]]

      session
        .set("allCartContentValues", allCartContents.lastOption.getOrElse("NOT_FOUND"))
        .set("p_id_1", allP_Id.headOption.getOrElse("NOT_FOUND"))
        .set("p_id_2", allP_Id.lift(1).getOrElse("NOT_FOUND"))
        .set("p_quantity_1", allP_Quantity.headOption.getOrElse("NOT_FOUND"))
        .set("p_quantity_2", allP_Quantity.lift(1).getOrElse("NOT_FOUND"))
    }
      .exec(http("Click on 'Place an order'")
        .post(baseUrl + "checkout")
        .formParam("cart_content", "${allCartContentValues}")
        .formParam("p_id[]", "${p_id_1}")
        .formParam("p_quantity[]", "${p_quantity_1}")
        .formParam("p_id[]", "${p_id_2}")
        .formParam("p_quantity[]", "${p_quantity_2}")
        .formParam("total_net", "${totalNetValue}")
        .formParam("trans_id", "${transactionIdValue}")
        .formParam("shipping", "order")
        .check(regexExtractValue("transactionIdCheckoutValue", transactionIdRegex))
        .check(regexExtractValue("cartContentValueCheckout", cartContentRegex))
        .check(regexExtractValue("totalNetValue", totalNetRegex))
        .check(regexExtractValues("productPriceAllValues", productPriceRegex))
        .check(regexExtractValue("cartTypeValue", cartTypeRegex))
        .check(regexExtractValues("cartInsideHeadersAllValues", cartInsideHeadersRegex))
        .check(regexExtractValue("cartSubmitValue", cartSubmitRegex))
      )
      .exec(http("Fill required details")
        .post(baseUrl + "wp-admin/admin-ajax.php")
        .formParam("action", "ic_state_dropdown")
        .formParam("country_code", "${countryCode}")
        .formParam("state_code", "")

      )
  }

  def checkout(): ChainBuilder = {
    exec { session =>
      val allProductPrice = session("productPriceAllValues").as[List[String]]
      val allCartInsideHeaders = session("cartInsideHeadersAllValues").as[List[String]]

      session
        .set("productPrice_1", allProductPrice.headOption.getOrElse("NOT_FOUND"))
        .set("productPrice_2", allProductPrice.lift(1).getOrElse("NOT_FOUND"))
        .set("cartInsideHeader_1", allCartInsideHeaders.headOption.getOrElse("NOT_FOUND"))
        .set("cartInsideHeader_2", allCartInsideHeaders.lift(1).getOrElse("NOT_FOUND"))
    }
      .exec(http("Checkout")
        .post(baseUrl + "checkout")
        .formParam("ic_formbuilder_redirect", s"${baseUrl}thank-you")
        .formParam("cart_content", "${cartContentValueCheckout}")
        .formParam("product_price_${p_id_1}", "${productPrice_1}")
        .formParam("product_price_${p_id_2}", "${productPrice_2}")
        .formParam("total_net", "${totalNetValue}")
        .formParam("trans_id", "${transactionIdCheckoutValue}")
        .formParam("shipping", "order")
        .formParam("cart_type", "${cartTypeValue}")
        .formParam("cart_inside_header_1", "$cartInsideHeader_1")
        .formParam("cart_company", "")
        .formParam("cart_name", "${userName}")
        .formParam("cart_address", "${address}")
        .formParam("cart_postal", "${postalCode}")
        .formParam("cart_city", "${city}")
        .formParam("cart_country", "${countryCode}")
        .formParam("cart_state", "")
        .formParam("cart_phone", "${phoneNumber}")
        .formParam("cart_email", "${email}")
        .formParam("cart_comment", "")
        .formParam("cart_inside_header_2", "$cartInsideHeader_2")
        .formParam("cart_s_company", "")
        .formParam("cart_s_name", "")
        .formParam("cart_s_address", "")
        .formParam("cart_s_postal", "")
        .formParam("cart_s_city", "")
        .formParam("cart_s_country", "")
        .formParam("cart_s_state", "")
        .formParam("cart_s_phone", "")
        .formParam("cart_s_email", "")
        .formParam("cart_s_comment", "")
        .formParam("cart_submit", "${cartSubmitValue}")
        .check(substring("Thank You").exists)
      )
  }
}
