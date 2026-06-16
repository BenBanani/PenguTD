package info.pengutd.game;

import org.jetbrains.annotations.NotNull;

/// Interface für Entities, die Speed Effekte auf die Umgebung haben
public interface SpeedModifier {
    /// @return ob der SpeedModifier an dieser Position Effekt hat
    boolean affects(@NotNull GameObject object);
    float getMultiplier();
}
