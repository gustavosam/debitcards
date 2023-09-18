package com.microservice.debitcard.documents;

import com.microservice.debitcard.util.complementary.SecondaryAccount;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "debitcards")
@Getter
@Setter
public class DebitCardDocument {

  @Id
  private String debitCardNumber;

  private String mainAccount;

  private List<SecondaryAccount> secondaryAccounts;

  private String clientDocument;

  private LocalDate debitCardDate;
}
