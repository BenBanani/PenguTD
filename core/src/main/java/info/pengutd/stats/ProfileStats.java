package info.pengutd.stats;

import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.profile.PlayerProfile;
import info.pengutd.save.JsonSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static info.pengutd.stats.StatsManager.formatDuration;

public class ProfileStats implements JsonSerializable {
    private final @NotNull PlayerProfile profile;
    int enemiesKilled;
    int gamesPlayed;
    float playTime;
    int highestWave;
    int moneyEarned;
    int wins;

    public ProfileStats(@NotNull PlayerProfile profile) {
        this.profile = profile;
    }

    public @NotNull PlayerProfile getProfile() {
        return profile;
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    public void setEnemiesKilled(int enemiesKilled) {
        this.enemiesKilled = enemiesKilled;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public float getPlayTime() {
        return playTime;
    }

    public void setPlayTime(float playTime) {
        this.playTime = playTime;
    }

    public int getHighestWave() {
        return highestWave;
    }

    public void setHighestWave(int highestWave) {
        this.highestWave = highestWave;
    }

    public int getMoneyEarned() {
        return moneyEarned;
    }

    public void setMoneyEarned(int moneyEarned) {
        this.moneyEarned = moneyEarned;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        value.addChild("enemiesKilled", new JsonValue(enemiesKilled));
        value.addChild("gamesPlayed", new JsonValue(gamesPlayed));
        value.addChild("playTime", new JsonValue(playTime));
        value.addChild("highestWave", new JsonValue(highestWave));
        value.addChild("moneyEarned", new JsonValue(moneyEarned));
        value.addChild("wins", new JsonValue(wins));
        return value;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        enemiesKilled = json.getInt("enemiesKilled");
        gamesPlayed = json.getInt("gamesPlayed");
        playTime = json.getFloat("playTime");
        highestWave = json.getInt("highestWave");
        moneyEarned = json.getInt("moneyEarned");
        wins = json.getInt("wins");
    }

    public Map<String, String> getStatsAsPrintMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Kills", "" + enemiesKilled);
        map.put("Games Played", "" + gamesPlayed);
        map.put("Time played", formatDuration((long) playTime));
        map.put("Highest wave", "" + highestWave);
        map.put("Money earned", moneyEarned + "$");
        map.put("Wins", "" + wins);
        return map;
    }
}
