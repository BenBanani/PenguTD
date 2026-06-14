package info.pengutd.stats;

import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.save.JsonSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static info.pengutd.stats.StatsManager.formatDuration;

public class GameStats implements JsonSerializable {
    int kills;
    float damageDealt;
    int towersPlaced;
    int wave;
    int money;
    float playTime;
    private boolean closed;

    public Map<String, String> getStatsAsPrintMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Kills", "" + kills);
        map.put("Damage dealt", damageDealt + " HP");
        map.put("Towers placed", "" + towersPlaced);
        map.put("Wave reached", "" + wave);
        map.put("Money made", money + "$");
        map.put("PlayTime", formatDuration((long) playTime));
        return map;
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        value.addChild("kills", new JsonValue(kills));
        value.addChild("damageDealt", new JsonValue(damageDealt));
        value.addChild("towersPlaced", new JsonValue(towersPlaced));
        value.addChild("wave", new JsonValue(wave));
        value.addChild("money", new JsonValue(money));
        value.addChild("playTime", new JsonValue(playTime));
        return value;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        kills = json.getInt("kills");
        damageDealt = json.getInt("damageDealt");
        towersPlaced = json.getInt("towersPlaced");
        wave = json.getInt("wave");
        money = json.getInt("money");
        playTime = json.getFloat("playTime");
    }

    /// GameStats werden geschlossen und können nicht mehr updated werden.
    /// Sollte genutzt werden, wenn das Spiel vorbei ist
    public void close() {
        assert !closed;
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
