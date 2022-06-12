package com.jay.shoppingmall.domain.seller.seller_bank_account_history;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerBankAccountHistoryRepository extends JpaRepository<SellerBankAccountHistory, Long> {
    List<SellerBankAccountHistory> findTop20BySellerIdOrderByCreatedDateDesc(Long sellerId);
}
