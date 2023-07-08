package com.nttdata.microtransaction.model.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.nttdata.microtransaction.model.Client;
import com.nttdata.microtransaction.model.ClientProduct;
import com.nttdata.microtransaction.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


/**
 * ClientProductRequest Class.
 */
@AllArgsConstructor
@Data
@Builder
public class ClientProductRequest {
  private String id;
  private Product product;
  private String codeProduct;
  private Client client;
  private String documentNumber;
  private String accountNumber;
  private LocalDateTime date;
  private Integer cutoffDay;
  private BigDecimal creditLimit;
  private BigDecimal balance;
  private BigDecimal consumption;
  private int state;

  /**
   * toClientProduct method.
   *
   * @return ,
   */
  public ClientProduct toClientProduct() {
    return ClientProduct.builder()
               .id(this.id)
               .product(this.product)
               .codeProduct(this.codeProduct)
               .client(this.client)
               .documentNumber(this.documentNumber)
               .accountNumber(this.accountNumber)
               .date(this.date)
               .creditLimit(this.creditLimit)
               .balance(this.balance)
               .consumption(this.consumption)
               .cutoffDay(this.cutoffDay)
               .state(this.state)
               .build();
  }
}
