package com.clearscore.techassignment.service.integrations

import com.clearscore.techassignment.dto.CreditCardsSearchRequest
import com.clearscore.techassignment.dto.IntegrationResponseEntity

/** Interface for credit card integration service. */
interface CreditCardInfoIntegrationService {
  /**
   * Gets credit cards information from the third-party integration service asynchronously. Should
   * have the fallback method in case of request or response error.
   *
   * @param creditCardsSearchRequest
   * @return List of responses from integration services
   */
  suspend fun getCreditCards(
      creditCardsSearchRequest: CreditCardsSearchRequest
  ): List<IntegrationResponseEntity>
}
