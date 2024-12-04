package com.example.playpal_search_service.services;


import com.example.playpal_search_service.dtos.SearchPostDTO;
import com.example.playpal_search_service.model.SearchPost;
import com.example.playpal_search_service.repository.SearchRepository;
import com.example.playpal_search_service.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImplementation implements SearchService {

    private final SearchRepository repository;

    @Override
    public SearchPostDTO createPost(SearchPostDTO postDTO) {
        SearchPost post = mapToEntity(postDTO);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return mapToDTO(repository.save(post));
    }

    @Override
    public List<SearchPostDTO> getAllPosts() {
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SearchPostDTO> getPostsByLiveStatus(boolean live) {
        return repository.findByLive(live).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public SearchPostDTO updatePost(Long id, SearchPostDTO postDTO) {
        return repository.findById(id).map(post -> {
            post.setTitle(postDTO.getTitle());
            post.setDescription(postDTO.getDescription());
            post.setTags(postDTO.getTags());
            post.setLive(postDTO.isLive());
            post.setUpdatedAt(LocalDateTime.now());
            return mapToDTO(repository.save(post));
        }).orElseThrow(() -> new RuntimeException("Post not found with ID: " + id));
    }

    @Override
    public void deletePost(Long id) {
        repository.deleteById(id);
    }

    // Utility methods for mapping
    private SearchPost mapToEntity(SearchPostDTO dto) {
        SearchPost post = new SearchPost();
        post.setId(dto.getId());
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setTags(dto.getTags());
        post.setLive(dto.isLive());
        return post;
    }

    private SearchPostDTO mapToDTO(SearchPost post) {
        SearchPostDTO dto = new SearchPostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setTags(post.getTags());
        dto.setLive(post.isLive());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }
}
