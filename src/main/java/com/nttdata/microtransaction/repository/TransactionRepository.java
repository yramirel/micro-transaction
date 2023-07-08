package com.nttdata.microtransaction.repository;

import com.nttdata.microtransaction.model.Transactions;
import io.reactivex.rxjava3.core.Flowable;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.RxJava3SortingRepository;
import org.springframework.stereotype.Repository;


/**
 * TransactionRepository interface.
 */
@Repository
public interface TransactionRepository extends RxJava3SortingRepository<Transactions, String> {

  Flowable<Transactions> getByAccountNumber(String accountNumber);

  @Query(value = "{'accountNumber':?0,'typeTransaction': ?1 ,'date':{$gte: ?2,$lte: ?3}}")
  Flowable<Transactions> getByAccountNumberAndTypeTransactionAndDateBetween(String accountNumber,
                        String typeTransaction, LocalDateTime startDate, LocalDateTime endDate);

  @Query(value = "{'accountNumber':?0,'date':{$gte: ?1,$lte: ?2},'comission':{$gt:'0.0'}}")
  Flowable<Transactions> getByCommisionsByAccountNumberAndDate(String accountNumber,
                                                   LocalDateTime startDate, LocalDateTime endDate);

  @Query("{'date':{ $gte: ?0, $lte: ?1 }}")
  Flowable<Transactions> getByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
