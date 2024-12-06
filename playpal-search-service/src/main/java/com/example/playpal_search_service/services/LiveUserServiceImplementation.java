package com.example.playpal_search_service.services;

import com.example.playpal_search_service.model.LiveUser;
import com.example.playpal_search_service.repository.LiveUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveUserServiceImplementation implements LiveUserService {

    private final LiveUserRepository liveUserRepository;

    @Override
    public LiveUser addLiveUser(Long userId, List<String> tags, String videoGame) {

        List<LiveUser> existingLiveUsers = liveUserRepository.findByUserIdAndIsLiveTrue(userId);

        existingLiveUsers.forEach(liveUser -> {
            liveUser.setLive(false);
            liveUserRepository.save(liveUser);
        });

        LiveUser liveUser = new LiveUser();
        liveUser.setUserId(userId);
        liveUser.setLive(true);
        liveUser.setVideoGame(videoGame);
        liveUser.setLiveStartTime(LocalDateTime.now());

        if (liveUser.getTags() == null) {
            liveUser.setTags(new ArrayList<>());
        }

        if (tags != null) {
            liveUser.getTags().addAll(tags);
        }

        liveUser.setVideoGame(videoGame);
        liveUser.setLiveStartTime(LocalDateTime.now());
        return liveUserRepository.save(liveUser);
    }

    @Override
    public void removeLiveUser(Long userId) {
        liveUserRepository.deleteById(userId);
    }

    @Override
    public List<LiveUser> getLiveUsers() {
        return liveUserRepository.findAll();
    }

    // is this needed? Some type of code to wipe out all live users that forgot to turn off life search.
    /*
    *  public void cleanExpiredLiveUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);
        List<LiveUser> expiredUsers = liveUserRepository.findAllByLiveStartTimeAfter(cutoff);
        liveUserRepository.deleteAll(expiredUsers);
    }
    * */

    @Override
    public List<LiveUser> disableLiveUser(Long userId) {
        List<LiveUser> liveUsers = liveUserRepository.findByUserId(userId);
        for (LiveUser liveUser : liveUsers) {
            liveUser.setLive(false);
        }
        return liveUserRepository.saveAll(liveUsers);
    }

    //todo: Implement websockets, and broadcasting with these?

}