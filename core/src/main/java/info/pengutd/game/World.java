package info.pengutd.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.enemy.WarriorEnemy;
import info.pengutd.game.tower.SnowballTower;
import info.pengutd.game.tower.Tower;
import info.pengutd.save.JsonSerializable;
import info.pengutd.screen.TowerSelection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class World implements Screen, InputProcessor, JsonSerializable {
    ///  debug enemy
    private final @NotNull Array<Enemy> enemies = new Array<>();
    private final @NotNull Array<Tower> towers = new Array<>();
    private final boolean fromJson;
    private SpriteBatch batch;
    private Viewport viewport;
    private String mapName = "map1";
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    /// in tiles
    private int mapWidth;
    ///  in tiles
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;
    private TowerSelection towerSelection;
    private int nextEntityId = 0;
    private @Nullable Tower previewTower;

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
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);
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

            enemies.add(new WarriorEnemy(4, this, nextEntityId++));

            towers.add(new SnowballTower(this, new Vector2(200, 300)).debug());

            towerSelection = new TowerSelection(viewport, this);
        }
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(towerSelection.getStage());
        multiplexer.addProcessor(Gdx.input.getInputProcessor());
        Gdx.input.setInputProcessor(multiplexer);
    }

    public @NotNull Array<Enemy> getEnemies() {
        return enemies;
    }

    /// @return die map oder null, wenn noch keine map geladen wurde
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

        enemies.forEach(e -> e.draw(batch));

        towers.forEach(t -> t.draw(batch));

        if (previewTower != null) {
            previewTower.draw(batch);
        }

        batch.end();

        towerSelection.render(delta);
    }

    private void updateLogic(float delta) {
        enemies.forEach(e -> e.update(delta));
        towers.forEach(t -> t.update(delta));
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
        enemies.add(new WarriorEnemy(2, this, createEntityId()));
        if (previewTower != null) {
            towers.add(previewTower.place());
        }
        setSelectedTower(0);
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
        if (previewTower == null) return false;
        previewTower.setPos(viewport.unproject(new Vector2(screenX, screenY)));
        return false;
    }

    public void setSelectedTower(int type) {
        if (type == 0) {
            previewTower = null;
            return;
        }

        switch (type) {
            case 1:
                previewTower = new SnowballTower(this, new Vector2()).preview();
                break;

            // später weitere types
            // case 2: ...
        }
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
    public @NotNull JsonValue toJson() {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        value.addChild("type", new JsonValue("world"));
        value.addChild("map", new JsonValue(mapName));
        value.addChild("next_entity_id", new JsonValue(nextEntityId));
        // gegner
        JsonValue jsonEnemies = new JsonValue(JsonValue.ValueType.array);
        enemies.forEach(e -> jsonEnemies.addChild(e.toJson()));
        value.addChild("enemies", jsonEnemies);
        // türme
        JsonValue jsonTowers = new JsonValue(JsonValue.ValueType.array);
        towers.forEach(t -> jsonTowers.addChild(t.toJson()));
        value.addChild("towers", jsonTowers);

        return value;
    }

    /// @throws IllegalArgumentException wenn ungültige enemy types in der json sind
    /// @throws IllegalStateException    wenn fromJson false ist
    @Override
    public void fromJson(@NotNull JsonValue json) {
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

        nextEntityId = json.getInt("next_entity_id");
        // enemies
        JsonValue jsonEnemies = json.get("enemies");
        enemies.forEach(Disposable::dispose);
        enemies.clear();
        for (JsonValue jsonEnemy : jsonEnemies) {
            Enemy enemy;
            String enemyType = jsonEnemy.getString("type");
            if (WarriorEnemy.JSON_TYPE.equals(enemyType)) {
                enemy = new WarriorEnemy(0, this, jsonEnemy.getInt("id"));
            } else {
                throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
            }
            enemy.fromJson(jsonEnemy);
            enemies.add(enemy);
        }

        // towers
        JsonValue jsonTowers = json.get("towers");
        towers.forEach(Disposable::dispose);
        towers.clear();
        for (JsonValue jsonTower : jsonTowers) {
            Tower tower;
            String towerType = jsonTower.getString("type");
            if (SnowballTower.JSON_TYPE.equals(towerType)) {
                tower = new SnowballTower(this, new Vector2());
            } else {
                throw new IllegalArgumentException("Unknown tower type: " + towerType);
            }
            tower.fromJson(jsonTower);
            towers.add(tower);
        }
    }

    public int createEntityId() {
        return nextEntityId++;
    }

    /// @return ein Enemy mit der gegebenen ID oder null wenn es keinen gibt
    public @Nullable Enemy getEnemyFromId(int id) {
        Enemy enemy = null;
        for (int i = 0; i < enemies.size; i++) {
            Enemy e = enemies.get(i);
            if (e.getId() == id) {
                enemy = e;
            }
        }
        return enemy;
    }

    public boolean canPlaceTower(@NotNull Vector2 pos, Tower tower) {
        TiledMapTileLayer groundLayer = (TiledMapTileLayer) map.getLayers().get("ground");
        TiledMapTileLayer.Cell cell = groundLayer.getCell((int) (pos.x / tileWidth), (int) (pos.y / tileHeight));
        if (cell == null) return false;

        if (!cell.getTile().getProperties().containsKey("placable")) return false;

        Rectangle towerHitbox = new Rectangle(tower.getHitbox());
        towerHitbox.setCenter(pos);
        for (int i = 0; i < towers.size; i++) {
            Tower t = towers.get(i);
            if (t.getHitbox().overlaps(towerHitbox)) return false;
        }

        MapLayer objectLayer = map.getLayers().get("detail_tiles");
        for (MapObject object : objectLayer.getObjects()) {

            if (object instanceof TextureMapObject) {
                TextureMapObject textureObject = (TextureMapObject) object;

                TextureRegion region = textureObject.getTextureRegion();

                float width = region.getRegionWidth() * textureObject.getScaleX();
                float height = region.getRegionHeight() * textureObject.getScaleY();

                Rectangle objectBounds =
                    new Rectangle(textureObject.getX(), textureObject.getY(), width, height);

                if (objectBounds.overlaps(towerHitbox)) {
                    return false;
                }
            }
        }

        return true;
    }
}
