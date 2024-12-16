package no.micro.livesearch_service2.controller;

import no.micro.livesearch_service2.model.LiveUserSearch;
import no.micro.livesearch_service2.service.LiveUserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/live")
public class LiveUserSearchController {

    @Autowired
    private LiveUserSearchService liveUserSearchService;

    @GetMapping("/all")
    public ResponseEntity<List<LiveUserSearch>> getLiveUserSearches() {
        List<LiveUserSearch> searches = liveUserSearchService.getLiveUserSearches();
        return ResponseEntity.ok(searches);
    }

    @PostMapping("/new/{userId}")
    public ResponseEntity<LiveUserSearch> newLiveUserSearch(@PathVariable Long userId) {
        LiveUserSearch search = liveUserSearchService.goLive(userId);
        if (search != null) {
            return ResponseEntity.ok(search);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/stop/{userId}")
    public ResponseEntity<Void> stopLive(@PathVariable Long userId) {
        liveUserSearchService.stopLive(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<Boolean> getLiveStatus(@PathVariable Long userId) {
        boolean status = liveUserSearchService.isUserLive(userId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/unread-match/{userId}")
    public ResponseEntity<String> getUnreadMatch(@PathVariable Long userId) {
        String matchMessage = liveUserSearchService.getUnreadMatch(userId);
        if (matchMessage.equals("No unread matches found.")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(matchMessage);
        } else {
            return ResponseEntity.ok(matchMessage);
        }
    }
}
