package com.nttdata.microtransaction.service.impl;
import com.nttdata.microtransaction.errorhandler.ConflictException;
import com.nttdata.microtransaction.model.BankDebs;
import com.nttdata.microtransaction.model.Client;
import com.nttdata.microtransaction.model.ClientProduct;
import com.nttdata.microtransaction.model.ProductRules;
import com.nttdata.microtransaction.model.Transactions;
import com.nttdata.microtransaction.model.request.TransactionsRequest;
import com.nttdata.microtransaction.repository.BankDebsRepository;
import com.nttdata.microtransaction.repository.ClientProductRepository;
import com.nttdata.microtransaction.repository.ClientRepository;
import com.nttdata.microtransaction.repository.ProductRulesRepository;
import com.nttdata.microtransaction.repository.TransactionRepository;
import com.nttdata.microtransaction.service.TransactionsService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TransactionServiceImpl class for all business logic of deposits, cashdrawal, payments.
 */
@Service
public class TransactionServiceImpl implements TransactionsService {
  @Autowired
  private TransactionRepository transactionRepository;
  @Autowired
  private ClientRepository clientRepository;
  @Autowired
  private ClientProductRepository clientProductRepository;
  @Autowired
  private ProductRulesRepository productRulesRepository;

  @Autowired
  private BankDebsRepository bankDebsRepository;

  @Override
  public Maybe<Transactions> saveTransaction(TransactionsRequest transactionsRequest) {
    return clientRepository.getByDocumentNumber(transactionsRequest.getDocumentNumber()).toSingle()
           .flatMap(client -> {
             ClientProduct clientProduct = clientProductRepository
                     .getByAccountNumber(transactionsRequest.getAccountNumber()).blockingGet();
             Boolean successRules = this.verifyRules(client, clientProduct, transactionsRequest);
             double comission = 0;
             if (!successRules) {
               comission = transactionsRequest.getAmount().floatValue() * 0.015;
             }
             return transactionRepository.save(this.businessLogic(clientProduct,
                 transactionsRequest, client, comission).blockingGet());
           }).toMaybe();
  }

  /**
   * businessLogic method.
   *
   * @param clientProduct ,
   * @param transactionsRequest ,
   * @param client ,
   * @param comission ,
   * @return ,
   */
  public Single<Transactions> businessLogic(ClientProduct clientProduct,
                                            TransactionsRequest transactionsRequest, Client client, double comission) {
    int uno = 1;
    BigDecimal balance = clientProduct.getBalance();
    BigDecimal amount = transactionsRequest.getAmount();
    BigDecimal consumption = clientProduct.getConsumption();
    if (transactionsRequest.getTypeTransaction().equals("deposito")) {
      amount = amount.subtract(BigDecimal.valueOf(comission));
      balance = balance.add(amount);
    } else if (transactionsRequest.getTypeTransaction().equals("retiro")
                   && balance.compareTo(amount.add(BigDecimal.valueOf(comission))) == uno) {
      balance = balance.subtract(amount.add(BigDecimal.valueOf(comission)));
    } else if (transactionsRequest.getTypeTransaction().equals("consumo")
                   && clientProduct.getCreditLimit().compareTo(amount.add(consumption)) == uno) {
      Integer cutoffDay = clientProduct.getCutoffDay();
      LocalDateTime startDate = null;
      LocalDateTime cutoffDate = null;
      if (cutoffDay >= LocalDateTime.now().getDayOfMonth()) {
        startDate = LocalDateTime.now().minusMonths(1).withDayOfMonth(cutoffDay + 1)
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
        cutoffDate = LocalDate.now().withDayOfMonth(cutoffDay - 1).atTime(LocalTime.MAX);
      } else {
        startDate = LocalDateTime.now().withDayOfMonth(cutoffDay + 1)
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
        cutoffDate = LocalDate.now().plusMonths(1).withDayOfMonth(cutoffDay - 1)
                         .atTime(LocalTime.MAX);
      }

      BankDebs bankDebs = bankDebsRepository.getByAccountNumberAndStartDateAndCutoffDate(
              transactionsRequest.getAccountNumber(),
              startDate, cutoffDate)
                              .switchIfEmpty(Flowable.just(BankDebs.builder().build()))
                              .blockingFirst();
      if (bankDebs.getId() == null) {
        bankDebs = BankDebs.builder().build();
        bankDebs.setAccountNumber(transactionsRequest.getAccountNumber());
        bankDebs.setDocumentNumber(client.getDocumentNumber());
        bankDebs.setStartDate(startDate);
        bankDebs.setCutoffDate(cutoffDate);
        bankDebs.setExpirationDate(cutoffDate.plusDays(20));
        bankDebs.setClientProduct(clientProduct);
        bankDebs.setClient(client);
        bankDebs.setAmount(BigDecimal.ZERO);
        bankDebs.setState(1);
      }
      bankDebs.setAmount(bankDebs.getAmount().add(amount));
      bankDebsRepository.save(bankDebs).subscribe();
      consumption = consumption.add(amount);
    } else if (transactionsRequest.getTypeTransaction().equals("pago")) {
      consumption = consumption.subtract(amount);
    } else if (transactionsRequest.getTypeTransaction().equals("transferencia")
                   && balance.compareTo(amount) == uno) {
      balance = balance.subtract(amount);
      ClientProduct clientProductReceiver = clientProductRepository.getByAccountNumber(
          transactionsRequest.getAccountNumberReceiver()).blockingGet();
      clientProductReceiver.setBalance(clientProductReceiver.getBalance().add(amount));
      clientProductRepository.save(clientProductReceiver).subscribe();
    } else {
      return Single.error(new ConflictException("Transaccion no soportada"));
    }

    clientProduct.setBalance(balance);
    clientProduct.setConsumption(consumption);
    clientProductRepository.save(clientProduct).subscribe();

    Transactions transactions = transactionsRequest.toTransactions();
    transactions.setClient(client);
    transactions.setComission(BigDecimal.valueOf(comission));
    transactions.setClientProduct(clientProduct);
    transactions.setDate(LocalDateTime.now());
    transactions.setState(1);
    return Single.just(transactions);
  }

