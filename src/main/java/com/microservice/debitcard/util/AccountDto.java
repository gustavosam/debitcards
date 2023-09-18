package com.microservice.debitcard.util;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Clase que contendrá información traida por el microservicio de las cuentas.
 * */
@Getter
@Setter
public class AccountDto {

  private String accountNumber;

  private String accountType;

  private Double accountAmount;

  private Integer quantityMovements;

  private String clientDocument;

  private LocalDate creationDate;

}
