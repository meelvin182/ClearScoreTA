package com.clearscore.techassignment.dto

import com.clearscore.techassignment.csCardsResponseEntity1
import com.clearscore.techassignment.recommendationResponseEntity1
import com.clearscore.techassignment.scoredCardsResponseEntity1
import com.clearscore.techassignment.scoredCardsResponseEntityWithNegativeApprovalRating
import com.clearscore.techassignment.scoredCardsResponseEntityWithNegativeApr
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class ResponseDTOsTest {

  @Test
  fun `creates CSCardsResponseEntity`() {
    assertEquals(BigDecimal("21.4"), csCardsResponseEntity1.apr)
    assertEquals("SuperSaver Card", csCardsResponseEntity1.cardName)
    assertEquals(BigDecimal("6.3"), csCardsResponseEntity1.eligibility)
  }

  @Test
  fun `maps CSCardsResponseEntity to IntegrationResponse`() {
    val integrationResponse = CSCardsResponseEntity.toIntegrationResponse(csCardsResponseEntity1)
    assertEquals(BigDecimal("21.4"), integrationResponse.apr)
    assertEquals(BigDecimal("63.0"), integrationResponse.normalizedEligibility)
    assertEquals("SuperSaver Card", integrationResponse.cardName)
    assertEquals(Provider.CS_CARDS, integrationResponse.provider)
  }

  @Test
  fun `invalidates CSCardsResponseEntity with negative apr`() {
    val csCardsResponseEntity =
        CSCardsResponseEntity(
            cardName = "dummy", apr = BigDecimal("-1"), eligibility = BigDecimal("0.1"))
    assertFalse(CSCardsResponseEntity.isValid(csCardsResponseEntity))
  }

  @Test
  fun `invalidates CSCardsResponseEntity with negative eligibility`() {
    val csCardsResponseEntity =
        CSCardsResponseEntity(
            cardName = "dummy", apr = BigDecimal("0.1"), eligibility = BigDecimal("-0.1"))
    assertFalse(CSCardsResponseEntity.isValid(csCardsResponseEntity))
  }

  @Test
  fun `creates ScoredCardsResponseEntity`() {
    assertEquals(BigDecimal("19.4"), scoredCardsResponseEntity1.apr)
    assertEquals("ScoredCard Builder", scoredCardsResponseEntity1.card)
    assertEquals(BigDecimal("0.8"), scoredCardsResponseEntity1.approvalRating)
  }

  @Test
  fun `maps ScoredCardsResponseEntity to IntegrationResponse`() {
    val integrationResponse =
        ScoredCardsResponseEntity.toIntegrationResponse(scoredCardsResponseEntity1)
    assertEquals(BigDecimal("19.4"), integrationResponse.apr)
    assertEquals(BigDecimal("80.0"), integrationResponse.normalizedEligibility)
    assertEquals("ScoredCard Builder", integrationResponse.cardName)
    assertEquals(Provider.SCORED_CARDS, integrationResponse.provider)
  }

  @Test
  fun `invalidates ScoredCardsResponseEntity with negative apr`() {
    assertFalse(ScoredCardsResponseEntity.isValid(scoredCardsResponseEntityWithNegativeApr))
  }

  @Test
  fun `invalidates ScoredCardsResponseEntity with negative approval rating`() {
    assertFalse(
        ScoredCardsResponseEntity.isValid(scoredCardsResponseEntityWithNegativeApprovalRating))
  }

  @Test
  fun `creates RecommendationResponseEntity`() {
    assertEquals(BigDecimal("21.4"), recommendationResponseEntity1.apr)
    assertEquals("CSCards", recommendationResponseEntity1.provider)
    assertEquals(BigDecimal("0.137"), recommendationResponseEntity1.cardScore)
    assertEquals("CSCards", recommendationResponseEntity1.name)
  }
}
