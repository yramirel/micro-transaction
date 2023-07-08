package com.nttdata.microtransaction.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Product Class.
 */
@Document(value = "product")
@AllArgsConstructor
@Data
@Builder
public class Product implements Serializable {
  @Id
  private String id;
  private String codeProduct;
  private String typeProduct; //pasivo,activo
  private String name;
  private int state;
}
