package com.datalcott.ecommerce.repository;

import com.datalcott.ecommerce.entity.Review;
import com.datalcott.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProduct(Product product);

}
