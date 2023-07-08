package com.nttdata.microtransaction.model;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * ProductRules Class.
 */
@Document(value = "product_rules")
@AllArgsConstructor
@Data
@Builder
public class ProductRules implements Serializable {
  @Id
  private String id;
  private int typeClient;
  private String codeProduct;
  private Integer maxAccount;
  private BigDecimal costMaintenance;
  private Integer maxDeposits;
  private Integer maxWithdrawal;
  private int state;
}
