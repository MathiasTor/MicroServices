package com.example.playpal_search_service.controllers;


import com.example.playpal_search_service.dtos.SearchPostDTO;
import com.example.playpal_search_service.services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/validate/{userId}")
    public boolean validateUser(@PathVariable Long userId) {
        // Mock logic: Return true for even IDs, false for odd IDs
        //return userId % 2 == 1;
        return true;
    }



    @GetMapping("/posts")
    public List<SearchPostDTO> getAllPosts() {
        return searchService.getAllPosts();
    }

    @GetMapping("/posts/pageable")
    public Page<SearchPostDTO> getAllPostsPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return searchService.getAllPostsPageable(pageable);
    }

    @GetMapping("/posts/keyword")
    public List<SearchPostDTO> getPostsByKeyword(@RequestParam(required = false) String keyword) {
        return searchService.getPostsByCriteria(keyword != null ? keyword : "");
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