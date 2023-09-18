package com.microservice.debitcard.delegate;

import com.microservice.debitcard.api.DebitcardApiDelegate;
import com.microservice.debitcard.model.DebitCard;
import com.microservice.debitcard.model.DebitCardRequest;
import com.microservice.debitcard.model.SecondaryAccountRequest;
import com.microservice.debitcard.service.DebitCardService;
import com.microservice.debitcard.util.AccountDto;
import com.microservice.debitcard.util.ClientDto;
import com.microservice.debitcard.util.Constants;
import com.microservice.debitcard.util.ErrorC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DebitCardDelegateImpl implements DebitcardApiDelegate {

  @Autowired
  private DebitCardService debitCardService;

  @Override
  public Mono<ResponseEntity<DebitCard>> createDebitCard(Mono<DebitCardRequest> debitCardRequest,
                                                         ServerWebExchange exchange){

    return debitCardRequest.flatMap(debitCard -> {

      if(debitCard.getClientDocument() == null){
        return Mono.just(ResponseEntity.badRequest().body(ErrorC.getInstance(Constants.DOCUMENT_EMPTY)));
      }

      if(debitCard.getMainAccount() == null){
        return Mono.just(ResponseEntity.badRequest().body(ErrorC.getInstance(Constants.MAIN_ACCOUNT_EMPTY)));
      }

      Mono<ClientDto> clientDtoMono = debitCardService.getClient(debitCard.getClientDocument());

      return clientDtoMono.flatMap(clientDto -> {

        if(clientDto.getDocument() == null){
          return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorC.getInstance("PROBLEMS WITH SERVICE CLIENT")));
        }

        if(clientDto.getDocument().equalsIgnoreCase("NOT_EXIST")){
          return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorC.getInstance(Constants.CLIENT_NOT_EXIST)));
        }

        if(clientDto.getExpiredDebt()){
          return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorC.getInstance(Constants.DEBT_EXIST)));
        }

        Flux<AccountDto> accountDtoFlux = debitCardService.getAccounts(clientDto.getDocument());

        Mono<Boolean> isOwner = debitCardService.isOwnerOfAccount(accountDtoFlux, debitCard.getMainAccount());

        return isOwner.flatMap(owner -> {

          if(!owner){
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorC.getInstance(Constants.NOT_OWNER)));
          }

          return debitCardService.createDebitCard(debitCard).map(ResponseEntity::ok);

        });
      });
    });
  }

  @Override
  public Mono<ResponseEntity<DebitCard>> addSecondaryAccounts(Mono<SecondaryAccountRequest> secondaryAccountRequest,
                                                              ServerWebExchange exchange){

    return secondaryAccountRequest.flatMap(accountRequest -> {

      if(accountRequest.getAccountNumber() == null){
        return Mono.just(ResponseEntity.badRequest().body(ErrorC.getInstance(Constants.ACCOUNT_EMPTY)));
      }

      if(accountRequest.getClientDocument() == null){
        return Mono.just(ResponseEntity.badRequest().body(ErrorC.getInstance(Constants.DOCUMENT_EMPTY)));
      }

      if(accountRequest.getDebitCardNumber() == null){
        return Mono.just(ResponseEntity.badRequest().body(ErrorC.getInstance(Constants.DEBIT_CARD_EMPTY)));
      }

      Mono<ClientDto> clientDtoMono = debitCardService.getClient(accountRequest.getClientDocument());

      return clientDtoMono.flatMap(clientDto -> {

        if(clientDto.getDocument() == null){
          return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorC.getInstance("PROBLEMS WITH SERVICE CLIENT")));
        }

        if(clientDto.getDocument().equalsIgnoreCase("NOT_EXIST")){
          return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorC.getInstance(Constants.CLIENT_NOT_EXIST)));
        }

        Flux<AccountDto> accountDtoFlux = debitCardService.getAccounts(clientDto.getDocument());

        Mono<Boolean> isOwner = debitCardService.isOwnerOfAccount(accountDtoFlux, accountRequest.getAccountNumber());

        return isOwner.flatMap(owner -> {

          if(!owner){
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorC.getInstance(Constants.NOT_OWNER)));
          }

          Mono<Boolean> debitCardExist = debitCardService.existDebitCard(accountRequest.getDebitCardNumber());

          return debitCardExist.flatMap(exist -> {

            if(!exist){
              return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorC.getInstance(Constants.DEBIT_CARD_NOT_EXIST)));
            }

            return debitCardService
                    .addSecondaryAccounts(accountRequest.getAccountNumber(), accountRequest.getDebitCardNumber(), accountRequest.getClientDocument())
                    .map(ResponseEntity::ok);

          });
        });
      });
    });
  }

  @Override
  public Mono<ResponseEntity<DebitCard>> getDebitCard(String debitCardNumber,
                                                      String clientDocument,
                                                      ServerWebExchange exchange){

    return debitCardService.getDebitCard(debitCardNumber, clientDocument).map(ResponseEntity::ok);

  }
}