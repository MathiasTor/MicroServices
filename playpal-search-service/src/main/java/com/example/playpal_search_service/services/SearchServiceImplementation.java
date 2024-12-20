package com.example.playpal_search_service.services;


import com.example.playpal_search_service.EventDriven.SearchEventPublisher;
import com.example.playpal_search_service.dtos.SearchEndedEvent;
import com.example.playpal_search_service.dtos.SearchPostDTO;
import com.example.playpal_search_service.model.SearchPost;
import com.example.playpal_search_service.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImplementation implements SearchService {

    private final SearchRepository repository;
    private final SearchEventPublisher searchEventPublisher;

    @Override
    public SearchPostDTO createPost(SearchPostDTO postDTO) {
        SearchPost post = mapToEntity(postDTO);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post = repository.save(post);

        log.info("Search post created by user: {}", post.getUserId());
        return mapToDTO(post);
    }



    @Override
    public List<SearchPostDTO> getAllPosts() {
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Page<SearchPostDTO> getAllPostsPageable(Pageable pageable) {
        return repository.findAll(pageable).map(this::mapToDTO);
    }

    public List<SearchPostDTO> getPostsByCriteria(String keyword) {
        List<SearchPost> posts = repository.findByTagsContainingOrTitleContaining(keyword, keyword);
        return posts.stream().map(this::mapToDTO).collect(Collectors.toList());
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
        post.setUserId(dto.getUserId());
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setTags(dto.getTags());
        post.setLive(dto.isLive());
        return post;
    }

    private SearchPostDTO mapToDTO(SearchPost post) {
        SearchPostDTO dto = new SearchPostDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setTags(post.getTags());
        dto.setLive(post.isLive());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        return dto;
    }

    @Override
    public void stopPost(Long id) {
        repository.findById(id).ifPresent(post -> {
            post.setLive(false);
            repository.save(post);
            // Create an event payload to send to RabbitMQ
            SearchEndedEvent event = new SearchEndedEvent(id, post.getUserId(), post.getTitle(), post.getDescription(), post.getApprovedUsers());
            searchEventPublisher.publishSearchEndedEvent(event);
        });
    }

    @Override
    public void applyToPost(Long postId, Long userId) {
        Optional<SearchPost> post = repository.findById(postId);

        if (post.isPresent()) {
            SearchPost searchPost = post.get();
            List<Long> appliedUsers = searchPost.getAppliedUsers();
            if (!appliedUsers.contains(userId)) {
                appliedUsers.add(userId);
                searchPost.setAppliedUsers(appliedUsers);
                repository.save(searchPost);
            }
        }
    }

    @Override
    public void approveUser(Long postId, Long userId) {
        Optional<SearchPost> post = repository.findById(postId);

        if (post.isPresent()) {
            SearchPost searchPost = post.get();
            List<Long> approvedUsers = searchPost.getApprovedUsers();

            searchPost.getAppliedUsers().remove(userId);

            if(approvedUsers.contains(userId)) {
            }else{
                approvedUsers.add(userId);
                searchPost.setApprovedUsers(approvedUsers);
            }

            repository.save(searchPost);

        }
    }

    @Override
    public void disapproveUser(Long postId, Long userId) {
        Optional<SearchPost> post = repository.findById(postId);

        if (post.isPresent()) {
            SearchPost searchPost = post.get();
            searchPost.getAppliedUsers().remove(userId);
            repository.save(searchPost);
        }
    }

    @Override
    public List<Long> getApplicants(Long postId) {
        Optional<SearchPost> post = repository.findById(postId);
        return post.map(SearchPost::getAppliedUsers).orElse(null);
    }

    @Override
    public List<Long> getApprovedUsers(Long postId) {
        Optional<SearchPost> post = repository.findById(postId);
        return post.map(SearchPost::getApprovedUsers).orElse(null);
    }

}
