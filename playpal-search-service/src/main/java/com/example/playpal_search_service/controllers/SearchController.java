package com.example.playpal_search_service.controllers;


import com.example.playpal_search_service.dtos.SearchPostDTO;
import com.example.playpal_search_service.services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/posts")
    public SearchPostDTO createPost(@RequestBody SearchPostDTO postDTO) {
        return searchService.createPost(postDTO);
    }

    @GetMapping("/posts")
    public List<SearchPostDTO> getAllPosts() {
        return searchService.getAllPosts();
    }

    @GetMapping("/posts/live")
    public List<SearchPostDTO> getPostsByLiveStatus(@RequestParam boolean live) {
        return searchService.getPostsByLiveStatus(live);
    }

    @PutMapping("/posts/{id}")
    public SearchPostDTO updatePost(@PathVariable Long id, @RequestBody SearchPostDTO postDTO) {
        return searchService.updatePost(id, postDTO);
    }

    @DeleteMapping("/posts/{id}")
    public void deletePost(@PathVariable Long id) {
        searchService.deletePost(id);
    }
}