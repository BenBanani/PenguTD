package info.pengutd.game.tower;
import com.badlogic.gdx.graphics.Texture;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Diddy extends Tower{
    public int basecost = 100;
    private final Texture texture;
    private final Vector2 pos = new Vector2();
    SpriteBatch batch;
    public Diddy(SpriteBatch batch){ 
        texture = new Texture(Gdx.files.internal("game/enemy/image.png"));
        this.pos.y = Gdx.input.getY();
        this.pos.x = Gdx.input.getX();
        this.batch = batch;
    }
    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public float getHeight() {
        return texture.getHeight() / 6f ;
    }

    @Override
    public float getWidth() {
        return texture.getWidth() / 6f;
    }
    @Override
    public float getX() {
        return pos.x;
    }

    @Override
    public float getY() {
        return pos.y;
    }
    public void render(float delta) {
        batch.begin();
        batch.draw(texture, 100, 100);
        batch.end();
    }
}
