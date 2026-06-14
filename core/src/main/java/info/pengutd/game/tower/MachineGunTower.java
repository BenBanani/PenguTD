package info.pengutd.game.tower;

import com.badlogic.gdx.math.Vector2;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.tower.projectile.MachineGunProjectile;
import info.pengutd.game.tower.projectile.Projectile;
import org.jetbrains.annotations.NotNull;

public class MachineGunTower extends Tower {
    public static final String JSON_TYPE = "mg_tower";
    /// in tiles
    private static final float WIDTH = 0.8f;
    private static final float HEIGHT = 0.85f;
    private static final float RANGE = 1.5f;
    private static final float DAMAGE = 0.2f;
    private static final float ATTACK_SPEED = 10;

    public MachineGunTower(@NotNull World world, @NotNull Vector2 pos, int id) {
        super(world, pos, id);
    }

    @Override
    public int getCost() {
        return 200;
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
        return 0.22f * getWorld().getTileWidth();
    }

    @Override
    protected Vector2 getHandPos() {
        Vector2 pos = super.getHandPos();

        Vector2 offset = new Vector2(0f, 0.4f * getWorld().getTileWidth()); // nach vorne an die mündung der mg
        offset.rotateDeg(getRotationDeg());

        return pos.add(offset);
    }

    @Override
    protected @NotNull Projectile createProjectile() {
        Enemy target = getTargetEnemy();
        assert target != null;
        return new MachineGunProjectile(getWorld(), getHandPos(), target.getPos().sub(getHandPos()).nor(), this, DAMAGE);
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
