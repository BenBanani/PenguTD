package info.pengutd.stats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import info.pengutd.save.JsonSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static info.pengutd.stats.StatsManager.formatDuration;

public class GlobalStats implements JsonSerializable {
    int enemiesKilled;
    int gamesPlayed;
    float playTime;
    int highestWave;
    int moneyEarned;

    /// Lädt die Globalstats aus den assets
    public GlobalStats() {
        loadStats();
    }

    /// lädt die stats vom globalen stats.json file.
    /// Wenn die datei nicht existiert, wird sie erstellt.
    void loadStats() {
        FileHandle handle = Gdx.files.local("saves/stats.json");

        if (!handle.exists()) {
            saveStats();
            return;
        }

        try {
            fromJson(new JsonReader().parse(handle));
        } catch (RuntimeException e) {
            Gdx.app.error(GlobalStats.class.getName(), "Failed to load stats.json\nresetting stats to 0");
            saveStats();
        }
    }

    void saveStats() {
        FileHandle handle = Gdx.files.local("saves/stats.json");
        handle.writeString(toJson().prettyPrint(JsonWriter.OutputType.json, 1), false);
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        value.addChild("enemiesKilled", new JsonValue(enemiesKilled));
        value.addChild("gamesPlayed", new JsonValue(gamesPlayed));
        value.addChild("playTime", new JsonValue(playTime));
        value.addChild("highestWave", new JsonValue(highestWave));
        value.addChild("moneyEarned", new JsonValue(moneyEarned));
        return value;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        enemiesKilled = json.getInt("enemiesKilled");
        gamesPlayed = json.getInt("gamesPlayed");
        playTime = json.getFloat("playTime");
        highestWave = json.getInt("highestWave");
        moneyEarned = json.getInt("moneyEarned");
    }

    public Map<String, String> getStatsAsPrintMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Kills", "" + enemiesKilled);
        map.put("Games Played", "" + gamesPlayed);
        map.put("Time played", formatDuration((long) playTime));
        map.put("Highest wave", "" + highestWave);
        map.put("Money earned", moneyEarned + "$");
        return map;
    }
}
