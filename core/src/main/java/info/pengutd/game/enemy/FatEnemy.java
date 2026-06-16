package info.pengutd.game.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.SpeedModifier;
import info.pengutd.game.World;
import info.pengutd.game.particle.DamageTextParticle;
import org.jetbrains.annotations.NotNull;

// Dicker Pinguin mit mehr leben
// todo um ihn rum ist eine Eis Aura die Türme verlangsamt
public class FatEnemy extends Enemy implements SpeedModifier {
    // in tiles
    public static final float HEIGHT = 0.9f;
    public static final float WIDTH = 0.9f;
    public static final float FROST_RADIUS = 1.5f;
    public static final float SPEED = 0.5f;
    public static final String JSON_TYPE = "fat_enemy";
    public static final float SLOW_MULTIPLIER = 0.75f;
    // in Sek
    public static final float FRAME_DURATION = (1f / SPEED) / 4f;
    public static final float POP_DURATION = 0.1f;
    private final @NotNull EnemyAnimatorSet animator;
    private final @NotNull ParticleEffect auraEffect;
    private final @NotNull ParticleEmitter auraEmitter;
    private float health = 5;

    public FatEnemy(@NotNull World world, int id) {
        super(world, new Vector2(), id);
        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.ENEMY_ATLAS);
        animator = new EnemyAnimatorSet(
            new EnemyAnimator("fat_up", 4, atlas, FRAME_DURATION),
            new EnemyAnimator("fat_down", 4, atlas, FRAME_DURATION),
            new EnemyAnimator("fat_side", 4, atlas, FRAME_DURATION)
        );
        auraEffect = new ParticleEffect();
        auraEffect.load(Gdx.files.internal("game/particle/snowflake.p"), PenguTD.getInstance().getAssetManager().get(Assets.PARTICLE_ATLAS, TextureAtlas.class));
        auraEffect.start();
        auraEmitter = auraEffect.getEmitters().first();
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    protected void setHealth(float value) {
        health = value;
    }

    @Override
    /// @return ob der Aura Effekt vom Pinguin an dieser Position Effekt hat
    public boolean affectsAt(@NotNull Vector2 pos) {
        if (!isAlive()) return false;
        float radius2 = FROST_RADIUS * getWorld().getTileWidth();
        radius2 *= radius2;
        return pos.dst2(getPos()) <= radius2;
    }

    @Override
    public float getMultiplier() {
        return SLOW_MULTIPLIER;
    }

    @Override
    public float getSpeed() {
        return SPEED * getWorld().getTileWidth();
    }

    @Override
    public void pop(float damage) {
        if (getPopTimeLeft() > 0 || damage <= 0) return; // kein Schaden nehmen wenn gerade gepoppt
        health -= damage;
        getWorld().addParticle(new DamageTextParticle(getPos().add(0, getHeight() / 2f), getWorld(), damage));
        setPopTimeLeft(POP_DURATION);
        if (health <= 0) die();
        // stats erhöhen
    }

    @Override
    public void die() {
        super.die();
        health = 0;
        // stats erhöhen
        getWorld().addMoney(10);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (health <= 0) return;
        animator.update(delta);

        float angle = MathUtils.random(0, MathUtils.PI2);
        float distance = (float) (Math.sqrt(MathUtils.random()) * FROST_RADIUS * getWorld().getTileWidth());

        float x = getX() + MathUtils.cos(angle) * distance;
        float y = getY() + MathUtils.sin(angle) * distance;

        auraEmitter.setPosition(x, y);
        auraEffect.update(delta);
    }

    @Override
    public void draw(@NotNull SpriteBatch batch) {
        super.draw(batch);
        auraEffect.draw(batch);
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        return animator.getTexture(getDirection());
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
    protected boolean flipX() {
        return getDirection() == Direction.LEFT;
    }

    @Override
    public void dispose() {
        auraEffect.dispose();
    }
}
