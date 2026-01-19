package com.enterprise.gamma.repository;

import com.enterprise.gamma.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(String productId);

    List<Inventory> findByWarehouse(String warehouse);

    boolean existsByProductId(String productId);
}
