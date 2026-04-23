package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public abstract class Enemy implements Disposable {
    public static final float HEIGHT = 8f;
    public static final float WIDTH = 8f;
    public abstract Texture getTexture();

    public abstract int getHealth();

    public abstract float getSpeed();

    public abstract Array<Vector2> getPath();

    public void draw(SpriteBatch batch) {
        batch.draw(getTexture(), getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
    }

    public float getHeight() {
        return HEIGHT;
    }

    public float getWidth() {
        return WIDTH;
    }

    public abstract float getX();

    public abstract float getY();

    public abstract void move(float delta);

    @Override
    public void dispose() {
        getTexture().dispose();
    }

    public abstract void pop(int damage);

    public abstract void die();
}
