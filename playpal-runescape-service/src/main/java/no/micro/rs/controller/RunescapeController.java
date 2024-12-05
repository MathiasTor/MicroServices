package no.micro.rs.controller;

import no.micro.rs.model.RunescapeChar;
import no.micro.rs.service.RunescapeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
