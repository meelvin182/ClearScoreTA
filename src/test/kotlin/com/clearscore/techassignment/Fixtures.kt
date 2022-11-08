package com.clearscore.techassignment

import com.clearscore.techassignment.dto.CSCardsResponseEntity
import com.clearscore.techassignment.dto.CreditCardsSearchRequest
import com.clearscore.techassignment.dto.IntegrationResponseEntity
import com.clearscore.techassignment.dto.Provider
import com.clearscore.techassignment.dto.RecommendationResponseEntity
import com.clearscore.techassignment.dto.ScoredCardsResponseEntity
import java.math.BigDecimal

val csCardsResponseEntity1 =
    CSCardsResponseEntity(
        cardName = "SuperSaver Card", apr = BigDecimal("21.4"), eligibility = BigDecimal("6.3"))

val csCardsResponseEntity2 =
    CSCardsResponseEntity(
        cardName = "SuperSpender Card", apr = BigDecimal("19.2"), eligibility = BigDecimal("5.0"))
val csCardsSuccessfulResponse = listOf(csCardsResponseEntity1, csCardsResponseEntity2)

val csCardsResponseEntityWithNegativeEligibility =
    CSCardsResponseEntity(
        cardName = "SuperSaver Card", apr = BigDecimal("21.4"), eligibility = BigDecimal("-6.3"))

val csCardsResponseEntityWithNegativeApr =
    CSCardsResponseEntity(
        cardName = "SuperSpender Card", apr = BigDecimal("-19.2"), eligibility = BigDecimal("5.0"))

val csCardsPartiallySuccessfulResponse =
    listOf(
        csCardsResponseEntityWithNegativeEligibility,
        csCardsResponseEntityWithNegativeApr,
        csCardsResponseEntity1,
        csCardsResponseEntity2)

val scoredCardsResponseEntity1 =
    ScoredCardsResponseEntity(
        card = "ScoredCard Builder", apr = BigDecimal("19.4"), approvalRating = BigDecimal("0.8"))
val scoredCardsResponseEntity2 =
    ScoredCardsResponseEntity(
        card = "ScoredCard Builder", apr = BigDecimal("10.4"), approvalRating = BigDecimal("0.1"))
val scoredCardsSuccessfulResponse = listOf(scoredCardsResponseEntity1, scoredCardsResponseEntity2)

val scoredCardsResponseEntityWithNegativeApprovalRating =
    ScoredCardsResponseEntity(
        card = "dummy", apr = BigDecimal("0.1"), approvalRating = BigDecimal("-0.1"))
val scoredCardsResponseEntityWithNegativeApr =
    ScoredCardsResponseEntity(
        card = "dummy", apr = BigDecimal("-0.1"), approvalRating = BigDecimal("0.1"))

val scoredCardsPartiallySuccessfulResponse =
    listOf(
        scoredCardsResponseEntityWithNegativeApprovalRating,
        scoredCardsResponseEntityWithNegativeApr,
        scoredCardsResponseEntity1,
        scoredCardsResponseEntity2)

val creditCardsSearchRequest =
    CreditCardsSearchRequest(name = "John Smith", creditScore = 500, salary = 9999)

val scoredCardResponseEntity1 =
    IntegrationResponseEntity(
        cardName = "ScoredCard Builder",
        apr = BigDecimal("19.4"),
        normalizedEligibility = BigDecimal("20"),
        provider = Provider.SCORED_CARDS)

val scoredCardResponseEntity2 =
    IntegrationResponseEntity(
        cardName = "ScoredCard Builder",
        apr = BigDecimal("10.2"),
        normalizedEligibility = BigDecimal("100"),
        provider = Provider.SCORED_CARDS)

val csCardResponseEntity1 =
    IntegrationResponseEntity(
        cardName = "CSCards",
        apr = BigDecimal("21.4"),
        normalizedEligibility = BigDecimal("63"),
        provider = Provider.CS_CARDS)

val csCardResponseEntity2 =
    IntegrationResponseEntity(
        cardName = "CSCards",
        apr = BigDecimal("21.4"),
        normalizedEligibility = BigDecimal("50"),
        provider = Provider.CS_CARDS)
val recommendationResponseEntity1 =
    RecommendationResponseEntity(
        provider = "CSCards",
        apr = BigDecimal("21.4"),
        cardScore = BigDecimal("0.137"),
        name = "CSCards")
val recommendationResponseEntity2 =
    RecommendationResponseEntity(
        provider = "ScoredCards",
        apr = BigDecimal("19.4"),
        cardScore = BigDecimal("0.053"),
        name = "ScoredCard Builder")
val recommendationResponseEntity3 =
    RecommendationResponseEntity(
        provider = "ScoredCards",
        apr = BigDecimal("10.2"),
        cardScore = BigDecimal("0.961"),
        name = "ScoredCard Builder")
val recommendationResponseEntity4 =
    RecommendationResponseEntity(
        provider = "CSCards",
        apr = BigDecimal("21.4"),
        cardScore = BigDecimal("0.109"),
        name = "CSCards")
val successCreditCardsRecommendation =
    listOf(
        recommendationResponseEntity1,
        recommendationResponseEntity2,
        recommendationResponseEntity3,
        recommendationResponseEntity4,
    )
