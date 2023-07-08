package com.nttdata.microtransaction.repository;

import com.nttdata.microtransaction.model.BankDebs;
import io.reactivex.rxjava3.core.Flowable;
import java.time.LocalDateTime;
import org.springframework.data.repository.reactive.RxJava3SortingRepository;

/**
 * BankDebsRepository interface.
 */
public interface BankDebsRepository extends RxJava3SortingRepository<BankDebs, String> {
  Flowable<BankDebs> getByAccountNumberAndStartDateAndCutoffDate(String accountNumber,
                                                                 LocalDateTime startDate, LocalDateTime cutoffDate);
}
