package info.pengutd.game.tower;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// Der erste einfachste Turm
public class NormalTower extends Tower {
    ///  breite in tiles
    private static final float WIDTH = 0.5f;
    ///  höhe in tiles
    private static final float HEIGHT = 0.5f;
    /// range in tiles
    private static final float RANGE = 2f;
    /// damage
    private static final int DAMAGE = 1;
    /// attack speed in seconds
    private static final float ATTACK_SPEED = 1f;
    private final @NotNull Texture texture;
    private final @NotNull Vector2 pos;
    private final @NotNull Rectangle hitbox;
    private final float timeUntilNext = 0f;
    private @Nullable Enemy targetEnemy = null;

    public NormalTower(@NotNull Vector2 position, @NotNull World world) {
        super(world);
        texture = new Texture("game/tower/tower1.png");
        pos = position.cpy();
        hitbox = new Rectangle(position.x, position.y, getWidth(), getHeight());
        hitbox.setCenter(pos);
    }

    @Override
    public int getCost() {
        return 200;
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
    public @Nullable Enemy getTargetEnemy() {
        return targetEnemy;
    }

    @Override
    public @NotNull Texture getTexture() {
        return texture;
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
    public float getX() {
        return pos.x;
    }

    @Override
    public float getY() {
        return pos.y;
    }

    @Override
    public void update(float delta) {
        if (targetEnemy == null || !targetEnemy.isAlive() || !inRange(targetEnemy)) {
            targetEnemy = findNewTarget();
        }
    }

    private @Nullable Enemy findNewTarget() {
        Enemy target = null;
        for (Enemy enemy : getWorld().getEnemies()) {
            if (inRange(enemy)) {
                if (target == null) {
                    target = enemy;
                } else if (enemy.getPos().dst(pos) < target.getPos().dst(pos)) {
                    target = enemy;
                }
            }
        }
        return target;
    }

    private boolean inRange(@Nullable Enemy targetEnemy) {
        if (targetEnemy == null) return false;
        return this.pos.dst(new Vector2(targetEnemy.getX(), targetEnemy.getY())) <= getRange();
    }

    @Override
    public @NotNull Shape2D getHitbox() {
        return hitbox;
    }

    @Override
    public void dispose() {
        this.texture.dispose();
    }

    @Override
    public @NotNull JsonValue toJson() {
        // todo
        return null;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        // todo
    }
}
