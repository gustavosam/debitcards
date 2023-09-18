package com.microservice.debitcard.service;

import com.microservice.debitcard.model.DebitCardRequest;
import com.microservice.debitcard.util.AccountDto;
import com.microservice.debitcard.util.ClientDto;
import com.microservice.debitcard.util.dto.DebitCardDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DebitCardService {

  Mono<DebitCardDto> createDebitCard(DebitCardRequest debitCardRequest);

  Mono<DebitCardDto> addSecondaryAccounts(String secondaryAccount, String debitCardNumber, String clientDocument);

  Mono<DebitCardDto> getDebitCard(String debitCardNumber, String clientDocument);

  Mono<ClientDto> getClient(String clientDocument);

  Mono<Boolean> existDebitCard(String debitCardNumber);

  Flux<AccountDto> getAccounts(String clientDocument);

  Mono<Boolean> isOwnerOfAccount(Flux<AccountDto> accountDtoFlux, String accountNumber);

  Mono<Double> getAmountMainAccount(Flux<AccountDto> accountDtoFlux, String mainAccount);
}
