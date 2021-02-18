package achievements.data.response.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Game {

    @JsonProperty("ID")
    private int ID;
    @JsonProperty("name")
    private String name;
    @JsonProperty("achievement_count")
    private int achievement_count;
    @JsonProperty("avg_completion")
    private Integer avg_completion;
    @JsonProperty("num_owners")
    private int num_owners;
    @JsonProperty("num_perfects")
    private int num_perfects;

    public int getID() { return ID; }

    public void setID(int ID) { this.ID = ID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getAchievement_count() {
        return achievement_count;
    }

    public void setAchievement_count(int achievement_count) {
        this.achievement_count = achievement_count;
    }

    public Integer getAvg_completion() {
        return avg_completion;
    }

    public void setAvg_completion(Integer avg_completion) {
        this.avg_completion = avg_completion;
    }

    public int getNum_owners() {
        return num_owners;
    }

    public void setNum_owners(int num_owners) {
        this.num_owners = num_owners;
    }

    public int getNum_perfects() {
        return num_perfects;
    }

    public void setNum_perfects(int num_perfects) {
        this.num_perfects = num_perfects;
    }
}
