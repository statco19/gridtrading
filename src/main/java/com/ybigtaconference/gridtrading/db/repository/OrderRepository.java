package com.ybigtaconference.gridtrading.db.repository;

import com.ybigtaconference.gridtrading.db.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByUuid(String uuid);
//    Boolean findByUuidExists(String uuid);
}
