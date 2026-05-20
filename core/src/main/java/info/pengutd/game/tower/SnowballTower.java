package info.pengutd.game.tower;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.tower.projectile.Projectile;
import info.pengutd.game.tower.projectile.SnowballProjectile;
import org.jetbrains.annotations.NotNull;

/// Turm mit AoE Damage
public class SnowballTower extends Tower {
    public static final String JSON_TYPE = "snowball_tower";
    ///  in tiles
    private static final float WIDTH = .75f;
    private static final float HEIGHT = .6f;
    private static final float RANGE = 2f;
    private static final float EXPLOSION_RADIUS = 1f;
    private static final int DAMAGE = 1;
    // in shots/second
    private static final float ATTACK_SPEED = .5f;
    private final @NotNull TowerAnimator animator;

    public SnowballTower(@NotNull World world, @NotNull Vector2 pos, int id) {
        super(world, pos, id);
        animator = new TowerAnimator(JSON_TYPE, PenguTD.getInstance().getAssetManager().get(Assets.TOWER_ATLAS));
    }

    @Override
    public int getCost() {
        return 75;
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
        return new SnowballProjectile(getWorld(), getHandPos(), target.getPos().sub(getHandPos()).nor(), this, getDamage(), EXPLOSION_RADIUS);
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        return animator.getTexture(getShotCooldown(), getTimeSinceLastAttack(), 1/getAttackSpeed(), getTargetEnemy() != null);
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
        // nichts, texturen sind im AssetManager
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = super.toJson();
        value.addChild("type", new JsonValue(JSON_TYPE));
        return value;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
    }
}
