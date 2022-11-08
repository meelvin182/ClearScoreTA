package com.clearscore.techassignment.service.integrations.scoredcards

import com.clearscore.techassignment.dto.CreditCardsSearchRequest
import com.clearscore.techassignment.dto.IntegrationResponseEntity
import com.clearscore.techassignment.dto.ScoredCardsResponseEntity
import com.clearscore.techassignment.exceptions.ScoredCardsException
import com.clearscore.techassignment.service.integrations.CreditCardInfoIntegrationService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono

/** Integration service for ScoredCards provider. */
@Service
class ScoredCardsService(@Qualifier("ScoredCardsWebClient") private val webClient: WebClient) :
    CreditCardInfoIntegrationService {

  private val logger = KotlinLogging.logger {}

  override suspend fun getCreditCards(
      creditCardsSearchRequest: CreditCardsSearchRequest
  ): List<IntegrationResponseEntity> {
    val scoredCardsRequest = CreditCardsSearchRequest.toScoredCardsRequest(creditCardsSearchRequest)
    logger.debug { "Sending request to CSCards with body $scoredCardsRequest" }
    val resp =
        try {
          webClient
              .post()
              .bodyValue(scoredCardsRequest)
              .retrieve()
              .onStatus(
                  { httpStatus -> HttpStatus.OK != httpStatus },
                  { response -> Mono.just(ScoredCardsException(response.statusCode().name)) })
              .awaitBody<List<ScoredCardsResponseEntity>>()
        } catch (exception: Exception) {
          return catchErrorWithFallback(exception)
        }

    return resp
        .filter { ScoredCardsResponseEntity.isValid(it) }
        .map { ScoredCardsResponseEntity.toIntegrationResponse(it) }
  }

  private fun catchErrorWithFallback(exception: Exception) =
      when (exception) {
        is WebClientRequestException,
        is NoSuchElementException -> {
          logger.error { "Error sending request to ScoredCards = ${exception.localizedMessage}" }
          fallback()
        }
        is ScoredCardsException -> {
          logger.error { "ScoredCards responded with error = ${exception.localizedMessage}" }
          fallback()
        }
        else -> throw exception
      }

  private fun fallback(): List<IntegrationResponseEntity> = emptyList()
}