  /**
   * verifyRules method, verify business rules.
   *
   * @param client              , is the client
   * @param clientProduct       , is the product assign to client
   * @param transactionsRequest , is the request to save
   * @return , true if success, false if not success
   */
  public Boolean verifyRules(Client client, ClientProduct clientProduct,
                             TransactionsRequest transactionsRequest) {
    Boolean successRules = true;
    ProductRules productRules = null;
    String typeTransaction = transactionsRequest.getTypeTransaction();
    Flowable<ProductRules> productRulesFlowable = productRulesRepository.getByCodeProduct(
            clientProduct.getCodeProduct())
            .filter(item -> item.getTypeClient() == client.getTypeClient());
    Long totalTransactions = transactionRepository
                     .getByAccountNumberAndTypeTransactionAndDateBetween(
                         transactionsRequest.getAccountNumber(),
                         typeTransaction,
                         LocalDateTime.now().withDayOfMonth(1).withHour(0)
                             .withMinute(0).withSecond(0).withNano(0),
                         LocalDate.now().plusMonths(1).withDayOfMonth(1)
                             .minusDays(1).atTime(LocalTime.MAX)).count().blockingGet();

    if (!productRulesFlowable.isEmpty().blockingGet()) {
      productRules = productRulesFlowable.blockingFirst();
    }
    if (productRules != null) {
      int maxTransaction = -1;
      if (typeTransaction.equals("deposito")) {
        maxTransaction = productRules.getMaxDeposits();
      } else if (typeTransaction.equals("retiro")) {
        maxTransaction = productRules.getMaxWithdrawal();
      }
      successRules = maxTransaction == -1 || (maxTransaction >= totalTransactions.intValue());
    }
    return successRules;
  }

  @Override
  public Flowable<Transactions> getTransactionsByAccountNumber(String accountNumber) {
    return transactionRepository.getByAccountNumber(accountNumber);
  }

  @Override
  public Flowable<Transactions> getByCommisionsByAccountNumberAndDate(String accountNumber) {
    return transactionRepository.getByCommisionsByAccountNumberAndDate(accountNumber,
        LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0),
        LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1).atTime(LocalTime.MAX));
  }

  @Override
  public Single<BigDecimal> getBalanceByClientProduct(String accountNumber) {
    return clientProductRepository.getByAccountNumber(accountNumber)
               .switchIfEmpty(Single.just(ClientProduct.builder().build()))
               .flatMap(item -> {
                 return Single.just(item.getBalance());
               });
  }
}
