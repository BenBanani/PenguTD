package info.pengutd.game.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import info.pengutd.game.GameObject;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.save.JsonSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// Base klasse für alle Türme
public abstract class Tower extends GameObject implements Disposable, JsonSerializable {
    private boolean debug = false;
    private boolean preview = false;

    protected Tower(@NotNull World world) {
        super(world);
    }

    public abstract int getCost();

    /// @return Range in pixeln
    public abstract float getRange();

    public abstract int getDamage();

    /// @return Schüsse pro Sekunde
    public abstract float getAttackSpeed();

    /// @return ziel des Turmes
    @Nullable
    public abstract Enemy getTargetEnemy();

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
            if (getWorld().canPlaceTower(getPos())) {
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
            Rectangle box = (Rectangle) getHitbox();
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

    public Tower preview() {
        preview = true;
        return this;
    }

    public boolean isPreview() {
        return preview;
    }

    ///  logik update des Towers
    @Override
    public abstract void update(float delta);

    /// Schaltet den Debug Modus an
    /// Jetzt werden zusätzlich die Hitbox, Range und Target gezeichnet
    ///
    /// @return this
    public @NotNull Tower debug() {
        debug = true;
        return this;
    }
}
