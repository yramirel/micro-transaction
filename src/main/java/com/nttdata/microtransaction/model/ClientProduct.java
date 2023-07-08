package com.nttdata.microtransaction.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * ClientProduct Class.
 */
@Document(value = "client_product")
@AllArgsConstructor
@Data
@Builder
public class ClientProduct implements Serializable {
  @Id
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
}
