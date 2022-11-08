package com.clearscore.techassignment.dto

import com.clearscore.techassignment.utils.calculateCardScore
import java.math.BigDecimal
import mu.KotlinLogging

private const val CSCARDS_ELIGIBILITY_NORMALIZATION_FACTOR = 10
private const val SCOREDCARDS_ELIGIBILITY_NORMALIZATION_FACTOR = 100
private val logger = KotlinLogging.logger {}

/**
 * General response from any integration with normalized eligibility.
 *
 * @property cardName
 * @property apr
 * @property normalizedEligibility
 * @property provider
 */
data class IntegrationResponseEntity(
    val cardName: String,
    val apr: BigDecimal,
    val normalizedEligibility: BigDecimal,
    val provider: Provider
) {
  companion object {
    fun toRecommendationResponseEntity(integrationResponseEntity: IntegrationResponseEntity) =
        RecommendationResponseEntity(
            provider = integrationResponseEntity.provider.provideName,
            name = integrationResponseEntity.cardName,
            apr = integrationResponseEntity.apr,
            cardScore = calculateCardScore(integrationResponseEntity))
  }
}

/**
 * Response from CSCards integration.
 *
 * @property cardName
 * @property apr
 * @property eligibility
 */
data class CSCardsResponseEntity(
    val cardName: String,
    val apr: BigDecimal,
    val eligibility: BigDecimal
) {
  companion object {
    fun toIntegrationResponse(
        csCardsResponseEntity: CSCardsResponseEntity
    ): IntegrationResponseEntity =
        IntegrationResponseEntity(
            cardName = csCardsResponseEntity.cardName,
            apr = csCardsResponseEntity.apr,
            normalizedEligibility =
                csCardsResponseEntity.eligibility *
                    BigDecimal(CSCARDS_ELIGIBILITY_NORMALIZATION_FACTOR),
            provider = Provider.CS_CARDS)

    fun isValid(csCardsResponseEntity: CSCardsResponseEntity): Boolean {
      val isAprValid = csCardsResponseEntity.apr >= BigDecimal("0")
      val isEligibilityValid = csCardsResponseEntity.eligibility >= BigDecimal("0")
      val res = isAprValid && isEligibilityValid
      if (!res) {
        logger.error { "Apr or Eligibility is not valid for $csCardsResponseEntity" }
      }
      return res
    }
  }
}

/**
 * Response from ScoredCards integration.
 *
 * @property card
 * @property apr
 * @property approvalRating
 */
data class ScoredCardsResponseEntity(
    val card: String,
    val apr: BigDecimal,
    val approvalRating: BigDecimal
) {
  companion object {
    fun toIntegrationResponse(
        scoredCardsResponseEntity: ScoredCardsResponseEntity
    ): IntegrationResponseEntity =
        IntegrationResponseEntity(
            cardName = scoredCardsResponseEntity.card,
            apr = scoredCardsResponseEntity.apr,
            normalizedEligibility =
                scoredCardsResponseEntity.approvalRating.multiply(
                    BigDecimal(SCOREDCARDS_ELIGIBILITY_NORMALIZATION_FACTOR)),
            provider = Provider.SCORED_CARDS)

    fun isValid(scoredCardsRequest: ScoredCardsResponseEntity): Boolean {
      val isAprValid = scoredCardsRequest.apr >= BigDecimal("0")
      val isApprovalRatingValid = scoredCardsRequest.approvalRating >= BigDecimal("0")
      val res = isAprValid && isApprovalRatingValid
      if (!res) {
        logger.error { "Apr or Approval is not valid for $scoredCardsRequest" }
      }
      return isAprValid && isApprovalRatingValid
    }
  }
}

/**
 * General response entity from microservice.
 *
 * @property provider
 * @property name
 * @property apr
 * @property cardScore
 */
data class RecommendationResponseEntity(
    val provider: String,
    val name: String,
    val apr: BigDecimal,
    val cardScore: BigDecimal
)

/**
 * Name of integration service provider.
 *
 * @property provideName
 */
enum class Provider(val provideName: String) {
  SCORED_CARDS("ScoredCards"),
  CS_CARDS("CSCards")
}
