package com.clearscore.techassignment.utils

import com.clearscore.techassignment.dto.IntegrationResponseEntity
import java.math.BigDecimal
import java.math.RoundingMode

private const val TWO_DECIMAL_PLACES = 3

/**
 * Calculates credit card score. sortingScore = eligibility * ((1/apr)^2)
 *
 * @param integrationResponseEntity
 * @return credit card score
 */
fun calculateCardScore(integrationResponseEntity: IntegrationResponseEntity): BigDecimal =
    integrationResponseEntity.normalizedEligibility.divide(
        integrationResponseEntity.apr * integrationResponseEntity.apr,
        TWO_DECIMAL_PLACES,
        RoundingMode.FLOOR)
