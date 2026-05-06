package info.pengutd.game.tower;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.enemy.NormalEnemy;
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
    private float shotCooldown = 0f;
    private @Nullable Enemy targetEnemy = null;

    public NormalTower(@NotNull Vector2 position, @NotNull World world) {
        super(world);
        texture = PenguTD.getInstance().getAssetManager().get(Assets.TOWER1);
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
    public @NotNull TextureRegion getTexture() {
        return new TextureRegion(texture);
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

    public void setPos(Vector2 pos) {
        this.pos.set(pos);
    }

    @Override
    public void update(float delta) {
        if (targetEnemy == null || !targetEnemy.isAlive() || !inRange(targetEnemy)) {
            targetEnemy = findNewTarget();
        }
    }

    private @Nullable Enemy findNewTarget() {
        Enemy target = null;

        float closestDst2 = Float.MAX_VALUE;

        for (Enemy enemy : getWorld().getEnemies()) {

            float dst2 = pos.dst2(enemy.getPos());

            if (dst2 <= getRange() * getRange()
                && dst2 < closestDst2) {

                closestDst2 = dst2;
                target = enemy;
            }
        }

        return target;
    }

    private boolean inRange(@Nullable Enemy targetEnemy) {
        if (targetEnemy == null) return false;
        return this.pos.dst2(targetEnemy.getPos()) <= getRange() * getRange();
    }

    @Override
    public @NotNull Rectangle getHitbox() {
        return hitbox;
    }

    @Override
    public void dispose() {
        // nichts da Texturen im AssetManager verwaltet werden
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        value.addChild("type", new JsonValue("normal_tower"));
        value.addChild("x", new JsonValue(pos.x));
        value.addChild("y", new JsonValue(pos.y));
        value.addChild("shot_cooldown", new JsonValue(shotCooldown));
        if (targetEnemy != null) {
            value.addChild("target", new JsonValue(targetEnemy.getId()));
        }
        return value;
    }

    /// Türme müssen nach den Gegnern geladen werden
    @Override
    public void fromJson(@NotNull JsonValue json) {
        pos.set(json.getFloat("x"), json.getFloat("y"));
        shotCooldown = json.getFloat("shot_cooldown");
        if (json.has("target")) {
            targetEnemy = getWorld().getEnemyFromId(json.getInt("target"));
        }
    }
}
