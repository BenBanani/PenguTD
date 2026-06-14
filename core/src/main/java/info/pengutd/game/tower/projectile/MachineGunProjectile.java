package info.pengutd.game.tower.projectile;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.tower.Tower;
import org.jetbrains.annotations.NotNull;

public class MachineGunProjectile extends Projectile {
    public static final String JSON_TYPE = "mg_projectile";
    private static final float SPEED = 5f;
    private final @NotNull TextureRegion texture;

    public MachineGunProjectile(@NotNull World world, @NotNull Vector2 pos, @NotNull Vector2 direction, @NotNull Tower tower, float damage) {
        super(world, pos, direction, tower, damage);
        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.PROJECTILE_ATLAS);
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
    public @NotNull String getType() {
        return JSON_TYPE;
    }

    @Override
    public float getHeight() {
        return getWidth();
    }

    @Override
    public float getWidth() {
        return 0.05f * getWorld().getTileWidth();
    }

    @Override
    public void onHit(@NotNull Enemy enemy) {
        super.onHit(enemy);
        enemy.pop(getDamage());
        PenguTD.getInstance().getStatsManager().addDamage(getDamage());
        destroy();
    }

    @Override
    public void dispose() {

    }
}
