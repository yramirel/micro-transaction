package com.nttdata.microtransaction.repository;

import com.nttdata.microtransaction.model.ProductRules;
import io.reactivex.rxjava3.core.Flowable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * ProductRulesRepository interface.
 */
@Repository
public interface ProductRulesRepository extends ReactiveMongoRepository<ProductRules, String> {
  Flowable<ProductRules> getByCodeProduct(String codeProduct);

  Flowable<ProductRules> getByCodeProductAndTypeClient(String codeProduct, Integer typeClient);
}
