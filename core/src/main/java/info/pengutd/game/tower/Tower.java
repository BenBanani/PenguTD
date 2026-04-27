package info.pengutd.game.tower;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class Tower implements Disposable{
    public int[] upgradecost;
    public int basecost;
    public abstract Texture getTexture();

    public void draw(SpriteBatch batch) {
        batch.draw(getTexture(), getX() - getWidth() / 2, getY() - getHeight() / 2, -getWidth(), getHeight());
    }

    public abstract float getHeight();

    public abstract float getWidth();

    public abstract float getX();

    public abstract float getY();

    @Override
    public void dispose() {
        getTexture().dispose();
    }

}
