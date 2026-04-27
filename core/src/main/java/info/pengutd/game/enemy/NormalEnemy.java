package info.pengutd.game.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import info.pengutd.game.World;

public class NormalEnemy extends Enemy {
    public static final float HEIGHT = 0.8f;
    public static final float WIDTH = 0.44f;
    /**
     * Anzahl Pixel pro Sekunde
     * 8 => 0.5 tiles pro Sekunde
     */
    private static final float MOVEMENT_MULTIPLIER = 1f;
    private static final float POP_DURATION = 0.5f;
    private final Texture texture;
    private final Texture popTexture;
    private final Array<Vector2> path;
    private final Vector2 pos;
    private final Rectangle rect;
    int currentPathIndex = 0;
    private int level;
    private float popTimeLeft = 0f;

    public NormalEnemy(int level, World world) {
        super(world);
        texture = new Texture(Gdx.files.internal("game/enemy/pengu.png"));
        popTexture = new Texture(Gdx.files.internal("game/enemy/pop.png"));
        this.level = level;

        path = new Array<>();
        MapLayer mapLayer = world.getMap().getLayers().get("path");
        if (mapLayer != null) {
            for (MapObject obj : mapLayer.getObjects()) {
                if (obj instanceof PointMapObject) {
                    PointMapObject point = (PointMapObject) obj;
                    path.add(point.getPoint().cpy().add(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f)));
                }
            }
            Vector2 start = path.get(0);
            this.pos = new Vector2(start);
        } else {
            Gdx.app.error("NormalEnemy", "No path layer found in map");
            this.pos = new Vector2(0, 0);
        }

        rect = new Rectangle(pos.x, pos.y, WIDTH, HEIGHT);
    }

    @Override
    public Texture getTexture() {
        return popTimeLeft > 0 ? popTexture : texture;
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
        return MOVEMENT_MULTIPLIER * level * getWorld().getTileWidth();
    }

    @Override
    public Array<Vector2> getPath() {
        return path;
    }

    @Override
    public float getHeight() {
        return HEIGHT * getWorld().getTileHeight();
    }

    @Override
    public float getWidth() {
        if (popTimeLeft <= 0) {
            return WIDTH * getWorld().getTileWidth();
        }
        return getHeight(); // pop texture ist quadratisch
    }

    @Override
    public void move(float delta) {

        if (popTimeLeft > 0) {
            popTimeLeft -= delta;
            return;
        }

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
        popTimeLeft = POP_DURATION;
        if (level <= 0) die();
        // Geld geben + stats erhöhen
    }

    public boolean isAlive() {
        return level <= 0;
    }

    @Override
    public void die() {
        // Geld geben + stats erhöhen
    }
}
