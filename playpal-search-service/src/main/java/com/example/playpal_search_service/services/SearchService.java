package com.example.playpal_search_service.services;

import com.example.playpal_search_service.dtos.SearchPostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {
    SearchPostDTO createPost(SearchPostDTO postDTO);
    List<SearchPostDTO> getAllPosts();
    List<SearchPostDTO> getPostsByLiveStatus(boolean live);
    List<SearchPostDTO> getPostsByCriteria(String keyword);
    SearchPostDTO updatePost(Long id, SearchPostDTO postDTO);

    void deletePost(Long id);

    // Add pageable method
    Page<SearchPostDTO> getAllPostsPageable(Pageable pageable);
}