package com.clearscore.techassignment.service

import com.clearscore.techassignment.creditCardsSearchRequest
import com.clearscore.techassignment.csCardResponseEntity1
import com.clearscore.techassignment.csCardResponseEntity2
import com.clearscore.techassignment.dto.IntegrationResponseEntity
import com.clearscore.techassignment.scoredCardResponseEntity1
import com.clearscore.techassignment.scoredCardResponseEntity2
import com.clearscore.techassignment.service.integrations.CreditCardInfoIntegrationService
import com.clearscore.techassignment.service.integrations.cscard.CSCardsService
import com.clearscore.techassignment.service.integrations.scoredcards.ScoredCardsService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class CreditCardsInfoProverServiceTest {

  @MockK private lateinit var csCardsService: CSCardsService

  @MockK private lateinit var scoredCardsService: ScoredCardsService

  private lateinit var creditCardsInfoProverService: CreditCardsInfoProverService

  @BeforeEach
  fun setup() {
    MockKAnnotations.init(this)
    creditCardsInfoProverService =
        CreditCardsInfoProverService(listOf(csCardsService, scoredCardsService))
  }

  @Test
  fun `collects credit card infos`() {
    mockProviderResponse(csCardsService, listOf(csCardResponseEntity1, csCardResponseEntity2))
    mockProviderResponse(
        scoredCardsService, listOf(scoredCardResponseEntity1, scoredCardResponseEntity2))

    runBlocking {
      val creditCardInfos =
          creditCardsInfoProverService.getCreditCardInfos(creditCardsSearchRequest)

      assertEquals(4, creditCardInfos.size)
    }
  }

  @Test
  fun `collects credit card infos when integration services return empty lists`() {
    mockProviderResponse(csCardsService, listOf())
    mockProviderResponse(scoredCardsService, listOf())

    runBlocking {
      val creditCardInfos =
          creditCardsInfoProverService.getCreditCardInfos(creditCardsSearchRequest)

      assertEquals(0, creditCardInfos.size)
    }
  }

  @Test
  fun `collects credit card infos when CsCards service returns empty list`() {
    mockProviderResponse(csCardsService, listOf())
    mockProviderResponse(
        scoredCardsService, listOf(scoredCardResponseEntity1, scoredCardResponseEntity2))

    runBlocking {
      val creditCardInfos =
          creditCardsInfoProverService.getCreditCardInfos(creditCardsSearchRequest)

      assertEquals(2, creditCardInfos.size)
    }
  }

  @Test
  fun `collects credit card infos when scored cards service returns empty list`() {
    mockProviderResponse(csCardsService, listOf(csCardResponseEntity1, csCardResponseEntity2))
    mockProviderResponse(scoredCardsService, listOf())

    runBlocking {
      val creditCardInfos =
          creditCardsInfoProverService.getCreditCardInfos(creditCardsSearchRequest)

      assertEquals(2, creditCardInfos.size)
    }
  }

  @Test
  fun `has descending order of credit card infos`() {
    mockProviderResponse(csCardsService, listOf(csCardResponseEntity1, csCardResponseEntity2))
    mockProviderResponse(
        scoredCardsService, listOf(scoredCardResponseEntity1, scoredCardResponseEntity2))
    var creditCardInfos: List<IntegrationResponseEntity>

    runBlocking {
      creditCardInfos = creditCardsInfoProverService.getCreditCardInfos(creditCardsSearchRequest)
    }

    assertTrue(creditCardInfos[0].normalizedEligibility > creditCardInfos[1].normalizedEligibility)
    assertTrue(creditCardInfos[1].normalizedEligibility > creditCardInfos[2].normalizedEligibility)
    assertTrue(creditCardInfos[2].normalizedEligibility > creditCardInfos[3].normalizedEligibility)
  }

  private fun mockProviderResponse(
      creditCardInfoIntegrationService: CreditCardInfoIntegrationService,
      responseEntities: List<IntegrationResponseEntity>
  ) {
    every { runBlocking { creditCardInfoIntegrationService.getCreditCards(any()) } } returns
        responseEntities
  }
}
