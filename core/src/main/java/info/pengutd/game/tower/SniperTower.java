package info.pengutd.game.tower;

import com.badlogic.gdx.math.Vector2;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.tower.projectile.IceProjectile;
import info.pengutd.game.tower.projectile.Projectile;
import org.jetbrains.annotations.NotNull;

/// Sniper Tower hat eine sehr große Range mit viel Schaden, aber langsamen Attack Speed.
/// Er kann außerdem getarnte Bush Gegner erkennen
public class SniperTower extends Tower {
    public static final String JSON_TYPE = "sniper_tower";
    private static final float WIDTH = 0.75f;
    private static final float HEIGHT = .6f;
    private static final float RANGE = 20f;
    private static final int DAMAGE = 3;
    private static final float ATTACK_SPEED = 0.2f;

    public SniperTower(@NotNull World world, @NotNull Vector2 pos, int id) {
        super(world, pos, id);
    }

    @Override
    public int getCost() {
        return 100;
    }

    @Override
    public float getRange() {
        return RANGE * getWorld().getTileWidth();
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
    protected float getHandOffset() {
        return 20;
    }

    @Override
    protected @NotNull Projectile createProjectile() {
        Enemy target = getTargetEnemy();
        if (target == null) {
            throw new IllegalStateException("shoot auf leeres enemy");
        }
        return new IceProjectile(getWorld(), getHandPos(), target.getPos().sub(getHandPos()).nor(), this, getDamage());
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

    }
}
