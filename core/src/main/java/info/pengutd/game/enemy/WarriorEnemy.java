package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import info.pengutd.game.particle.DamageTextParticle;
import org.jetbrains.annotations.NotNull;

/// Standard Gegner mit mehreren Stufen.
/// wenn ein Gegner getroffen wird, wird er zu einem Gegner mit geringerer Stufe
public class WarriorEnemy extends Enemy {
    ///  in tiles
    public static final float HEIGHT = 0.75f;
    public static final float WIDTH = 0.65f;
    public static final String JSON_TYPE = "warrior_enemy";
    public static final float SPEED_TO_ANIMATION_TIME = 20f;
    private static final float SPEED = 0.75f;
    private static final float POP_DURATION = 0.1f;

    private final @NotNull TextureRegion popTexture;
    private final @NotNull Array<EnemyAnimatorSet> animators = new Array<>(4);

    private float health;  // echte hp
    private int stage; // visuelle stufe (1 - 4)

    public WarriorEnemy(int level, @NotNull World world, int id) {
        super(world, new Vector2(), id); // placeholder position

        health = level;

        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.ENEMY_ATLAS);

        createAnimators(atlas);

        popTexture = Assets.findRegionOrMissing(atlas, "pop");
        stage = calcStage(level);
    }

    /// berechnet die stage aus hp (clamp von 1 bis animators.size - 1)
    private int calcStage(float hp) {
        return (int) Math.max(1, Math.min(animators.size, Math.ceil(hp)));
    }

    private void createAnimators(@NotNull TextureAtlas atlas) {
        for (int i = 1; i <= 4; i++) { // bis jetzt nur 4 levels
            animators.add(new EnemyAnimatorSet(new EnemyAnimator("warrior_" + i + "_up", 4, atlas, (1f / (SPEED * getWorld().getTileWidth() * i)) * SPEED_TO_ANIMATION_TIME), new EnemyAnimator("warrior_" + i + "_down", 4, atlas, (1f / (SPEED * getWorld().getTileWidth() * i)) * SPEED_TO_ANIMATION_TIME), new EnemyAnimator("warrior_" + i + "_side", 4, atlas, (1f / (SPEED * getWorld().getTileWidth() * i)) * SPEED_TO_ANIMATION_TIME)));
        }
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        if (health <= 0) return popTexture;
        return animators.get(stage - 1).getTexture(getDirection());
    }

    @Override
    public @NotNull String getType() {
        return JSON_TYPE;
    }

    @Override
    protected boolean flipX() {
        return getDirection() == Direction.LEFT;
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    protected void setHealth(float value) {
        health = value;
        stage = calcStage(health);
    }

    @Override
    public float getSpeed() {
        return SPEED * stage * getWorld().getTileWidth();
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
    public void pop(float damage) {
        if (getPopTimeLeft() > 0 || damage <= 0) return; // kein Schaden nehmen wenn gerade gepoppt

        int oldStage = stage;
        health -= damage;
        int newStage = calcStage(health);

        getWorld().addParticle(new DamageTextParticle(getPos().add(0, getHeight() / 2f), getWorld(), damage));

        setPopTimeLeft(POP_DURATION);
        if (health <= 0) {
            die();
            return;
        }

        if (oldStage != newStage) {
            stage = newStage;
        }
        // stats erhöhen
        getWorld().addMoney(1);
    }

    @Override
    public void die() {
        super.die();
        health = 0;
        stage = calcStage(health);
        getWorld().addMoney(5);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (health <= 0) return;

        animators.get(stage - 1).update(delta);
    }

    /// Lädt einen Gegner aus json ein.
    /// world muss bereits gesetzt sein, level ist egal
    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
        stage = calcStage(health);
    }

    @Override
    public void dispose() {
        // nichts, da Texturen im AssetManager verwaltet werden
    }
}
