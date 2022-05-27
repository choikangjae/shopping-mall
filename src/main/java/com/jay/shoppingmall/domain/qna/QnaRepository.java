package com.jay.shoppingmall.domain.qna;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {

//    List<Qna> findAllOrderByIdDesc(Pageable pageable);
    List<Qna> findAllByItemId(Long id);
    Page<Qna> findAllByItemId(Long id, Pageable pageable);
}
