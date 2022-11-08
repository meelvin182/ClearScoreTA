package com.clearscore.techassignment.service.integrations.scoredcards

import com.clearscore.techassignment.creditCardsSearchRequest
import com.clearscore.techassignment.csCardsPartiallySuccessfulResponse
import com.clearscore.techassignment.dto.IntegrationResponseEntity
import com.clearscore.techassignment.scoredCardsPartiallySuccessfulResponse
import com.clearscore.techassignment.scoredCardsSuccessfulResponse
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient

@ExtendWith(SpringExtension::class)
@Import(
    ScoredCardsServiceIntegrationTest.ScoredCardsServiceTestConfig::class,
    JacksonAutoConfiguration::class)
internal class ScoredCardsServiceIntegrationTest {

  @Autowired private lateinit var mapper: ObjectMapper

  @Autowired private lateinit var server: MockWebServer

  @Autowired private lateinit var scoredCardsService: ScoredCardsService

  @Test
  fun `returns credit card infos`() {
    server.enqueue(
        MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody(mapper.writeValueAsString(scoredCardsSuccessfulResponse)))

    // https://github.com/square/okhttp/pull/6736 still not released :-(
    val prevRequestCount = server.requestCount
    val creditCards: List<IntegrationResponseEntity>

    runBlocking { creditCards = scoredCardsService.getCreditCards(creditCardsSearchRequest) }
    Assertions.assertEquals(1, server.requestCount - prevRequestCount)
    Assertions.assertEquals(2, creditCards.size)
  }

  @Test
  fun `does not return malformed credit card infos`() {
    server.enqueue(
        MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody(mapper.writeValueAsString(scoredCardsPartiallySuccessfulResponse)))
    val prevRequestCount = server.requestCount
    val creditCards: List<IntegrationResponseEntity>

    runBlocking { creditCards = scoredCardsService.getCreditCards(creditCardsSearchRequest) }

    Assertions.assertEquals(4, csCardsPartiallySuccessfulResponse.size)
    Assertions.assertEquals(1, server.requestCount - prevRequestCount)
    Assertions.assertEquals(2, creditCards.size)
  }

  @Test
  fun `returns empty list when failed response`() {
    server.enqueue(MockResponse().setResponseCode(400))
    val prevRequestCount = server.requestCount
    val creditCards: List<IntegrationResponseEntity>

    runBlocking { creditCards = scoredCardsService.getCreditCards(creditCardsSearchRequest) }

    Assertions.assertEquals(1, server.requestCount - prevRequestCount)
    Assertions.assertEquals(0, creditCards.size)
  }

  @Test
  fun `returns empty list when timeout`() {
    server.enqueue(MockResponse().setBodyDelay(5, TimeUnit.SECONDS))
    val prevRequestCount = server.requestCount
    val creditCards: List<IntegrationResponseEntity>

    runBlocking { creditCards = scoredCardsService.getCreditCards(creditCardsSearchRequest) }

    Assertions.assertEquals(1, server.requestCount - prevRequestCount)
    Assertions.assertEquals(0, creditCards.size)
  }

  @TestConfiguration
  class ScoredCardsServiceTestConfig {

    @Bean
    fun webServer(): MockWebServer {
      return MockWebServer()
    }

    @Bean
    fun webClient(webServer: MockWebServer): WebClient {
      return WebClient.builder().baseUrl(webServer.url("").toString()).build()
    }

    @Bean
    fun scoredCardsService(webClient: WebClient): ScoredCardsService {
      return ScoredCardsService(webClient)
    }
  }
}
