package com.epam.reusedComponents

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure._
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck

object BaseHelpers {
  val baseUrl = "http://localhost/"
  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("en-GB,en;q=0.9,en-US;q=0.8")
    .connectionHeader("keep-alive")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0")
  val currentProductRegex = """name="current_product" value="(\d+)""""
  val cartContentRegex = """name="cart_content" value='([^']*)'"""
  val currentQuantityRegex = """name="current_quantity"\s+type="number"\s+min="1"\s+max=""\s+step="1"\s+value="(\d+)""""
  val productUrlsRegex = s""""${baseUrl}products/(.*?)""""
  val productQuantityRegex = """name="p_quantity\[\]"\s*value="(\d+)""""
  val productIdRegex = """name="p_id\[\]".*?value="([^"]+)""""
  val transactionIdRegex = """value="(\d+)"\s+name="trans_id""""
  val totalNetRegex = """name="total_net"\s+value="([\d.,]+)""""
  val productPriceRegex = """name="product_price_\d+__"\s*value="([0-9,.]+)""""
  val cartTypeRegex = """name="cart_type"\s+value="([^"]*)""""
  val cartInsideHeadersRegex = """<input type="hidden" value="([^"]+)" name="cart_inside_header_\d+" \/>"""
  val cartSubmitRegex = """input value="([^"]+)" name="cart_submit""""
  val testDataPath = "testData/testData.csv"

  def regexExtractValue(createdVarName: String, regexPattern: String): HttpCheck = {
    regex(regexPattern).find.optional.saveAs(createdVarName)
  }

  def regexExtractValues(createdVarName: String, regexPattern: String): HttpCheck = {
    regex(regexPattern).findAll.optional.saveAs(createdVarName)
  }

  def allProductPageChecks(): List[HttpCheck] = {
    List(
      regexExtractValue("currentProductValue", currentProductRegex),
      regexExtractValue("currentQuantityValue", currentQuantityRegex),
      regexExtractValue("cartContentValue", cartContentRegex)
    )
  }

  def testDataValues(filePath: String): FeederBuilderBase[String] = {
    csv(filePath).circular
  }

  def timer(Min: Int = 2, Max: Int = 5): ChainBuilder = {
    pause(Min, Max)
  }
}
