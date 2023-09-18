package com.microservice.debitcard.repository;


import com.microservice.debitcard.documents.DebitCardDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface DebitCardRepository extends ReactiveMongoRepository<DebitCardDocument, String> {

  Mono<DebitCardDocument> findByDebitCardNumber(String debitCardNumber);
}
