package com.example.playpal_search_service.repository;


import com.example.playpal_search_service.model.SearchPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchRepository extends JpaRepository<SearchPost, Long> {
    List<SearchPost> findByLive(boolean live);
}
