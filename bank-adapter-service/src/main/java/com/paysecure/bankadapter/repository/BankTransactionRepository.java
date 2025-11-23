package com.paysecure.bankadapter.repository;

import com.paysecure.bankadapter.model.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    BankTransaction findByGatewayTransactionId(String gatewayTransactionId);
}
