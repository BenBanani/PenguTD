package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import org.jetbrains.annotations.NotNull;

// Dicker Pinguin mit mehr leben
// todo um ihn rum ist eine Eis Aura die Türme verlangsamt
public class FatEnemy extends Enemy {
    // in tiles
    public static final float HEIGHT = 0.9f;
    public static final float WIDTH = 0.9f;
    public static final String JSON_TYPE = "fat_enemy";
    public static final float SPEED = 0.5f;
    public static final float FRAME_DURATION = (1f / SPEED) / 4f;
    public static final float POP_DURATION = 0.1f;
    private final @NotNull EnemyAnimatorSet animator;
    private int health = 5;

    public FatEnemy(@NotNull World world, int id) {
        super(world, new Vector2(), id);
        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.ENEMY_ATLAS);
        animator = new EnemyAnimatorSet(
            new EnemyAnimator("fat_up", 4, atlas, FRAME_DURATION),
            new EnemyAnimator("fat_down", 4, atlas, FRAME_DURATION),
        new EnemyAnimator("fat_side", 4, atlas, FRAME_DURATION)
        );
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public float getSpeed() {
        return SPEED * getWorld().getTileWidth();
    }

    @Override
    public void pop(int damage) {
        if (getPopTimeLeft() > 0) return; // kein Schaden nehmen wenn gerade gepoppt
        health -= damage;
        setPopTimeLeft(POP_DURATION);
        if (health <= 0) die();
        // stats erhöhen
    }

    @Override
    public void die() {
        health = 0;
        // stats erhöhen
        getWorld().addMoney(10);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (health <= 0) return;
        animator.update(delta);
    }

    @Override
    protected void setHealth(int value) {
        this.health = value;
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        return animator.getTexture(getDirection());
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

    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = super.toJson();
        value.addChild("type", new JsonValue(JSON_TYPE));
        value.addChild("health", new JsonValue(health));
        return value;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
        health = json.getInt("health");
    }
}
