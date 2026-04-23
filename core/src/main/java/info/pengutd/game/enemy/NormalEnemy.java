package info.pengutd.game.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class NormalEnemy extends Enemy {
    /**
     * Anzahl Pixel pro Sekunde
     * 8 => 0.5 tiles pro Sekunde
     */
    private static final float MOVEMENT_MULTIPLIER = 8f;
    private final Texture texture;
    private final Array<Vector2> path;
    private final Vector2 pos;
    private final Rectangle rect;
    int currentPathIndex = 0;
    private int level;

    public NormalEnemy(int level, TiledMap map) {
        texture = new Texture(Gdx.files.internal("Epstein.png"));
        this.level = level;

        path = new Array<>();
        MapLayer mapLayer = map.getLayers().get("path");
        for (MapObject obj : mapLayer.getObjects()) {
            if (obj instanceof PointMapObject) {
                PointMapObject point = (PointMapObject) obj;
                path.add(point.getPoint().cpy().add(MathUtils.random(-2f, 2f), MathUtils.random(-2f, 2f)));
            }
        }

        Vector2 start = path.get(0);
        this.pos = new Vector2(start);

        rect = new Rectangle(pos.x, pos.y, WIDTH, HEIGHT);
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public float getX() {
        return pos.x;
    }

    @Override
    public float getY() {
        return pos.y;
    }

    @Override
    public int getHealth() {
        return level;
    }

    @Override
    public float getSpeed() {
        return MOVEMENT_MULTIPLIER * level;
    }

    @Override
    public Array<Vector2> getPath() {
        return path;
    }

    @Override
    public void move(float delta) {

        if (currentPathIndex >= path.size - 1) return;
        Vector2 target = path.get(currentPathIndex).lerp(path.get(currentPathIndex + 1), 0.02f);


        if (getPos().dst(target) < 2f) {
            currentPathIndex++;
        }

        Vector2 dir = target.cpy().sub(pos).nor();
        pos.add(dir.scl(getSpeed() * delta));

        rect.setPosition(pos);
    }

    public Vector2 getPos() {
        return pos;
    }

    @Override
    public void pop(int damage) {
        level -= damage;
        if (level <= 0) die();
    }

    public boolean isAlive() {
        return level <= 0;
    }

    @Override
    public void die() {
        // Geld geben + stats erhöhen
    }
}
