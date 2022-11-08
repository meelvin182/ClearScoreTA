package com.clearscore.techassignment.service.integrations.cscard

import com.clearscore.techassignment.dto.CSCardsResponseEntity
import com.clearscore.techassignment.dto.CreditCardsSearchRequest
import com.clearscore.techassignment.dto.IntegrationResponseEntity
import com.clearscore.techassignment.exceptions.CsCardsException
import com.clearscore.techassignment.service.integrations.CreditCardInfoIntegrationService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono

/** Integration service for CSCards provider. */
@Service
class CSCardsService(@Qualifier("CSCardsWebClient") private val webClient: WebClient) :
    CreditCardInfoIntegrationService {

  private val logger = KotlinLogging.logger {}

  override suspend fun getCreditCards(
      creditCardsSearchRequest: CreditCardsSearchRequest
  ): List<IntegrationResponseEntity> {
    val csCardsRequest = CreditCardsSearchRequest.toCsCardsRequest(creditCardsSearchRequest)
    logger.debug { "Sending request to CSCards with body $csCardsRequest" }
    val response =
        try {
          webClient
              .post()
              .bodyValue(csCardsRequest)
              .retrieve()
              .onStatus(
                  { httpStatus -> HttpStatus.OK != httpStatus },
                  { response -> Mono.just(CsCardsException(response.statusCode().name)) })
              .awaitBody<List<CSCardsResponseEntity>>()
        } catch (exception: Exception) {
          return catchErrorWithFallback(exception)
        }
    return response
        .filter { CSCardsResponseEntity.isValid(it) }
        .map { CSCardsResponseEntity.toIntegrationResponse(it) }
  }

  private fun catchErrorWithFallback(exception: Exception) =
      when (exception) {
        is WebClientRequestException,
        is NoSuchElementException -> {
          logger.error { "Error sending request to CSCards = ${exception.localizedMessage}" }
          fallback()
        }
        is CsCardsException -> {
          logger.error { "CSCards responded with error = ${exception.localizedMessage}" }
          fallback()
        }
        else -> throw exception
      }

  private fun fallback(): List<IntegrationResponseEntity> = emptyList()
}
