package achievements.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Game {

    @JsonProperty("ID")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("platforms")
    private List<String> platforms;
    @JsonProperty("achievementCount")
    private int achievementCount;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<String> getPlatforms() { return platforms; }

    public void setPlatforms(List<String> platforms) { this.platforms = platforms; }

    public void addToPlatforms(String platform) { this.platforms.add(platform); }
}
