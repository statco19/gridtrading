package com.ybigtaconference.gridtrading.db.repository;

import com.ybigtaconference.gridtrading.db.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
