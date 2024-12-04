package com.example.playpal_search_service.repository;


import com.example.playpal_search_service.model.SearchPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchRepository extends JpaRepository<SearchPost, Long> {
    List<SearchPost> findByLive(boolean live);
    List<SearchPost> findByTagsContainingOrTitleContaining(String tags, String title);
    List<SearchPost> findAll();
    Page<SearchPost> findAll(Pageable pageable);
}
