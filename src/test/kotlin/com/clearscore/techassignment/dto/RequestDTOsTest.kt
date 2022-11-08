package com.clearscore.techassignment.dto

import com.clearscore.techassignment.creditCardsSearchRequest
import java.lang.IllegalArgumentException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class RequestDTOsTest {

  @Test
  fun `creates credit score search request`() {
    assertEquals("John Smith", creditCardsSearchRequest.name)
    assertEquals(500, creditCardsSearchRequest.creditScore)
    assertEquals(9999, creditCardsSearchRequest.salary)
  }

  @Test
  fun `throws exception on too big credit score`() {
    val tooBigCreditScoreException =
        assertThrows<IllegalArgumentException> {
          CreditCardsSearchRequest(name = "foo", creditScore = 1000, salary = 10)
        }
    assertEquals("Credit score must be between 0 and 700", tooBigCreditScoreException.message)
  }

  @Test
  fun `throws exception on negative credit score`() {
    val negativeCreditScoreException =
        assertThrows<IllegalArgumentException> {
          CreditCardsSearchRequest(name = "foo", creditScore = -1, salary = 10)
        }
    assertEquals("Credit score must be between 0 and 700", negativeCreditScoreException.message)
  }

  @Test
  fun `throws exception on negative salary`() {
    val negativeSalaryException =
        assertThrows<IllegalArgumentException> {
          CreditCardsSearchRequest(name = "foo", creditScore = 100, salary = -10)
        }
    assertEquals("Salary must be positive", negativeSalaryException.message)
  }

  @Test
  fun `maps CreditScoreSearchRequest to CSCards request`() {
    val csCardsRequest = CreditCardsSearchRequest.toCsCardsRequest(creditCardsSearchRequest)
    assertEquals("John Smith", csCardsRequest.name)
    assertEquals(500, csCardsRequest.creditScore)
  }

  @Test
  fun `maps CreditScoreSearchRequest to ScoredCards request`() {
    val scoredCardsRequest = CreditCardsSearchRequest.toScoredCardsRequest(creditCardsSearchRequest)
    assertEquals("John Smith", scoredCardsRequest.name)
    assertEquals(500, scoredCardsRequest.score)
    assertEquals(9999, scoredCardsRequest.salary)
  }
}

// todo: tests for response DTOs
// todo: rename Dto in class name to DTO
