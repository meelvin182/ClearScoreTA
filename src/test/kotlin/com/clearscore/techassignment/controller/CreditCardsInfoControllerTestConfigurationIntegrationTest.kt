package com.clearscore.techassignment.controller

import com.clearscore.techassignment.creditCardsSearchRequest
import com.clearscore.techassignment.csCardsPartiallySuccessfulResponse
import com.clearscore.techassignment.csCardsSuccessfulResponse
import com.clearscore.techassignment.dto.CSCardsResponseEntity
import com.clearscore.techassignment.dto.RecommendationResponseEntity
import com.clearscore.techassignment.dto.ScoredCardsResponseEntity
import com.clearscore.techassignment.scoredCardsPartiallySuccessfulResponse
import com.clearscore.techassignment.scoredCardsSuccessfulResponse
import com.clearscore.techassignment.service.CreditCardsInfoProverService
import com.clearscore.techassignment.service.integrations.cscard.CSCardsService
import com.clearscore.techassignment.service.integrations.scoredcards.ScoredCardsService
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient

@ExtendWith(SpringExtension::class)
@Import(
    CreditCardsInfoControllerTestConfigurationIntegrationTest
        .CreditCardsInfoControllerTestConfiguration::class,
    JacksonAutoConfiguration::class)
internal class CreditCardsInfoControllerTestConfigurationIntegrationTest {

  @Autowired private lateinit var mapper: ObjectMapper

  @Autowired private lateinit var server: MockWebServer

  @Autowired private lateinit var creditCardsInfoController: CreditCardsInfoController

  @Test
  fun `returns credit cards recommendation`() {
    server.dispatcher =
        createDispatcherFromResponses(csCardsSuccessfulResponse, scoredCardsSuccessfulResponse)
    val creditCardsRecommendation: ResponseEntity<List<RecommendationResponseEntity>>
    runBlocking {
      creditCardsRecommendation =
          creditCardsInfoController.getCreditCardsRecommendation(creditCardsSearchRequest)
    }
    assertEquals(HttpStatus.OK, creditCardsRecommendation.statusCode)
    assertEquals(4, creditCardsRecommendation.body!!.size)
  }

  @Test
  fun `returns partial cards recommendation`() {
    server.dispatcher = createDispatcherFromResponses(csCardsSuccessfulResponse, emptyList())
    val creditCardsRecommendation: ResponseEntity<List<RecommendationResponseEntity>>
    runBlocking {
      creditCardsRecommendation =
          creditCardsInfoController.getCreditCardsRecommendation(creditCardsSearchRequest)
    }
    assertEquals(HttpStatus.OK, creditCardsRecommendation.statusCode)
    assertEquals(2, creditCardsRecommendation.body!!.size)
  }

  @Test
  fun `returns partial cards recommendation when scoredCards integration with timeout`() {
    server.dispatcher =
        object : Dispatcher() {
          override fun dispatch(request: RecordedRequest): MockResponse {
            if ("cs" in request.path.toString()) {
              return MockResponse()
                  .setBody(mapper.writeValueAsString(csCardsSuccessfulResponse))
                  .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            }
            if ("scored" in request.path.toString()) {
              return MockResponse()
                  .setBodyDelay(5, TimeUnit.SECONDS)
                  .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            }
            return MockResponse().setResponseCode(404)
          }
        }
    val creditCardsRecommendation: ResponseEntity<List<RecommendationResponseEntity>>

    runBlocking {
      creditCardsRecommendation =
          creditCardsInfoController.getCreditCardsRecommendation(creditCardsSearchRequest)
    }

    assertEquals(HttpStatus.OK, creditCardsRecommendation.statusCode)
    assertEquals(2, creditCardsRecommendation.body!!.size)
    assertEquals("CSCards", creditCardsRecommendation.body!![0].provider)
    assertEquals("CSCards", creditCardsRecommendation.body!![1].provider)
  }

