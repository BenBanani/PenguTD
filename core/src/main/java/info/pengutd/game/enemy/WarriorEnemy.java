package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import org.jetbrains.annotations.NotNull;

/// Standard Gegner mit mehreren Stufen.
/// wenn ein Gegner getroffen wird, wird er zu einem Gegner mit geringerer Stufe
public class WarriorEnemy extends Enemy {
    ///  Höhe in tiles
    public static final float HEIGHT = 0.75f;
    ///  breite in tiles
    public static final float WIDTH = 0.65f;
    public static final String JSON_TYPE = "warrior_enemy";
    public static final float SPEED_TO_ANIMATION_TIME = 20f;
    ///  speed in tiles
    private static final float SPEED = 0.75f;
    private static final float POP_DURATION = 0.1f;
    private final @NotNull TextureRegion popTexture;
    private final @NotNull Array<EnemyAnimatorSet> animators = new Array<>(4);
    private int level;

    public WarriorEnemy(int level, @NotNull World world, int id) {
        super(world, new Vector2(), id); // placeholder position
        this.level = level;
        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.WARRIOR_ENEMY_ATLAS);

        createAnimators(atlas);

        popTexture = Assets.findRegionOrMissing(atlas, "pop");
    }

    private void createAnimators(@NotNull TextureAtlas atlas) {
        for (int i = 0; i < 4; i++) { // bis jetzt nur 4 levels
            int enemyLevel = i + 1;
            animators.add(new EnemyAnimatorSet(new EnemyAnimator("warrior_" + enemyLevel + "_up", 4, atlas, (1f / getSpeed()) * SPEED_TO_ANIMATION_TIME), new EnemyAnimator("warrior_" + enemyLevel + "_down", 4, atlas, (1f / getSpeed()) * SPEED_TO_ANIMATION_TIME), new EnemyAnimator("warrior_" + enemyLevel + "_side", 4, atlas, (1f / getSpeed()) * SPEED_TO_ANIMATION_TIME)));
        }
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        if (level <= 0) return popTexture; // damit nicht indexOutOfBoundsException
        return getPopTimeLeft() > 0 ? popTexture : animators.get(level - 1).getTexture(getDirection());
    }

    @Override
    protected boolean flipX() {
        return getDirection() == Direction.LEFT;
    }

    @Override
    public int getHealth() {
        return level;
    }

    @Override
    public float getSpeed() {
        return SPEED * level * getWorld().getTileWidth();
    }

    @Override
    public float getHeight() {
        return HEIGHT * getWorld().getTileHeight();
    }

    @Override
    public float getWidth() {
        if (getPopTimeLeft() <= 0) {
            return WIDTH * getWorld().getTileWidth();
        }
        return getHeight(); // pop texture ist quadratisch
    }

    @Override
    public void pop(int damage) {
        if (getPopTimeLeft() > 0) return; // kein Schaden nehmen wenn gerade gepoppt
        level -= damage;
        setPopTimeLeft(POP_DURATION);
        if (level <= 0) die();
        animators.forEach((e) -> e.setFrameDuration((1f / getSpeed()) * SPEED_TO_ANIMATION_TIME));
        // stats erhöhen
        getWorld().addMoney(1);
    }

    @Override
    public void die() {
        level = 0;
        // stats erhöhen
        getWorld().addMoney(5);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (level <= 0) return;
        animators.get(level - 1).update(delta);
    }

    /// Zum Speichern des Gegners als .json datei
    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = super.toJson();
        value.addChild("type", new JsonValue(JSON_TYPE));
        value.addChild("level", new JsonValue(level));
        return value;
    }

    /// Lädt einen Gegner aus json ein.
    /// world muss bereits gesetzt sein, level ist egal
    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
        this.level = json.getInt("level");
        animators.forEach((e) -> e.setFrameDuration(getSpeed() / SPEED_TO_ANIMATION_TIME));
    }

    @Override
    public void dispose() {
        // nichts, da Texturen im AssetManager verwaltet werden
    }
}
