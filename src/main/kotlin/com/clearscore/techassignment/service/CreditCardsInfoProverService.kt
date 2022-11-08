package com.clearscore.techassignment.service

import com.clearscore.techassignment.dto.CreditCardsSearchRequest
import com.clearscore.techassignment.dto.IntegrationResponseEntity
import com.clearscore.techassignment.service.integrations.CreditCardInfoIntegrationService
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class CreditCardsInfoProverService(
    private val integrationServices: List<CreditCardInfoIntegrationService>
) {
  /**
   * Integrations caller function, it calls all integration services in asynchronous manner (without
   * blocking the thread). Then it sorts it by eligibility rating in descending order.
   *
   * @param searchRequest
   * @return List of IntegrationResponseEntity
   */
  suspend fun getCreditCardInfos(
      searchRequest: CreditCardsSearchRequest
  ): List<IntegrationResponseEntity> {
    return coroutineScope {
      integrationServices
          .map { cardInfoService ->
            async(start = CoroutineStart.LAZY) { cardInfoService.getCreditCards(searchRequest) }
          }
          .awaitAll()
          .flatten()
          .sortedByDescending { it.normalizedEligibility }
    }
  }
}
