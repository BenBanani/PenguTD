package info.pengutd.game.tower.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.tower.Tower;
import org.jetbrains.annotations.NotNull;

public class SnowballProjectile extends Projectile {
    // speed in tiles pro sekunde
    private static final float SPEED = 7.5f;
    private final @NotNull TextureRegion texture;

    public SnowballProjectile(@NotNull World world, @NotNull Vector2 pos, @NotNull Vector2 direction, @NotNull Tower tower, int damage) {
        super(world, pos, direction, tower, damage);
        texture = new TextureRegion(PenguTD.getInstance().getAssetManager().get(Assets.SNOWBALL_PROJECTILE, Texture.class));
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
        return 0.25f * getWorld().getTileHeight();
    }

    @Override
    public float getWidth() {
        return 0.25f * getWorld().getTileWidth();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        setRotationDeg(getRotationDeg() + delta * 100);
    }

    @Override
    public void onHit(@NotNull Enemy enemy) {
        super.onHit(enemy);
        enemy.pop(getDamage());
        destroy();
    }

    @Override
    public void dispose() {
        // nichts texture ist in TextreManager
    }
}
