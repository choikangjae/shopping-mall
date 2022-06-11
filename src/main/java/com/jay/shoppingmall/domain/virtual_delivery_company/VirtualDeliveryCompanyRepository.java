package com.jay.shoppingmall.domain.virtual_delivery_company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VirtualDeliveryCompanyRepository extends JpaRepository<VirtualDeliveryCompany, Long> {

    List<VirtualDeliveryCompany> findByTrackingNumber(String trackingNumber);

    Optional<VirtualDeliveryCompany> findFirstByTrackingNumber(String trackingNumber);

    Optional<List<VirtualDeliveryCompany>> findByUserId(Long userId);

    Optional<VirtualDeliveryCompany> findByOrderItemId(Long orderItemId);
}
