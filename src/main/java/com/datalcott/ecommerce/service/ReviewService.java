package com.datalcott.ecommerce.service;

import com.datalcott.ecommerce.entity.Product;
import com.datalcott.ecommerce.entity.Review;
import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;


    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }


    public Review saveReview(Review review) {

        review.setCreatedDate(LocalDateTime.now());

        return reviewRepository.save(review);
    }


    public List<Review> getProductReviews(Product product) {

        return reviewRepository.findByProduct(product);
    }

}