package info.pengutd.game.tower;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import info.pengutd.Assets;
import org.jetbrains.annotations.NotNull;

/// TowerAnimator kümmert sich um die Animation von Towers.
/// jeder Tower sollte 4 Texturen haben:
///     idle: normale Textur
///     attack1: vor dem Schießen
///     attack2: beim Schießen
///     attack3: nach dem Schießen
public class TowerAnimator {
    private final @NotNull TextureRegion idle;
    private final @NotNull TextureRegion attack1;
    private final @NotNull TextureRegion attack2;
    private final @NotNull TextureRegion attack3;

    public TowerAnimator(@NotNull String name, @NotNull TextureAtlas atlas) {
        idle = Assets.findRegionOrMissing(atlas, name + "_idle");
        attack1 = Assets.findRegionOrMissing(atlas, name + "_attack1");
        attack2 = Assets.findRegionOrMissing(atlas, name + "_attack2");
        attack3 = Assets.findRegionOrMissing(atlas, name + "_attack3");
    }

    /// @return die aktuelle Textur des Towers
    /// @param timeToNextAttack die Zeit bis zum nächsten Schuss
    /// @param timeSinceLastAttack die Zeit seit dem letzten Schuss
    /// @param isAttacking ob der Tower einen Gegner anvisiert hat.
    /// gerade geschossen → attack3
    /// schießt gerade → attack2
    /// schießt gleich → attack1
    public @NotNull TextureRegion getTexture(float timeToNextAttack, float timeSinceLastAttack, boolean isAttacking) {
        if (!isAttacking) return idle;
        if (timeToNextAttack < 0.05 || timeSinceLastAttack < 0.1) return attack2;
        if (timeSinceLastAttack < 0.2) return attack3;
        return attack1;
    }
}
