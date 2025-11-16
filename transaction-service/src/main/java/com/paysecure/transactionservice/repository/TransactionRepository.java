package com.paysecure.transactionservice.repository;

import com.paysecure.transactionservice.model.Transaction;
import com.paysecure.transactionservice.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionId(String transactionId);
    
    List<Transaction> findByUserId(Long userId);
    
    List<Transaction> findByMerchantId(Long merchantId);
    
    List<Transaction> findByStatus(TransactionStatus status);
    
    List<Transaction> findByUserIdAndStatus(Long userId, TransactionStatus status);
    
    List<Transaction> findByMerchantIdAndStatus(Long merchantId, TransactionStatus status);
    
    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByDateRange(@Param("startDate") Instant startDate, 
                                      @Param("endDate") Instant endDate);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.status = :status")
    Long countByStatus(@Param("status") TransactionStatus status);
}
