package com.example.playpal_search_service.services;

import com.example.playpal_search_service.dtos.SearchPostDTO;

import java.util.List;

public interface SearchService {
    SearchPostDTO createPost(SearchPostDTO postDTO);
    List<SearchPostDTO> getAllPosts();
    List<SearchPostDTO> getPostsByLiveStatus(boolean live);
    SearchPostDTO updatePost(Long id, SearchPostDTO postDTO);
    void deletePost(Long id);
}