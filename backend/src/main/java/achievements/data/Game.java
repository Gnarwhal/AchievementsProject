package achievements.data;

import achievements.data.query.StringFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public static class Query {
        @JsonProperty("name")
        private StringFilter name;
        @JsonProperty("platforms")
        private StringFilter platforms;
    }

    @JsonProperty("ID")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("platforms")
    private List<String> platforms;
    @JsonProperty("achievementCount")
    private int achievementCount;

    public Game(int id, String name, String platform) {
        this.id   = id;
        this.name = name;
        this.platforms = new ArrayList<>();
        this.platforms.add(platform);
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<String> getPlatforms() { return platforms; }

    public void setPlatforms(List<String> platforms) { this.platforms = platforms; }

    public void addToPlatforms(String platform) { this.platforms.add(platform); }
}
