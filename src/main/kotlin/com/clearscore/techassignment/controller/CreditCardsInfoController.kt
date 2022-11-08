package com.clearscore.techassignment.controller

import com.clearscore.techassignment.dto.CreditCardsSearchRequest
import com.clearscore.techassignment.dto.IntegrationResponseEntity
import com.clearscore.techassignment.dto.RecommendationResponseEntity
import com.clearscore.techassignment.service.CreditCardsInfoProverService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreditCardsInfoController(
    private val creditCardsInfoProverService: CreditCardsInfoProverService
) {

  private val logger = KotlinLogging.logger {}

  /**
   * Entrypoint for credit card search.
   *
   * @param creditCardsSearchRequest
   * @return list of recommended credit cards with descending order.
   */
  @PostMapping("/creditcards", consumes = [MediaType.APPLICATION_JSON_VALUE])
  suspend fun getCreditCardsRecommendation(
      @RequestBody creditCardsSearchRequest: CreditCardsSearchRequest
  ): ResponseEntity<List<RecommendationResponseEntity>> {
    logger.debug { "Sending $creditCardsSearchRequest" }
    CreditCardsSearchRequest.validate(creditCardsSearchRequest)
      logger.info { "Starting to search creditCards for user ${creditCardsSearchRequest.name}" }
    val creditCardsSearchResult =
        creditCardsInfoProverService.getCreditCardInfos(creditCardsSearchRequest)
      logger.info { "Searched is finished, found ${creditCardsSearchResult.size} recommendations" }

      return ResponseEntity.ok()
        .body(
            creditCardsSearchResult.map {
              IntegrationResponseEntity.toRecommendationResponseEntity(it)
            })
  }

  @ControllerAdvice
  class MyRestExceptionHandler {
    /**
     * Handles malformed CreditCardsSearchRequest. Check for IllegalArgumentException from data
     * classes.
     *
     * @param Throwable exception
     * @return Responds with BAD_REQUEST
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequestException(ex: Throwable): ResponseEntity<String> {
      val errorResponse = ex.localizedMessage
      return ResponseEntity<String>(errorResponse, HttpStatus.BAD_REQUEST)
    }
  }
}
