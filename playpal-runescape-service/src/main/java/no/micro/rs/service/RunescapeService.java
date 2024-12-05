package no.micro.rs.service;

import lombok.extern.slf4j.Slf4j;
import no.micro.rs.model.RunescapeChar;
import no.micro.rs.repository.RunescapeRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class RunescapeService {
    @Autowired
    private RunescapeRepository runescapeRepository;

    //Logger slf4j
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RunescapeService.class);

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
            existingRunescapeChar.setUserId(runescapeChar.getUserId());
            return runescapeRepository.save(existingRunescapeChar);
        }
        return null;
    }

    //Link user to runescape character
    public RunescapeChar linkUserToRSChar(Long userid, String runescapeName) {
        if(runescapeRepository.findByRunescapeName(runescapeName) != null) {
            RunescapeChar runescapeChar = runescapeRepository.findByRunescapeName(runescapeName);
            runescapeChar.setUserId(userid);
            return runescapeRepository.save(runescapeChar);
        }
        return null;
    }
    //Fetch runescape stats from highscores
    public void fetchRunescapeStats(String runescapeName) {
        try {
            RunescapeChar runescapeChar = runescapeRepository.findByRunescapeName(runescapeName);

            if(runescapeChar == null) {
                return;
            }

                String url = "https://secure.runescape.com/m=hiscore_oldschool/hiscorepersonal?user1=" + runescapeName;
                logger.info("Fetching RuneScape stats from: " + url);

                Document document = Jsoup.connect(url).get();

                Elements rows = document.select("#contentHiscores table tr");
                for (int i = 1; i < rows.size(); i++) {
                    Elements columns = rows.get(i).select("td");

                    if (columns.size() >= 5) {
                        String skill = columns.get(1).text().toLowerCase(); // Skill name
                        String rank = columns.get(2).text();
                        String level = columns.get(3).text();
                        String xp = columns.get(4).text();

                        switch (skill){
                            case "overall":
                                level = level.replace(",", "");
                                runescapeChar.setTotal(Integer.parseInt(level));
                                break;
                            case "attack":
                                runescapeChar.setAttack(Integer.parseInt(level));
                                break;
                            case "defence":
                                runescapeChar.setDefence(Integer.parseInt(level));
                                break;
                            case "strength":
                                runescapeChar.setStrength(Integer.parseInt(level));
                                break;
                            case "hitpoints":
                                runescapeChar.setHitpoints(Integer.parseInt(level));
                                break;
                            case "ranged":
                                runescapeChar.setRanged(Integer.parseInt(level));
                                break;
                            case "prayer":
                                runescapeChar.setPrayer(Integer.parseInt(level));
                                break;
                            case "magic":
                                runescapeChar.setMagic(Integer.parseInt(level));
                                break;
                            case "cooking":
                                runescapeChar.setCooking(Integer.parseInt(level));
                                break;
                            case "woodcutting":
                                runescapeChar.setWoodcutting(Integer.parseInt(level));
                                break;
                            case "fletching":
                                runescapeChar.setFletching(Integer.parseInt(level));
                                break;
                            case "fishing":
                                runescapeChar.setFishing(Integer.parseInt(level));
                                break;
                            case "firemaking":
                                runescapeChar.setFiremaking(Integer.parseInt(level));
                                break;
                            case "crafting":
                                runescapeChar.setCrafting(Integer.parseInt(level));
                                break;
                            case "smithing":
                                runescapeChar.setSmithing(Integer.parseInt(level));
                                break;
                            case "mining":
                                runescapeChar.setMining(Integer.parseInt(level));
                                break;
                            case "herblore":
                                runescapeChar.setHerblore(Integer.parseInt(level));
                                break;
                            case "agility":
                                runescapeChar.setAgility(Integer.parseInt(level));
                                break;
                            case "thieving":
                                runescapeChar.setThieving(Integer.parseInt(level));
                                break;
                            case "slayer":
                                runescapeChar.setSlayer(Integer.parseInt(level));
                                break;
                            case "farming":
                                runescapeChar.setFarming(Integer.parseInt(level));
                                break;
                            case "runecraft":
                                runescapeChar.setRunecrafting(Integer.parseInt(level));
                                break;
                            case "hunter":
                                runescapeChar.setHunter(Integer.parseInt(level));
                                break;
                            case "construction":
                                runescapeChar.setConstruction(Integer.parseInt(level));
                                break;
                        }
                    }
                }
            runescapeRepository.save(runescapeChar);
            getRaidsKC(runescapeName);
        } catch (Exception e) {
            logger.error("Error fetching RuneScape stats: " + e.getMessage());
        }
    }

    public void createRunescapeChar(Long userid, String runescapeName) {

        if(runescapeRepository.findByUserId(userid) != null) {
            logger.error("User already linked to a RuneScape character.");
            return;
        }

        RunescapeChar runescapeChar = new RunescapeChar();
        runescapeChar.setRunescapeName(runescapeName);
        runescapeChar.setUserId(userid);
        runescapeRepository.save(runescapeChar);
        fetchRunescapeStats(runescapeName);
    }

    public void getRaidsKC(String runescapeName) {
        try {
            RunescapeChar runescapeChar = runescapeRepository.findByRunescapeName(runescapeName);

            if (runescapeChar == null) {
                System.err.println("Character not found in the database.");
                return;
            }

            if(runescapeChar.getRunescapeName() != runescapeName) {
                System.err.println("Mismatch usernames - aborting.");
                return;
            }

            String url = "https://secure.runescape.com/m=hiscore_oldschool/hiscorepersonal?user1=" + URLEncoder.encode(runescapeName, StandardCharsets.UTF_8);
            logger.info("Fetching RuneScape stats from: " + url);

            Document document = Jsoup.connect(url).get();

            // Locate the table containing boss/minigame data
            Elements rows = document.select("#contentHiscores table tr");

            for (Element row : rows) {
                Elements columns = row.select("td");


                if (columns.size() >= 4) {
                    String bossName = columns.get(1).text().toLowerCase();
                    String kcText = columns.get(3).text();

                    int killCount;
                    try {
                        killCount = Integer.parseInt(kcText.replace(",", "").trim());
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    switch (bossName) {
                        case "chambers of xeric":
                            runescapeChar.setCoxKC(killCount);
                            break;
                        case "theatre of blood":
                            runescapeChar.setTobKC(killCount);
                            break;
                        case "tombs of amascut":
                            runescapeChar.setToaKC(killCount);
                            break;
                        default:
                            // Ignore other bosses
                            break;
                    }
                }
            }

            // Save the updated character stats
            runescapeRepository.save(runescapeChar);
        } catch (Exception e) {
            System.err.println("Error fetching RuneScape stats: " + e.getMessage());
        }
    }


    public RunescapeChar getStatsForUser(Long userid) {
        RunescapeChar runescapeChar = runescapeRepository.findByUserId(userid);
        if(runescapeChar != null) {
            System.out.println("Found character for user: " + runescapeChar.getRunescapeName());
            return runescapeChar;
        } else {
            return null;
        }
    }
}
