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
    private final @NotNull TextureRegion idle;  // Textur, wenn kein Gegner anvisiert ist
    private final @NotNull TextureRegion attackIdle; // Textur, wenn Gegner anvisiert ist, aber gerade nicht geschossen wird
    private final @NotNull TextureRegion attackShoot; // Textur, während Geschossen wird (z.B. ohne Projektil in der Hand)
    private final @NotNull TextureRegion attackShotOver; // Textur, nachdem geschossen wurde (z.B. Hand unten zum nachladen)

    public TowerAnimator(@NotNull String name, @NotNull TextureAtlas atlas) {
        idle = Assets.findRegionOrMissing(atlas, name + "_idle");
        attackIdle = Assets.findRegionOrMissing(atlas, name + "_attack1");
        attackShoot = Assets.findRegionOrMissing(atlas, name + "_attack2");
        attackShotOver = Assets.findRegionOrMissing(atlas, name + "_attack3");
    }

    /// @return die aktuelle Textur des Towers
    /// @param timeSinceLastAttack die Zeit seit dem letzten Schuss
    /// @param totalCooldown Cooldown Zeit die der Tower hat (normalerweise 1 / getAttackSpeed())
    /// @param isAttacking ob der Tower einen Gegner anvisiert hat.
    public @NotNull TextureRegion getTexture(float timeSinceLastAttack, float totalCooldown, boolean isAttacking) {
        if (!isAttacking) return idle;
        if (timeSinceLastAttack < 0.2 * totalCooldown) return attackShoot;
        if (timeSinceLastAttack < 0.5 * totalCooldown) return attackShotOver;
        return attackIdle;
    }
}
