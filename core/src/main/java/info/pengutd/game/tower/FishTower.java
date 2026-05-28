package info.pengutd.game.tower;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.tower.projectile.Projectile;
import info.pengutd.game.tower.projectile.FishProjectile;
import org.jetbrains.annotations.NotNull;

/// Der erste einfachste Turm
public class FishTower extends Tower {
    public static final String JSON_TYPE = "fish_tower";
    ///  breite in tiles
    private static final float WIDTH = .75f;
    ///  höhe in tiles
    private static final float HEIGHT = .6f;
    /// range in tiles
    private static final float RANGE = 2f;
    /// damage
    private static final int DAMAGE = 1;
    /// attack speed in seconds
    private static final float ATTACK_SPEED = 1f;

    public FishTower(@NotNull World world, @NotNull Vector2 pos, int id) {
        super(world, pos, id);
    }

    @Override
    public int getCost() {
        return 50;
    }

    @Override
    public float getRange() {
        return (RANGE * getWorld().getTileWidth());
    }

    @Override
    public int getDamage() {
        return DAMAGE;
    }

    @Override
    public float getAttackSpeed() {
        return ATTACK_SPEED;
    }

    @Override
    protected @NotNull Projectile createProjectile() {
        Enemy target = getTargetEnemy();
        if (target == null) {
            throw new IllegalStateException("shoot auf leeres enemy");
        }
        return new FishProjectile(getWorld(), getHandPos(), target.getPos().sub(getHandPos()).nor(), this, getDamage());
    }

    @Override
    protected float getHandOffset() {
        return 20f;
    }

    @Override
    public @NotNull String getType() {
        return JSON_TYPE;
    }

    @Override
    public float getHeight() {
        return HEIGHT * getWorld().getTileHeight();
    }

    @Override
    public float getWidth() {
        return WIDTH * getWorld().getTileWidth();
    }

    @Override
    public void dispose() {
        // nichts da Texturen im AssetManager verwaltet werden
    }

    /// Türme müssen nach den Gegnern geladen werden
    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
    }
}
