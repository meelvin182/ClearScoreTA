package com.clearscore.techassignment.exceptions

/** Extendable exception for integration service. */
open class IntegrationException(message: String) : Exception(message)

class ScoredCardsException(message: String) : IntegrationException(message)

class CsCardsException(message: String) : IntegrationException(message)
