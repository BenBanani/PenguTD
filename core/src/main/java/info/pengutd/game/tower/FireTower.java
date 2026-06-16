package info.pengutd.game.tower;

import com.badlogic.gdx.math.Vector2;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.tower.projectile.FireProjectile;
import info.pengutd.game.tower.projectile.Projectile;
import org.jetbrains.annotations.NotNull;

/// Turm der Feuerkugeln schießt die Gegner in Brand setzen
public class FireTower extends Tower {
    public static final String JSON_TYPE = "fire_tower";
    private static final float WIDTH = .75f;
    private static final float HEIGHT = .6f;
    private static final float RANGE = 2.5f;
    private static final float DAMAGE = .5f;
    private static final float ATTACK_SPEED = 0.7f;

    public FireTower(@NotNull World world, @NotNull Vector2 pos, int id) {
        super(world, pos, id);
    }

    @Override
    public int getCost() {
        return 150;
    }

    @Override
    public float getRange() {
        return RANGE * getWorld().getTileWidth();
    }

    @Override
    public float getDamage() {
        return DAMAGE;
    }

    @Override
    public float getAttackSpeed() {
        return ATTACK_SPEED;
    }

    @Override
    protected float getHandOffset() {
        return 0;
    }

    @Override
    protected @NotNull Projectile createProjectile() {
        Enemy target = getTargetEnemy();
        if (target == null) {
            throw new IllegalStateException("shoot auf leeres enemy");
        }
        return new FireProjectile(getWorld(), getHandPos(), getTargetEnemy().getPos().sub(getHandPos()), this, DAMAGE);
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
