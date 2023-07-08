package com.nttdata.microtransaction.repository;

import com.nttdata.microtransaction.model.Product;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.RxJava3SortingRepository;
import org.springframework.stereotype.Repository;

/**
 * ProductRepository interface.
 */
@Repository
public interface ProductRepository extends RxJava3SortingRepository<Product, String> {

  @Query(value = "db.product.find()")
  Flowable<Product> listProducts();

  Maybe<Product> getByName(String name);

  Maybe<Product> getByCodeProduct(String codProduct);
}
