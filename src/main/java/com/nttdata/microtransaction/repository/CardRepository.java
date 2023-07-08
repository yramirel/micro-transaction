package com.nttdata.microtransaction.repository;

import com.nttdata.microtransaction.model.Card;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.RxJava3SortingRepository;

/**
 * CardRepository interface.
 */
public interface CardRepository extends RxJava3SortingRepository<Card, String> {
  Maybe<Card> getByCardNumber(String cardNumber);

  @Query("{state:1}")
  Flowable<Card> getAllCard();
}
