package com.microservice.debitcard.webclient;

import com.microservice.debitcard.util.AccountDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class AccountsWebClient {

  @Autowired
  private WebClient.Builder webClientBuilder;

  @CircuitBreaker(name = "circuitBreakerAccount", fallbackMethod = "fallbackMethod")
  public Flux<AccountDto> getAccountsByClient(String document){
    return webClientBuilder.build()
            .get()
            .uri("http://localhost:8083/account/client/{document}", document)
            .retrieve()
            .bodyToFlux(AccountDto.class);
  }

  public Flux<AccountDto> fallbackMethod(String document, Exception ex) {

    return Flux.empty();
  }
}
