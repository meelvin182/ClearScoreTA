package com.clearscore.techassignment.utils

import com.clearscore.techassignment.csCardResponseEntity1
import com.clearscore.techassignment.scoredCardResponseEntity1
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UtilsTests {

  @Test
  fun `calculates credit card score for CsCard response`() {
    assertEquals(BigDecimal("0.137"), calculateCardScore(csCardResponseEntity1))
  }

  @Test
  fun `calculates credit card score for Scored Cards response`() {
    assertEquals(BigDecimal("0.053"), calculateCardScore(scoredCardResponseEntity1))
  }
}
