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

    @GetMapping("/all")
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

    //Set post inactive
    @PostMapping("/stop/{id}")
    public void stopPost(@PathVariable Long id) {
        searchService.stopPost(id);
    }

    //Apply to post
    @PostMapping("/apply/{postId}/{userId}")
    public void applyToPost(@PathVariable Long postId, @PathVariable Long userId) {
        searchService.applyToPost(postId, userId);
    }

    //Approve user
    @PostMapping("/approve/{postId}/{userId}")
    public void approveUser(@PathVariable Long postId, @PathVariable Long userId) {
        searchService.approveUser(postId, userId);
    }

    //Disapprove user
    @PostMapping("/disapprove/{postId}/{userId}")
    public void disapproveUser(@PathVariable Long postId, @PathVariable Long userId) {
        searchService.disapproveUser(postId, userId);
    }

    //Get applicants for post
    @GetMapping("/applicants/{postId}")
    public List<Long> getApplicants(@PathVariable Long postId) {
        return searchService.getApplicants(postId);
    }

    //Get approved users for post
    @GetMapping("/approved/{postId}")
    public List<Long> getApprovedUsers(@PathVariable Long postId) {
        return searchService.getApprovedUsers(postId);
    }
}