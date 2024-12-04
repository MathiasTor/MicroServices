package no.micro.rs.controller;

import no.micro.rs.service.RunescapeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
