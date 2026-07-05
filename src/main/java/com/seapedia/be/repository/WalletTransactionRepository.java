package com.seapedia.be.repository;

import com.seapedia.be.model.BuyerWallet;
import com.seapedia.be.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletOrderByCreatedAtDesc(BuyerWallet wallet);
}