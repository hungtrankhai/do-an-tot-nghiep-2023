package com.trankhaihung.cnpm.repository;

import com.trankhaihung.cnpm.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product getById(Long id);
}