  @Test
  fun `returns partial cards recommendation when csCards integration failed with timeout`() {
    server.dispatcher =
        object : Dispatcher() {
          override fun dispatch(request: RecordedRequest): MockResponse {
            if ("cs" in request.path.toString()) {
              return MockResponse()
                  .setBodyDelay(5, TimeUnit.SECONDS)
                  .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            }
            if ("scored" in request.path.toString()) {
              return MockResponse()
                  .setBody(mapper.writeValueAsString(scoredCardsSuccessfulResponse))
                  .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            }
            return MockResponse().setResponseCode(404)
          }
        }
    val creditCardsRecommendation: ResponseEntity<List<RecommendationResponseEntity>>

    runBlocking {
      creditCardsRecommendation =
          creditCardsInfoController.getCreditCardsRecommendation(creditCardsSearchRequest)
    }

    assertEquals(HttpStatus.OK, creditCardsRecommendation.statusCode)
    assertEquals(2, creditCardsRecommendation.body!!.size)
    assertEquals("ScoredCards", creditCardsRecommendation.body!![0].provider)
    assertEquals("ScoredCards", creditCardsRecommendation.body!![1].provider)
  }

  @Test
  fun `returns partial cards recommendation when csCards responded with not fully valid response`() {
    server.dispatcher =
        createDispatcherFromResponses(
            csCardsPartiallySuccessfulResponse, scoredCardsSuccessfulResponse)
    val creditCardsRecommendation: ResponseEntity<List<RecommendationResponseEntity>>

    runBlocking {
      creditCardsRecommendation =
          creditCardsInfoController.getCreditCardsRecommendation(creditCardsSearchRequest)
    }

    assertEquals(HttpStatus.OK, creditCardsRecommendation.statusCode)
    assertEquals(6, csCardsPartiallySuccessfulResponse.size + scoredCardsSuccessfulResponse.size)
    assertEquals(4, creditCardsRecommendation.body!!.size)
  }

  @Test
  fun `returns partial cards recommendation when scoredCards responded with not fully valid response`() {
    server.dispatcher =
        createDispatcherFromResponses(
            csCardsSuccessfulResponse, scoredCardsPartiallySuccessfulResponse)
    val creditCardsRecommendation: ResponseEntity<List<RecommendationResponseEntity>>

    runBlocking {
      creditCardsRecommendation =
          creditCardsInfoController.getCreditCardsRecommendation(creditCardsSearchRequest)
    }

    assertEquals(HttpStatus.OK, creditCardsRecommendation.statusCode)
    assertEquals(6, csCardsPartiallySuccessfulResponse.size + scoredCardsSuccessfulResponse.size)
    assertEquals(4, creditCardsRecommendation.body!!.size)
  }

  private fun createDispatcherFromResponses(
      csCardResponse: List<CSCardsResponseEntity>,
      scoredCardsResponse: List<ScoredCardsResponseEntity>
  ): Dispatcher {
    return object : Dispatcher() {
      override fun dispatch(request: RecordedRequest): MockResponse {
        if ("cs" in request.path.toString()) {
          return MockResponse()
              .setBody(mapper.writeValueAsString(csCardResponse))
              .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
        if ("scored" in request.path.toString()) {
          return MockResponse()
              .setBody(mapper.writeValueAsString(scoredCardsResponse))
              .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
        return MockResponse().setResponseCode(404)
      }
    }
  }

  @TestConfiguration
  @Import(JacksonAutoConfiguration::class)
  class CreditCardsInfoControllerTestConfiguration {

    @Bean
    fun webServer(mapper: ObjectMapper): MockWebServer {
      return MockWebServer()
    }

    @Bean("scoredWebClient")
    fun scoredWebClient(webServer: MockWebServer): WebClient {
      return WebClient.builder().baseUrl(webServer.url("/scored").toString()).build()
    }

    @Bean("CsWebClient")
    fun csWebClient(webServer: MockWebServer): WebClient {
      return WebClient.builder().baseUrl(webServer.url("/cs").toString()).build()
    }

    @Bean
    fun scoredCardsService(@Qualifier("scoredWebClient") webClient: WebClient): ScoredCardsService {
      return ScoredCardsService(webClient)
    }

    @Bean
    fun csCardsService(@Qualifier("CsWebClient") webClient: WebClient): CSCardsService {
      return CSCardsService(webClient)
    }

    @Bean
    fun creditCardsInfoProverService(
        csCardsService: CSCardsService,
        scoredCardsService: ScoredCardsService
    ): CreditCardsInfoProverService {
      return CreditCardsInfoProverService(listOf(csCardsService, scoredCardsService))
    }

    @Bean
    fun creditCardsInfoController(
        creditCardsInfoProverService: CreditCardsInfoProverService
    ): CreditCardsInfoController {
      return CreditCardsInfoController(creditCardsInfoProverService)
    }
  }
}
