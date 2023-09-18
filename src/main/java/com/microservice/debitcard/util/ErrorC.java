package com.microservice.debitcard.util;

import com.microservice.debitcard.model.DebitCard;
import lombok.Getter;
import lombok.Setter;

/**
 * Esta clase extiende de DebitCard.
 * */
@Getter
@Setter
public class ErrorC extends DebitCard {

  private static ErrorC instance;

  private ErrorC() {

  }

  /**
   * Solo se puede crear una sola instancia de esta clase.
   *  * Esa instancia contendrá un mensaje que se indicará por parámetro cuando sea llamada.
   * */
  public static ErrorC getInstance(String mensaje) {

    if (instance == null) {
      instance = new ErrorC();
    }
    instance.setMessage(mensaje);
    return instance;
  }
}
