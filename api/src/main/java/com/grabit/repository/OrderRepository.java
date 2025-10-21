package com.grabit.repository;

import com.grabit.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    Page<Order> findAllByRestaurantIdAndBranchId(Long restaurantId, Long branchid, PageRequest pageRequest);

    Page<Order> findAllByMemberId(Long memberId,PageRequest pageRequest);
}
