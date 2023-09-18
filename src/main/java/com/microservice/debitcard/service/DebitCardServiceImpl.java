package com.microservice.debitcard.service;

import com.microservice.debitcard.documents.DebitCardDocument;
import com.microservice.debitcard.model.DebitCardRequest;
import com.microservice.debitcard.repository.DebitCardRepository;
import com.microservice.debitcard.util.AccountDto;
import com.microservice.debitcard.util.ClientDto;
import com.microservice.debitcard.util.dto.DebitCardDto;
import com.microservice.debitcard.util.dto.SecondaryAccountDto;
import com.microservice.debitcard.util.mapper.MapperDebit;
import com.microservice.debitcard.webclient.AccountsWebClient;
import com.microservice.debitcard.webclient.ClientWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DebitCardServiceImpl implements DebitCardService{

  @Autowired
  private DebitCardRepository debitCardRepository;

  @Autowired
  private ClientWebClient clientWebClient;

  @Autowired
  private AccountsWebClient accountsWebClient;

  @Autowired
  private MapperDebit mapper;


  @Override
  public Mono<DebitCardDto> createDebitCard(DebitCardRequest debitCardRequest) {

    DebitCardDocument debitCardDocument = mapper.mapDebitRequestToDebitDocument(debitCardRequest);
    debitCardDocument.setDebitCardDate(LocalDate.now());

    Mono<DebitCardDocument> debitCardDocumentMono = debitCardRepository.save(debitCardDocument);

    Flux<AccountDto> accountDtoFlux = getAccounts(debitCardRequest.getClientDocument());


    Mono<Double> amountMono = getAmountMainAccount(accountDtoFlux, debitCardRequest.getMainAccount());

    return debitCardDocumentMono.zipWith(amountMono).map(tuple -> {
      DebitCardDto debitCardDto = mapper.mapDebitDocumentToDebitDto(tuple.getT1());
      debitCardDto.setAmount(tuple.getT2());

      return debitCardDto;
    });
  }

  @Override
  public Mono<DebitCardDto> addSecondaryAccounts(String secondaryAccount, String debitCardNumber, String clientDocument) {

    Mono<DebitCardDto> debitCardDtoMono = getDebitCard(debitCardNumber, clientDocument);

    return debitCardDtoMono.flatMap(debitCardDto -> {

      DebitCardDto cardDto = debitCardDto;

      SecondaryAccountDto secondaryAccountDto = new SecondaryAccountDto();
      secondaryAccountDto.setAccountNumber(secondaryAccount);

      List<SecondaryAccountDto> secondaryAccounts = cardDto.getSecondaryAccounts();

      if(secondaryAccounts == null){
        secondaryAccounts = new ArrayList<>();
      }

      secondaryAccounts.add(secondaryAccountDto);

      cardDto.setSecondaryAccounts(secondaryAccounts);

      DebitCardDocument debitCardDocument = mapper.mapDebitDtoToDebitDocument(cardDto);

      Mono<DebitCardDocument> debitCardDocumentUpdated = debitCardRepository.save(debitCardDocument);

      return debitCardDocumentUpdated.map(debitDocUpdated -> mapper.mapDebitDocumentToDebitDto(debitDocUpdated));

    });
  }

  @Override
  public Mono<DebitCardDto> getDebitCard(String debitCardNumber, String clientDocument) {

    Mono<DebitCardDocument> debitCardDocumentMono = debitCardRepository.findByDebitCardNumber(debitCardNumber);

    return debitCardDocumentMono.flatMap(debitCard -> {

      Flux<AccountDto> accountDtoFlux = getAccounts(debitCard.getClientDocument());
      Mono<Double> amountMono = getAmountMainAccount(accountDtoFlux, debitCard.getMainAccount());

      return amountMono.map(aDouble -> {

        DebitCardDto debitCardDto = mapper.mapDebitDocumentToDebitDto(debitCard);
        debitCardDto.setAmount(aDouble);

        return debitCardDto;
      });

    });
  }

  @Override
  public Mono<ClientDto> getClient(String clientDocument) {
    return clientWebClient.getClient(clientDocument);
  }

  @Override
  public Mono<Boolean> existDebitCard(String debitCardNumber) {
    return debitCardRepository.existsById(debitCardNumber);
  }

  @Override
  public Flux<AccountDto> getAccounts(String clientDocument) {
    return accountsWebClient.getAccountsByClient(clientDocument);
  }

  @Override
  public Mono<Boolean> isOwnerOfAccount(Flux<AccountDto> accountDtoFlux, String accountNumber) {

    return accountDtoFlux
            .any(accountDto -> accountDto.getAccountNumber().equalsIgnoreCase(accountNumber));
  }

  @Override
  public Mono<Double> getAmountMainAccount(Flux<AccountDto> accountDtoFlux, String mainAccount) {

    Mono<AccountDto> accountDtoMono = accountDtoFlux
            .filter(accountDto -> accountDto.getAccountNumber().equalsIgnoreCase(mainAccount)).next();

    return accountDtoMono.map(AccountDto::getAccountAmount);
  }
}
