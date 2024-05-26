package com.epam.api

import com.epam.reusedComponents.BaseHelpers._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object tabs {
  def tab(tabName: String): ChainBuilder = {
    exec(http(s"Click on $tabName")
      .get(baseUrl + s"$tabName")
      .check(regexExtractValues("productUrlAllValues", productUrlsRegex))
      .check(regexExtractValues("cartContentAllValues", cartContentRegex))
      .check(regexExtractValues("productQuantityAllValues", productQuantityRegex))
      .check(regexExtractValues("productIdAllValues", productIdRegex))
      .check(regexExtractValue("transactionIdValue", transactionIdRegex))
      .check(regexExtractValue("totalNetValue", totalNetRegex))
    )
  }
}
