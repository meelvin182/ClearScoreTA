
# How to run
You have 2 options to run the service
- Export the variables discussed in the assignment and run `./start.sh` (you need docker for it, but no dependency on installed JVM)
- Export the variables discussed in the assignment and run `start_with_gradle_only.sh`, the result can be unstable and depends on JVM installed on host machine

## Export varialbes
To export variables for local execution simply run with your input:
```sh
export CSCARDS_ENDPOINT="<your-api-cscard-endpoint>"
export SCOREDCARDS_ENDPOINT="<your-api-score-cards-endpoint>"
export HTTP_PORT="<your-http-port>"
```

## Overall design
The microservice is written using Kotlin and SpringBoot and heavily relies on feature of this language for async programming - [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
1. The entrypoint for the system in `CreditCardsInfoController`
2. It launches the service `CreditCardsInfoProverService` which is responsible for calling all integrations
3. `CreditCardsInfoProverService` asynchronously gathers the responses from the providers
(in current case only 2, but can be easily extended for N)
4. If the provider takes too long to respond (timeout is configurable in `application.yml`) or responded with an error, default fallback method is called (For real life services it makes sense to use Netflix Hystrix, or spring cloud circuit breaker)
5. The data classes are responsible for their own transformation to other data class, 
so in case we would need to add another integration service, we could have added webClient, integrationService, and add DTO mappings 
8. Due to creation of Dockerfile the application can be deployed to Kube/ECS/EKS/Cloudrun/\<insert your service name\>
9. The App is stateless so multiple replicas can be launched as well
