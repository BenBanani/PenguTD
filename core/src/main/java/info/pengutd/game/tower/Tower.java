package info.pengutd.game.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.GameObject;
import info.pengutd.game.World;
import info.pengutd.game.enemy.BushEnemy;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.enemy.FatEnemy;
import info.pengutd.game.tower.projectile.Projectile;
import info.pengutd.save.JsonSerializable;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// Base klasse für alle Türme
public abstract class Tower extends GameObject implements Disposable, JsonSerializable {
    private boolean debug = false;
    private boolean preview = false;
    private @Nullable Enemy targetEnemy = null;
    private float shotCooldown = 0f;
    private float timeSinceLastAttack = 0f;
    private int id;
    private final @NotNull TowerAnimator animator;

    protected Tower(@NotNull World world, @NotNull Vector2 pos, int id) {
        super(world, pos);
        this.id = id;
        animator = new TowerAnimator(getType(), PenguTD.getInstance().getAssetManager().get(Assets.TOWER_ATLAS));
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        return animator.getTexture(timeSinceLastAttack, shotCooldown, 1/getAttackSpeed(), getTargetEnemy() != null);
    }

    public abstract int getCost();

    /// @return Range in pixeln
    public abstract float getRange();

    public abstract int getDamage();

    /// @return Schüsse pro Sekunde
    public abstract float getAttackSpeed();

    /// @return ziel des Turmes
    public @Nullable Enemy getTargetEnemy() {
        return targetEnemy;
    }

    protected void setTargetEnemy(@Nullable Enemy targetEnemy) {
        this.targetEnemy = targetEnemy;
    }

