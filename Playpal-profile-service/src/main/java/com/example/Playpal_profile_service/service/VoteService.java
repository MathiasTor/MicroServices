package com.example.Playpal_profile_service.service;

import com.example.Playpal_profile_service.model.PlaypalProfile;
import com.example.Playpal_profile_service.model.Vote;
import com.example.Playpal_profile_service.repository.ProfileRepository;
import com.example.Playpal_profile_service.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private VoteRepository voteRepository;

    // Cast or update a vote
    public PlaypalProfile castVote(Long profileId, Long voterId, Vote.VoteType voteType) {
        PlaypalProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Optional<Vote> existingVote = voteRepository.findByProfileIdAndVoterId(profileId, voterId);

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            if (vote.getVoteType() == voteType) {
                // If the same vote type is submitted, do nothing
                return profile;
            }

            // Update the profile's vote counts
            if (vote.getVoteType() == Vote.VoteType.UPVOTE) {
                profile.setUpvotes(profile.getUpvotes() - 1);
            } else {
                profile.setDownvotes(profile.getDownvotes() - 1);
            }

            vote.setVoteType(voteType);
            voteRepository.save(vote);
        } else {
            // New vote
            Vote vote = new Vote();
            vote.setProfileId(profileId);
            vote.setVoterId(voterId);
            vote.setVoteType(voteType);
            voteRepository.save(vote);
        }

        // Update the profile's vote counts
        if (voteType == Vote.VoteType.UPVOTE) {
            profile.setUpvotes(profile.getUpvotes() + 1);
        } else {
            profile.setDownvotes(profile.getDownvotes() + 1);
        }

        return profileRepository.save(profile);
    }
}
