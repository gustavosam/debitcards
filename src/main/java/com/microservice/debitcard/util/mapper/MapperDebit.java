package com.microservice.debitcard.util.mapper;

import com.microservice.debitcard.documents.DebitCardDocument;
import com.microservice.debitcard.model.DebitCardRequest;
import com.microservice.debitcard.util.dto.DebitCardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperDebit {

  //@Mapping(target = "mainAccount", source = "debitCardRequest.mainAccount")
  //@Mapping(target = "clientDocument", source = "debitCardRequest.clientDocument")
  DebitCardDocument mapDebitRequestToDebitDocument(DebitCardRequest debitCardRequest);

  //@Mapping(target = "debitCardNumber", source = "debitCardDocument.debitCardNumber")
  //@Mapping(target = "mainAccount", source = "debitCardDocument.mainAccount")
  //@Mapping(target = "clientDocument", source = "debitCardDocument.clientDocument")
  //@Mapping(target = "debitCardDate", source = "debitCardDocument.debitCardDate")
  DebitCardDto mapDebitDocumentToDebitDto(DebitCardDocument debitCardDocument);

  DebitCardDocument mapDebitDtoToDebitDocument(DebitCardDto debitCardDto);
}
