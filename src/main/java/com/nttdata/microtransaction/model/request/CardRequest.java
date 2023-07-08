package com.nttdata.microtransaction.model.request;

import java.util.List;

import com.nttdata.microtransaction.model.Card;
import com.nttdata.microtransaction.model.ClientProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;


/**
 * CardRequest Class.
 */
@AllArgsConstructor
@Data
@Builder
public class CardRequest {
  @Id
  private String id;
  @DocumentReference(lazy = true)
  private ClientProduct clientProduct;
  private String codeProduct;
  private String accountNumber;
  private String cardNumber;
  private List<ClientProduct> associatedAccounts;
  private int state;

  /**
   * toCard mthod.
   *
   * @return ,
   */
  public Card toCard() {
    return Card.builder()
               .clientProduct(this.clientProduct)
               .accountNumber(this.accountNumber)
               .cardNumber(this.cardNumber)
               .codeProduct(this.codeProduct)
               .state(this.state)
               .build();
  }
}