    /// Zeichnet den Tower auf den screen
    /// SpriteBatch.begin() muss davor aufgerufen werden
    @Override
    public void draw(@NotNull SpriteBatch batch) {
        if (isPreview()) {
            Color oldColor = batch.getColor().cpy();
            batch.setColor(1f, 1f, 1f, 0.5f); // transparent zeichnen
            super.draw(batch);
            batch.setColor(oldColor);

            // Range anzeigen
            batch.end();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); // Blending für transparenz

            ShapeRenderer renderer = new ShapeRenderer();
            renderer.setProjectionMatrix(batch.getProjectionMatrix());
            renderer.setAutoShapeType(true);

            renderer.begin(ShapeRenderer.ShapeType.Filled);
            if (getWorld().canPlaceTower(getPos(), this)) {
                renderer.setColor(new Color(0.75f, 0.75f, 0.75f, 0.25f));
            } else {
                renderer.setColor(new Color(1f, 0f, 0f, 0.3f));
            }
            renderer.circle(getX(), getY(), getRange());

            renderer.set(ShapeRenderer.ShapeType.Line);
            renderer.setColor(new Color(0.75f, 0.75f, 0.75f, 1f));
            renderer.circle(getX(), getY(), getRange());

            renderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.begin();
        } else {
            super.draw(batch);
        }
        if (debug) {
            batch.end(); // zeichnen mit internen batch von ShapeRenderer

            ShapeRenderer renderer = new ShapeRenderer();
            renderer.setProjectionMatrix(batch.getProjectionMatrix());
            renderer.begin(ShapeRenderer.ShapeType.Line);
            // Hitbox
            renderer.setColor(Color.RED);
            Rectangle box = getHitbox();
            renderer.rect(box.x, box.y, box.width, box.height);

            // Range
            renderer.setColor(Color.BLUE);
            renderer.circle(getX(), getY(), getRange());

            // Target
            renderer.setColor(Color.GREEN);
            if (getTargetEnemy() != null) {
                renderer.line(new Vector2(getX(), getY()), new Vector2(getTargetEnemy().getX(), getTargetEnemy().getY()));
            }

            renderer.end();

            batch.begin();
        }
    }

    ///  Setzt den Tower in den preview modus.
    /// Der Tower wird jetzt transparent gezeichnet und die Range angezeigt.
    /// Der Tower kann nicht schießen und es sollte kein update aufgerufen werden.
    public Tower preview() {
        preview = true;
        return this;
    }

    ///  Platziert einen preview Tower an der aktuellen Position
    /// → Setzt preview auf false
    public Tower place() {
        preview = false;
        return this;
    }

    public boolean isPreview() {
        return preview;
    }

    ///  logik update des Towers
    @Override
    public void update(float delta) {
        float speedMultiplier = getAttackSpeedMultiplier();
        shotCooldown -= delta * speedMultiplier;
        timeSinceLastAttack += delta * speedMultiplier;

        if (targetEnemy == null || !targetEnemy.isAlive() || !inRange(targetEnemy)) {
            targetEnemy = findNewTarget();
        } else {
            float dx = targetEnemy.getPos().x - getPos().x;
            float dy = targetEnemy.getPos().y - getPos().y;
            float deg = MathUtils.radiansToDegrees * MathUtils.atan2(dy, dx) - 90;
            setRotationDeg(deg);
            if (shotCooldown <= 0) {
                shoot();
            }
        }
    }

    /// slow Effekte von FatEnemy etc.
    public float getAttackSpeedMultiplier() {
        final float[] multiplier = {1f};

        getWorld().getEnemies().forEach(e -> {
            if (e instanceof FatEnemy) {
                if (e.isAlive() && ((FatEnemy) e).affectsTower(this)) {
                    multiplier[0] *= FatEnemy.SLOW_MULTIPLIER;
                }
            }
        });
        return (float) Math.max(multiplier[0], 0.5);
    }

    /// Wird aufgerufen, wenn ein Projektil, das von diesem Tower geschossen wurde ein Gegner trifft.
    /// Hier können zum Beispiel stats erhöht werden
    public void onProjectileHit(@NotNull Projectile projectile, @NotNull Enemy enemy) {
        // todo stats?
    }

    /// Schießt ein Projektil von diesem Tower.
    /// Setzt den shotCooldown zurück.
    /// Achtung diese Methode prüft nicht, ob überhaupt geschossen werden kann
    private void shoot() {
        shotCooldown = 1 / getAttackSpeed();
        timeSinceLastAttack = 0;
        Projectile projectile = createProjectile();
        getWorld().addProjectile(projectile);
    }

    /// @return die Position der Hand des Towers.
    /// von hier sollten die Projektile aus geschossen werden
    protected Vector2 getHandPos() {
        Vector2 pos = getPos();
        // Offset der Hand relativ zum Tower-Zentrum
        Vector2 offset = new Vector2(getHandOffset(), 0f);
        offset.rotateDeg(getRotationDeg());

        return pos.add(offset);
    }

    /// @return offset der Hand des Towers in Pixeln.
    /// Diese wird gebraucht um die HandPosition zu berechnen
    /// > 0 nach rechts
    /// < 0 nach links
    protected abstract float getHandOffset();

    /// Erstellt ein neues Projektil.
    /// Dieses sollte schon voll initialisiert sein und muss nur noch zur Welt hinzugefügt werden
    protected abstract @NotNull Projectile createProjectile();


    /// @return das nächste Enemy zum Turm, oder null, wenn es keines gibt
    private @Nullable Enemy findNewTarget() {
        Enemy target = null;

        float closestDst2 = Float.MAX_VALUE;

        for (int i = 0; i < getWorld().getEnemies().size; i++) {
            Enemy enemy = getWorld().getEnemies().get(i);

            float dst2 = getPos().dst2(enemy.getPos());

            if (dst2 <= getRange() * getRange() && dst2 < closestDst2) {
                if (enemy instanceof BushEnemy) {
                    BushEnemy bush = ((BushEnemy) enemy);
                    if (!canSee(bush)) continue;
                }

                closestDst2 = dst2;
                target = enemy;
            }
        }

        return target;
    }

    public boolean canSee(BushEnemy enemy) {
        return enemy.isVisible();
    }


    /// @return ist targetEnemy innerhalb der Range des Towers
    private boolean inRange(@Nullable Enemy targetEnemy) {
        if (targetEnemy == null) return false;
        return this.getPos().dst2(targetEnemy.getPos()) <= getRange() * getRange();
    }

    /// Schaltet den Debug Modus an
    /// Jetzt werden zusätzlich die Hitbox, Range und Target gezeichnet
    ///
    /// @return this
    public @NotNull Tower debug() {
        debug = true;
        return this;
    }

    public float getShotCooldown() {
        return shotCooldown;
    }

    public void setShotCooldown(float shotCooldown) {
        this.shotCooldown = shotCooldown;
    }

    public float getTimeSinceLastAttack() {
        return timeSinceLastAttack;
    }

    @Override
    @MustBeInvokedByOverriders
    public @NotNull JsonValue toJson() {
        JsonValue value = super.toJson();
        value.addChild("shot_cooldown", new JsonValue(getShotCooldown()));
        value.addChild("time_since_last_attack", new JsonValue(getTimeSinceLastAttack()));
        value.addChild("target", new JsonValue(getTargetEnemy() != null ? getTargetEnemy().getId() : -1));
        value.addChild("id", new JsonValue(getId()));
        return value;
    }

    @Override
    @MustBeInvokedByOverriders
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
        setShotCooldown(json.get("shot_cooldown").asFloat());
        timeSinceLastAttack = json.get("time_since_last_attack").asFloat();
        setTargetEnemy(getWorld().getEnemyFromId(json.get("target").asInt()));
        id = json.get("id").asInt();
    }

    public int getId() {
        return id;
    }
}
