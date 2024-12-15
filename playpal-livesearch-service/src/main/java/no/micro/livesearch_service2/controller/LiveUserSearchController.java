package no.micro.livesearch_service2.controller;

import no.micro.livesearch_service2.model.LiveUserSearch;
import no.micro.livesearch_service2.service.LiveUserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/live")
public class LiveUserSearchController {

    @Autowired
    private LiveUserSearchService liveUserSearchService;

    @GetMapping("/all")
    public List<LiveUserSearch> getLiveUserSearches() {
        return liveUserSearchService.getLiveUserSearches();
    }

    @PostMapping("/new/{userId}")
    public LiveUserSearch newLiveUserSearch(@PathVariable Long userId) {
        return liveUserSearchService.goLive(userId);
    }

    @GetMapping("/match/{userId}")
    public LiveUserSearch findMatch(@PathVariable Long userId) {
        return liveUserSearchService.findMatch(userId);
    }

    @PutMapping("/stop/{userId}")
    public void stopLive(@PathVariable Long userId) {
        liveUserSearchService.stopLive(userId);
    }

    @GetMapping("/status/{userId}")
    public boolean getLiveStatus(@PathVariable Long userId) {
        return liveUserSearchService.isUserLive(userId);
    }

    //Check if an entry contains a match
    @GetMapping("/match-status/{id}")
    public boolean getMatchStatus(@PathVariable Long id) {
        return liveUserSearchService.isMatched(id);
    }
}
