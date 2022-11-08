package com.clearscore.techassignment.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ScoredCardsRequest(val name: String, val score: Int, val salary: Int)

data class CSCardRequest(val name: String, val creditScore: Int)

/**
 * Data class for incoming request from user.
 *
 * @property name, not empty
 * @property creditScore, between 0 and 700
 * @property salary, greater than 0
 */
data class CreditCardsSearchRequest
private constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("creditScore") val creditScore: Int,
    @JsonProperty("salary") val salary: Int
) {
  companion object {
    /**
     * Validates CreditCardsSearchRequest. Checks not empty name, creditScore range and positive
     * salary.
     *
     * @param creditCardsSearchRequest
     * @return creditCardsSearchRequest
     */
    fun validate(creditCardsSearchRequest: CreditCardsSearchRequest): CreditCardsSearchRequest {
      require(creditCardsSearchRequest.name.isNotBlank()) { "Name cannot be blank" }
      require(creditCardsSearchRequest.creditScore in 0..700) {
        "Credit score must be between 0 and 700"
      }
      require(creditCardsSearchRequest.salary > 0) { "Salary must be positive" }
      return creditCardsSearchRequest
    }

    /**
     * Invoke function which will be run on data class creation
     *
     * @param name
     * @param creditScore
     * @param salary
     * @return CreditCardsSearchRequest
     */
    operator fun invoke(name: String, creditScore: Int, salary: Int): CreditCardsSearchRequest {
      return validate(CreditCardsSearchRequest(name, creditScore, salary))
    }

    fun toCsCardsRequest(creditCardsSearchRequest: CreditCardsSearchRequest): CSCardRequest {
      return CSCardRequest(
          name = creditCardsSearchRequest.name, creditScore = creditCardsSearchRequest.creditScore)
    }

    fun toScoredCardsRequest(
        creditCardsSearchRequest: CreditCardsSearchRequest
    ): ScoredCardsRequest {
      return ScoredCardsRequest(
          name = creditCardsSearchRequest.name,
          score = creditCardsSearchRequest.creditScore,
          salary = creditCardsSearchRequest.salary)
    }
  }
}
