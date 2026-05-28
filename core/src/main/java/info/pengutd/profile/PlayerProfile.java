package info.pengutd.profile;

import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.PenguTD;
import info.pengutd.save.JsonSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerProfile implements JsonSerializable {
    private @NotNull String name;
    /// welche Türme der Spieler bereits freigeschaltet hat
    private final boolean[] towersUnlocked = new boolean[PenguTD.towerNames.size()];

    PlayerProfile(@NotNull String name) {
        this.name = name;
    }

    public void unlockTower(int towerId) {
        towersUnlocked[towerId] = true;
    }

    public boolean isTowerUnlocked(int towerId) {
        return towersUnlocked[towerId];
    }

    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        value.addChild("name", new JsonValue(name));
        JsonValue towers = new JsonValue(JsonValue.ValueType.array);
        for (boolean tUnlocked : towersUnlocked) {
            towers.addChild(new JsonValue(tUnlocked));
        }
        value.addChild("towers", towers);
        return value;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        name = json.getString("name");
        JsonValue towers = json.get("towers");
        boolean[] asBooleanArray = towers.asBooleanArray();
        for (int i = 0; i < asBooleanArray.length; i++) {
            boolean tUnlocked = asBooleanArray[i];
            towersUnlocked[i] = tUnlocked;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerProfile)) return false;
        return ((PlayerProfile) obj).name.equals(name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
