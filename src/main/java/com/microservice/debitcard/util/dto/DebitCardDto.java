package com.microservice.debitcard.util.dto;

import com.microservice.debitcard.model.DebitCard;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DebitCardDto extends DebitCard {

  private String debitCardNumber;

  private String mainAccount;

  private Double amount;

  private List<SecondaryAccountDto> secondaryAccounts;

  private String clientDocument;

  private LocalDate debitCardDate;
}
