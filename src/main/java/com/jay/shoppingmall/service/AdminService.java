package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.dto.WriteItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final ItemRepository itemRepository;

    public void writeItem(WriteItemRequest writeItemRequest) {
        itemRepository.save(writeItemRequest.toEntity());
    }
}
