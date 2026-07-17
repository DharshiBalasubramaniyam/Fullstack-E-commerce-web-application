package com.dharshi.inventory_service.repositories;

import com.dharshi.inventory_service.models.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {

    List<Inventory> findByProductId(String productId);
    List<Inventory> findByProductIdAndSku(String productId, String sku);


}
