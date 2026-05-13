package info.pengutd.game.tower.projectile;

import com.badlogic.gdx.graphics.Texture;
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

/// Standard Projektil
public class FishProjectile extends Projectile {
    public static final String JSON_TYPE = "fish_projectile";
    // speed in tiles pro sekunde
    private static final float SPEED = 5f;
    private final @NotNull TextureRegion texture;

    public FishProjectile(@NotNull World world, @NotNull Vector2 pos, @NotNull Vector2 direction, @NotNull Tower tower, int damage) {
        super(world, pos, direction, tower, damage);
        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.PROJECTILE_ATLAS, TextureAtlas.class);
        texture = Assets.findRegionOrMissing(atlas, "fish");

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
        return 0.5f * getWorld().getTileHeight();
    }

    @Override
    public float getWidth() {
        return 0.25f * getWorld().getTileWidth();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void onHit(@NotNull Enemy enemy) {
        super.onHit(enemy);
        enemy.pop(getDamage());
        destroy();
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = super.toJson();
        value.addChild("type", new JsonValue(JSON_TYPE));
        return value;
    }

    @Override
    public void dispose() {
        // nichts texture ist in TextreManager
    }
}
