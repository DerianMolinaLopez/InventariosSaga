package com.inventarios.inventarios.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventarios.inventarios.models.ItemEntity;
@Repository
public interface ItemEntityRepository extends JpaRepository<ItemEntity,String> {
     boolean existsBySku(String sku);

   
    
}
