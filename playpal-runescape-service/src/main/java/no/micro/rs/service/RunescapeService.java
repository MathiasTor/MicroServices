package no.micro.rs.service;

import no.micro.rs.model.RunescapeChar;
import no.micro.rs.repository.RunescapeRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RunescapeService {
    @Autowired
    private RunescapeRepository runescapeRepository;

    //Get all runescape characters
    public List<RunescapeChar> getAllRunescapeChars() {
        return runescapeRepository.findAll();
    }

    //Get runescape character by id
    public RunescapeChar getRunescapeCharById(Long id) {
        return runescapeRepository.findById(id).orElse(null);
    }

    //Save runescape character
    public RunescapeChar saveRunescapeChar(RunescapeChar runescapeChar) {
        return runescapeRepository.save(runescapeChar);
    }

    //Delete runescape character
    public void deleteRunescapeChar(Long id) {
        runescapeRepository.deleteById(id);
    }

    //Update runescape character
    public RunescapeChar updateRunescapeChar(Long id, RunescapeChar runescapeChar) {
        RunescapeChar existingRunescapeChar = runescapeRepository.findById(id).orElse(null);
        if (existingRunescapeChar != null) {
            existingRunescapeChar.setRunescapeName(runescapeChar.getRunescapeName());
            existingRunescapeChar.setUser(runescapeChar.getUser());
            return runescapeRepository.save(existingRunescapeChar);
        }
        return null;
    }

    //Fetch runescape stats from highscores
    public void fetchRunescapeStats(String runescapeName) {
        try {
            String url = "https://secure.runescape.com/m=hiscore_oldschool/hiscorepersonal?user1=" + runescapeName;
            System.out.println("Fetching RuneScape stats from: " + url);

            Document document = Jsoup.connect(url).get();

            Elements rows = document.select("#contentHiscores table tr");
            for (int i = 1; i < rows.size(); i++) { // Skip header row
                Elements columns = rows.get(i).select("td");

                if (columns.size() >= 5) {
                    String skill = columns.get(1).text(); // Skill name
                    String rank = columns.get(2).text(); // Rank
                    String level = columns.get(3).text(); // Level
                    String xp = columns.get(4).text(); // XP

                    System.out.printf("Skill: %s, Rank: %s, Level: %s, XP: %s%n", skill, rank, level, xp);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching RuneScape stats: " + e.getMessage());
        }
    }
}
