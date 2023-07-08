package com.nttdata.microtransaction.model.request;

import javax.validation.constraints.NotBlank;
import com.nttdata.microtransaction.model.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * ClientRequest Class.
 */
@AllArgsConstructor
@Data
@Builder
public class ClientRequest {

  private String id;
  @NotBlank
  private String name;
  @NotBlank
  private String documentNumber;
  private int typeClient;
  private int signature;
  private int state;

  /**
   * toClient method.
   *
   * @return ,
   */
  public Client toClient() {
    return Client.builder()
               .id(this.id)
               .name(this.name)
               .documentNumber(this.documentNumber)
               .typeClient(this.typeClient)
               .signature(this.signature)
               .state(this.state)
               .build();
  }
}
