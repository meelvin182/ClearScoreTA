package com.clearscore.techassignment.configuration

import java.time.Duration
import mu.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

/**
 * Default configuration for CsCard integration.
 *
 * @property apiUrl will be passed from env variables.
 * @property timeOut in seconds
 */
@Configuration
@ConfigurationProperties(prefix = "api.cscard")
data class CsCardConfigurationProperties(var apiUrl: String = "", var timeOutMS: Long = 1000)

/**
 * Default configuration for ScoredCards integration.
 *
 * @property apiUrl will be passed from env variables.
 * @property timeOut in seconds
 */
@Configuration
@ConfigurationProperties(prefix = "api.scoredcards")
data class ScoredCardsConfigurationProperties(var apiUrl: String = "", var timeOutMS: Long = 1000)

@Configuration
class WebClientBuilderConfig(
    private val csCardConfigurationProperties: CsCardConfigurationProperties,
    private val scoredCardsConfigurationProperties: ScoredCardsConfigurationProperties
) {

  private val logger = KotlinLogging.logger {}

  /** Create webclient for csCards provider with timeout and default headers. */
  @Bean("CSCardsWebClient")
  fun cSCardsWebClient(): WebClient {
    val httpClient =
        HttpClient.create()
            .responseTimeout(Duration.ofMillis(csCardConfigurationProperties.timeOutMS))

    return WebClient.builder()
        .filters { exchangeFilterFunctions -> exchangeFilterFunctions.add(logRequest()) }
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .baseUrl(csCardConfigurationProperties.apiUrl)
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .build()
  }

  /** Create webclient for scoredCards provider with timeout and default headers. */
  @Bean("ScoredCardsWebClient")
  fun scoredCardsWebClient(): WebClient {
    val httpClient =
        HttpClient.create()
            .responseTimeout(Duration.ofMillis(scoredCardsConfigurationProperties.timeOutMS))

    return WebClient.builder()
        .filters { exchangeFilterFunctions -> exchangeFilterFunctions.add(logRequest()) }
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .baseUrl(scoredCardsConfigurationProperties.apiUrl)
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .build()
  }

  private fun logRequest(): ExchangeFilterFunction {
    return ExchangeFilterFunction.ofRequestProcessor { req ->
      logger.debug { "Request ${req.method()} ${req.url()}" }
      logger.debug { "Request body ${req.body()}" }
      req.headers().forEach { headerName, values ->
        values.forEach { headerVal -> logger.debug { "$headerName:$headerVal" } }
      }
      Mono.just(req)
    }
  }
}
