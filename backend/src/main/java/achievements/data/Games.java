package achievements.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Games {

    public static class Game {

        @JsonProperty("ID")
        private int id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("platforms")
        private List<String> platforms;

        public Game(int id, String name, String platform) {
            this.id   = id;
            this.name = name;
            this.platforms = new ArrayList<>();
            this.platforms.add(platform);
        }

        // Start Getters/Setters
        public int getId() { return id; }

        public void setId(int id) { this.id = id; }

        public String getName() { return name; }

        public void setName(String name) { this.name = name; }

        public List<String> getPlatforms() { return platforms; }

        public void setPlatforms(List<String> platforms) { this.platforms = platforms; }

        public void addToPlatforms(String platform) { this.platforms.add(platform); }
        // End Getters/Setters

    }

    @JsonProperty("games")
    private List<Game> games;

    public Games() { games = new ArrayList<Game>(); }

    // Start Getters/Setters
    public List<Game> getGames() { return games; }

    public void setGames(List<Game> games) { this.games = games; }
    // End Getters/Setters

    public void addGame(Game game) { this.games.add(game); }

}
