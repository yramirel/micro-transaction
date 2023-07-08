package com.nttdata.microtransaction.repository;

import com.nttdata.microtransaction.model.ClientProduct;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import org.springframework.data.repository.reactive.RxJava3SortingRepository;
import org.springframework.stereotype.Repository;

/**
 * ClientProductRepository interface.
 */
@Repository
public interface ClientProductRepository extends RxJava3SortingRepository<ClientProduct, String> {
  Maybe<ClientProduct> getByAccountNumber(String accountNumber);

  Flowable<ClientProduct> getByDocumentNumberAndCodeProduct(String documentNumber,
                                                            String codeProduct);

  Flowable<ClientProduct> getByDocumentNumber(String documentNumber);
}
