package info.pengutd.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.enemy.NormalEnemy;
import info.pengutd.save.JsonSerializable;
import info.pengutd.screen.TowerSelection;

public class World implements Screen, InputProcessor, JsonSerializable {

    private SpriteBatch batch;
    private Viewport viewport;
    private String mapName = "map1";
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    ///  debug enemy
    private Array<Enemy> enemies;
    /// in tiles
    private int mapWidth;
    ///  in tiles
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;
    private TowerSelection towerSelection;
    private final boolean fromJson;

    /// Normaler Konstruktor für eine neue Welt
    public World() {
        this(false);
    }

    ///  Konstruktor muss aufgerufen werden, wenn die Welt aus einer Json Datei geladen wird,
    /// da show() möglicherweise nach toJson() aufgerufen wird und somit sonst alles selbst neu initialisiert
    public World(boolean fromJson) {
        this.fromJson = fromJson;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        batch = new SpriteBatch();
        if (!fromJson) {
            map = new TmxMapLoader().load("map/" + mapName + ".tmx");
            mapRenderer = new OrthogonalTiledMapRenderer(map);

            mapWidth = map.getProperties().get("width", Integer.class);
            mapHeight = map.getProperties().get("height", Integer.class);
            tileWidth = map.getProperties().get("tilewidth", Integer.class);
            tileHeight = map.getProperties().get("tileheight", Integer.class);

            viewport = new FitViewport(
                mapWidth * tileWidth,
                mapHeight * tileHeight
            );

            enemies = new Array<>();
            enemies.add(new NormalEnemy(4, this).debug());

            towerSelection = new TowerSelection(viewport, this);
        }
    }

    public TiledMap getMap() {
        return map;
    }

    @Override
    public void render(float delta) {
        updateLogic(delta);

        updateGraphics(delta);
    }

    private void updateGraphics(float delta) {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();

        mapRenderer.setView((OrthographicCamera) viewport.getCamera());
        mapRenderer.render();

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        renderMapObjects();

        for (Enemy enemy : this.enemies) {
            enemy.draw(batch);
        }

        batch.end();

        towerSelection.render(delta);
    }

    private void updateLogic(float delta) {
        for (Enemy enemy : this.enemies) {
            enemy.move(delta);
        }
    }

    private void renderMapObjects() {
        MapLayer objectLayer = map.getLayers().get("detail_tiles");

        Array<TextureMapObject> objects = new Array<>();

        for (MapObject obj : objectLayer.getObjects()) {
            if (obj instanceof TextureMapObject) {
                TextureMapObject texObj = (TextureMapObject) obj;
                objects.add(texObj);
            }
        }
        objects.sort((a, b) -> Float.compare(b.getY(), a.getY()));
        objects.forEach((obj) -> batch.draw(obj.getTextureRegion(), obj.getX(), obj.getY()));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // todo pause game
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        mapRenderer.dispose();
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        enemies.get(0).pop(1);
        System.out.println(this.toJson());
        System.out.println("----------------");
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public int get_map_height() {
        return mapHeight;
    }

    public int get_map_width() {
        return mapWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    @Override
    public JsonValue toJson() {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        value.addChild("type", new JsonValue("world"));
        value.addChild("map", new JsonValue(mapName));
        // gegner
        JsonValue jsonEnemies = new JsonValue(JsonValue.ValueType.array);
        for (Enemy enemy : enemies) {
            jsonEnemies.addChild(enemy.toJson());
        }
        value.addChild("enemies", jsonEnemies);
        // todo tower speichern
        return value;
    }

    /// @throws IllegalArgumentException wenn ungültige enemy types in der json sind
    /// @throws IllegalStateException wenn fromJson false ist
    @Override
    public void fromJson(JsonValue json) {
        if (!fromJson) {
            throw new IllegalStateException("fromJson ist false, aber fromJson() wurde aufgerufen");
        }

        // map laden
        mapName = json.getString("map");
        // alte map disposen
        if (map != null) {
            map.dispose();
        }
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }

        map = new TmxMapLoader().load("map/" + mapName + ".tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);

        viewport = new FitViewport(mapWidth * tileWidth, mapHeight * tileHeight);
        // neue TowerSelection, da neuer Viewport
        towerSelection = new TowerSelection(viewport, this);

        JsonValue jsonEnemies = json.get("enemies");
        if (enemies == null) {
            enemies = new Array<>();
        } else {
            for (Enemy enemy : enemies) {
                enemy.dispose();
            }
            enemies.clear();
        }
        for (JsonValue jsonEnemy : jsonEnemies) {
            Enemy enemy;
            String enemyType = jsonEnemy.getString("type");
            if ("normal_enemy".equals(enemyType)) {
                enemy = new NormalEnemy(0, this);
            } else {
                throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
            }
            enemy.fromJson(jsonEnemy);
            enemies.add(enemy);
        }
    }
}
