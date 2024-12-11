package no.micro.rs.controller;

import lombok.extern.slf4j.Slf4j;
import no.micro.rs.model.RunescapeChar;
import no.micro.rs.service.RunescapeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/runescape")
public class RunescapeController {
    @Autowired
    private RunescapeService runescapeService;

    //Fetch stats
    @GetMapping("/fetch-stats/{runescapeName}")
    public void fetchRunescapeStats(@PathVariable String runescapeName) {
        runescapeService.fetchRunescapeStats(runescapeName);
    }

    //Link user to runescape character
    @PostMapping("/link-user/{userid}/{runescapeName}")
    public void linkUserToRSChar(@PathVariable Long userid, @PathVariable String runescapeName) {
        log.info("Linking user to runescape character " + runescapeName + " for user " + userid);
        runescapeService.linkUserToRSChar(userid, runescapeName);
    }

    //Create runescape character
    @PostMapping("/create/{userid}/{runescapeName}")
    public void createRunescapeChar(@PathVariable Long userid, @PathVariable String runescapeName) {
        runescapeService.createRunescapeChar(userid, runescapeName);
    }

    //Get stats for userid
    @GetMapping("/get-stats/{userid}")
    public RunescapeChar getStatsForUser(@PathVariable Long userid) {
        return runescapeService.getStatsForUser(userid);
    }

    //Check if user has linked runescape character
    @GetMapping("/is-linked/{userid}")
    public boolean isLinked(@PathVariable Long userid) {
        return runescapeService.isLinked(userid);
    }

    //get all runescape characters
    @GetMapping("/runescape-chars")
    public List<RunescapeChar> getAllRunescapeChars() {
        return runescapeService.getAllRunescapeChars();
    }
}
