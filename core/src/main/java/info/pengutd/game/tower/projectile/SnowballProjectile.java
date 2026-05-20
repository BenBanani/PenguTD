package info.pengutd.game.tower.projectile;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.tower.Tower;
import org.jetbrains.annotations.NotNull;

public class SnowballProjectile extends Projectile {
    public static final String JSON_TYPE = "snowball_projectile";
    private static final float SPEED = 5f;
    private final @NotNull TextureRegion texture;
    private final float explosionRadius;

    public SnowballProjectile(@NotNull World world, @NotNull Vector2 pos, @NotNull Vector2 direction, @NotNull Tower tower, int damage, float explosionRadius) {
        super(world, pos, direction, tower, damage);
        this.explosionRadius = explosionRadius;
        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.PROJECTILE_ATLAS, TextureAtlas.class);
        texture = Assets.findRegionOrMissing(atlas, JSON_TYPE);

        float deg = MathUtils.radiansToDegrees * MathUtils.atan2(direction.y, direction.x) - 90;
        setRotationDeg(deg);
    }

    @Override
    public float getSpeed() {
        return SPEED * getWorld().getTileWidth();
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        return texture;
    }

    @Override
    public float getHeight() {
        return 0.15f * getWorld().getTileHeight();
    }

    @Override
    public float getWidth() {
        return 0.15f * getWorld().getTileWidth();
    }

    @Override
    public void onHit(@NotNull Enemy enemy) {
        super.onHit(enemy);
        enemy.pop(getDamage());
        popAoE(enemy);
        destroy();
    }

    /// poppt Gegner in der nähe
    private void popAoE(@NotNull Enemy enemy) {
        getWorld().getEnemies().forEach((e) -> {
            if (e != enemy && e.getPos().dst2(enemy.getPos()) < explosionRadius * getWorld().getTileWidth() * explosionRadius * getWorld().getTileWidth()) {
                e.pop(getDamage());
            }
        });
    }

    @Override
    public void dispose() {

    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = super.toJson();
        value.addChild("type", new JsonValue(JSON_TYPE));
        return value;
    }
}
