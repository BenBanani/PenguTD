package info.pengutd.game.tower.projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.GameObject;
import info.pengutd.game.SpeedModifier;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.tower.Tower;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FireProjectile extends Projectile implements SpeedModifier {
    public static final String JSON_TYPE = "fire_projectile";
    private static final float SPEED = 5f;
    private static final float MAX_BURN_DURATION = 3;
    private static final float BURN_DAMAGE = 0.1f;
    private final @NotNull TextureRegion texture;
    private @Nullable ParticleEffect fireEffect;
    private @Nullable Enemy target;  // gegner an dem das projectile klebt oder null
    private float burnDuration;
    private float timeSinceLastBurn;

    public FireProjectile(@NotNull World world, @NotNull Vector2 pos, @NotNull Vector2 direction, @NotNull Tower tower, float damage) {
        super(world, pos, direction, tower, damage);
        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.PROJECTILE_ATLAS);
        texture = Assets.findRegionOrMissing(atlas, JSON_TYPE);

        float deg = MathUtils.degreesToRadians * MathUtils.atan2(direction.y, direction.x) - 180;
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
        return 0.25f * getWorld().getTileHeight();
    }

    @Override
    public float getWidth() {
        return 0.25f * getWorld().getTileWidth();
    }

    @Override
    public void onHit(@NotNull Enemy enemy) {
        super.onHit(enemy);

        target = enemy;
        enemy.pop(getDamage());
        PenguTD.getInstance().getStatsManager().addDamage(getDamage());
        setPos(target.getPos().sub(0, target.getHeight() / 4f));

        fireEffect = new ParticleEffect();
        fireEffect.load(Gdx.files.internal("game/particle/fire2.p"), PenguTD.getInstance().getAssetManager().get(Assets.PARTICLE_ATLAS, TextureAtlas.class));

        fireEffect.setPosition(target.getX(), target.getY() - target.getHeight() / 4f);
        fireEffect.start();
    }

    @Override
    public void update(float delta) {
        if (!isAlive()) return;
        if (target == null) {
            super.update(delta);
        } else {
            assert fireEffect != null;
            fireEffect.setPosition(target.getX(), target.getY() - target.getHeight() / 4f);
            fireEffect.update(delta);

            if (!target.isAlive()) {
                destroy();
                return;
            }
            setPos(target.getPos().sub(0, target.getHeight() / 4f));
            burnDuration += delta;
            timeSinceLastBurn += delta;

            while (timeSinceLastBurn >= 0.25f) {
                target.pop(BURN_DAMAGE);
                timeSinceLastBurn -= 0.25f;
            }

            if (burnDuration >= MAX_BURN_DURATION) {
                destroy();
            }
        }
    }

    @Override
    public void draw(@NotNull SpriteBatch batch) {
        if (target != null) {
            assert fireEffect != null;
            fireEffect.draw(batch);
        } else {
            super.draw(batch);
        }
    }

    @Override
    public void dispose() {
        if (fireEffect != null) {
            fireEffect.dispose();
        }
    }

    @Override
    public boolean affects(@NotNull GameObject obj) {
        if (!(obj instanceof Enemy)) return false;
        return target != null && target == obj;
    }

    @Override
    public float getMultiplier() {
        return 0.75f;
    }
}
