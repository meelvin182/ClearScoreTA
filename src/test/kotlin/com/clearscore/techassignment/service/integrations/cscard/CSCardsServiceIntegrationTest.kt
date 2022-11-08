package com.clearscore.techassignment.service.integrations.cscard

import com.clearscore.techassignment.creditCardsSearchRequest
import com.clearscore.techassignment.csCardsPartiallySuccessfulResponse
import com.clearscore.techassignment.csCardsSuccessfulResponse
import com.clearscore.techassignment.dto.IntegrationResponseEntity
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.assertEquals
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
    CSCardsServiceIntegrationTest.CSCardServiceTestConfig::class, JacksonAutoConfiguration::class)
internal class CSCardsServiceIntegrationTest {

  @Autowired private lateinit var mapper: ObjectMapper

  @Autowired private lateinit var server: MockWebServer

  @Autowired private lateinit var csCardsService: CSCardsService

  @Test
  fun `returns credit card infos`() {
    server.enqueue(
        MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody(mapper.writeValueAsString(csCardsSuccessfulResponse)))
    val prevRequestCount = server.requestCount
    runBlocking {
      // https://github.com/square/okhttp/pull/6736 still not released :(
      val creditCards = csCardsService.getCreditCards(creditCardsSearchRequest)

      assertEquals(1, server.requestCount - prevRequestCount)
      assertEquals(2, creditCards.size)
    }
  }

  @Test
  fun `does not return malformed credit card infos`() {
    server.enqueue(
        MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody(mapper.writeValueAsString(csCardsPartiallySuccessfulResponse)))
    val prevRequestCount = server.requestCount
    val creditCards: List<IntegrationResponseEntity>

    runBlocking {
      // https://github.com/square/okhttp/pull/6736 still not released :(
      creditCards = csCardsService.getCreditCards(creditCardsSearchRequest)
    }

    assertEquals(4, csCardsPartiallySuccessfulResponse.size)
    assertEquals(1, server.requestCount - prevRequestCount)
    assertEquals(2, creditCards.size)
  }

  @Test
  fun `returns empty list when failed response`() {
    server.enqueue(MockResponse().setResponseCode(400))
    val prevRequestCount = server.requestCount
    val creditCards: List<IntegrationResponseEntity>

    runBlocking { creditCards = csCardsService.getCreditCards(creditCardsSearchRequest) }

    assertEquals(1, server.requestCount - prevRequestCount)
    assertEquals(0, creditCards.size)
  }

  @Test
  fun `returns empty list when timeout`() {
    server.enqueue(MockResponse().setBodyDelay(5, TimeUnit.SECONDS))
    val prevRequestCount = server.requestCount
    val creditCards: List<IntegrationResponseEntity>

    runBlocking { creditCards = csCardsService.getCreditCards(creditCardsSearchRequest) }

    assertEquals(1, server.requestCount - prevRequestCount)
    assertEquals(0, creditCards.size)
  }

  @TestConfiguration
  class CSCardServiceTestConfig {

    @Bean
    fun webServer(): MockWebServer {
      return MockWebServer()
    }

    @Bean
    fun webClient(webServer: MockWebServer): WebClient {
      return WebClient.builder().baseUrl(webServer.url("").toString()).build()
    }

    @Bean
    fun csCardsService(webClient: WebClient): CSCardsService {
      return CSCardsService(webClient)
    }
  }
}
