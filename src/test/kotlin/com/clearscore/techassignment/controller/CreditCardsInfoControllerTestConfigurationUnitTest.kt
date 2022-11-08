package com.clearscore.techassignment.controller

import com.clearscore.techassignment.creditCardsSearchRequest
import com.clearscore.techassignment.csCardResponseEntity1
import com.clearscore.techassignment.csCardResponseEntity2
import com.clearscore.techassignment.dto.RecommendationResponseEntity
import com.clearscore.techassignment.scoredCardResponseEntity1
import com.clearscore.techassignment.scoredCardResponseEntity2
import com.clearscore.techassignment.service.CreditCardsInfoProverService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@ExtendWith(MockKExtension::class)
internal class CreditCardsInfoControllerTestConfigurationUnitTest {

  @MockK private lateinit var creditCardsInfoProverService: CreditCardsInfoProverService

  @InjectMockKs private lateinit var creditCardsInfoController: CreditCardsInfoController

  @Test
  fun `gets cards recommendation`() {
    every { runBlocking { creditCardsInfoProverService.getCreditCardInfos(any()) } } returns
        listOf(
            csCardResponseEntity1,
            csCardResponseEntity2,
            scoredCardResponseEntity1,
            scoredCardResponseEntity2)
    val creditCardsRecommendation: ResponseEntity<List<RecommendationResponseEntity>>
    runBlocking {
      creditCardsRecommendation =
          creditCardsInfoController.getCreditCardsRecommendation(creditCardsSearchRequest)
    }
    assertEquals(4, creditCardsRecommendation.body?.size)
    assertEquals(HttpStatus.OK, creditCardsRecommendation.statusCode)
  }
}
