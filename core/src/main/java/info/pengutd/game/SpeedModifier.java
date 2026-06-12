package info.pengutd.game;

import com.badlogic.gdx.math.Vector2;

/// Interface für Entities, die Speed Effekte auf die Umgebung haben
public interface SpeedModifier {
    /// @return ob der SpeedModifier an dieser Position Effekt hat
    boolean affectsAt(Vector2 pos);
    float getMultiplier();
}
