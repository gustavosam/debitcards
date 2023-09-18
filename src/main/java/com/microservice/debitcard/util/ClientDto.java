package com.microservice.debitcard.util;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Esta clase contendrá los atributos que serán llamados
 * desde el microservicio de cliente.
 * */
@Getter
@Setter
public class ClientDto {

  private String document;

  private String name;

  private String clientType;

  private String email;

  private Boolean isActive;

  private Boolean expiredDebt;

  private LocalDate clientCreationDate;
}
