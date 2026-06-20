package com.dharshi.inventory_service.repositories;

import com.dharshi.inventory_service.models.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {

    List<Inventory> findByProductId(String productId);
}
