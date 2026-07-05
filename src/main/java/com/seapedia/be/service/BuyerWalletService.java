package com.seapedia.be.service;

import com.seapedia.be.dto.TopUpRequest;
import com.seapedia.be.dto.WalletResponse;
import com.seapedia.be.dto.WalletTransactionResponse;
import com.seapedia.be.enums.WalletTransactionType;
import com.seapedia.be.model.BuyerWallet;
import com.seapedia.be.model.User;
import com.seapedia.be.model.WalletTransaction;
import com.seapedia.be.repository.BuyerWalletRepository;
import com.seapedia.be.repository.UserRepository;
import com.seapedia.be.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BuyerWalletService {

    private final BuyerWalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public BuyerWalletService(BuyerWalletRepository walletRepository,
                              WalletTransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public WalletResponse topUp(String username, TopUpRequest request) {
        User buyer = getUser(username);
        BuyerWallet wallet = walletRepository.findByBuyer(buyer)
                .orElseGet(() -> walletRepository.save(BuyerWallet.builder().buyer(buyer).build()));

        wallet.setBalance(wallet.getBalance().add(request.amount()));
        walletRepository.save(wallet);

        transactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .type(WalletTransactionType.TOP_UP)
                .amount(request.amount())
                .description("Dummy buyer wallet top-up")
                .build());

        return getWallet(username);
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(String username) {
        User buyer = getUser(username);
        BuyerWallet wallet = walletRepository.findByBuyer(buyer)
                .orElseGet(() -> BuyerWallet.builder().buyer(buyer).balance(BigDecimal.ZERO).build());

        List<WalletTransaction> transactions = wallet.getId() != null ?
                transactionRepository.findByWalletOrderByCreatedAtDesc(wallet) : List.of();

        List<WalletTransactionResponse> transactionResponses = transactions.stream()
                .map(t -> new WalletTransactionResponse(
                        t.getType().name(),
                        t.getAmount(),
                        t.getDescription(),
                        t.getCreatedAt()))
                .toList();

        return new WalletResponse(wallet.getBalance(), transactionResponses);
    }
}
