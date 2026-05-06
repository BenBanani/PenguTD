package info.pengutd.game;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.enemy.NormalEnemy;
import info.pengutd.game.tower.NormalTower;
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

            enemies.add(new NormalEnemy(4, this, nextEntityId++).debug());

            towers.add(new NormalTower(new Vector2(200, 300), this).debug());

            towerSelection = new TowerSelection(viewport, this);

            previewTower = new NormalTower(new Vector2(200, 300), this).preview();
        }
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

        for (Enemy enemy : this.enemies) {
            enemy.draw(batch);
        }

        for (Tower tower : this.towers) {
            tower.draw(batch);
        }

        if (previewTower != null) {
            previewTower.draw(batch);
        }

        batch.end();

        towerSelection.render(delta);
    }

    private void updateLogic(float delta) {
        for (Enemy enemy : this.enemies) {
            enemy.update(delta);
        }

        for (Tower tower : this.towers) {
            tower.update(delta);
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
        enemies.add(new NormalEnemy(2, this, createEntityId()));
        System.out.println(this.toJson());
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
        ((NormalTower)previewTower).setPos(viewport.unproject(new Vector2(screenX, screenY)));
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
    public @NotNull JsonValue toJson() {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        value.addChild("type", new JsonValue("world"));
        value.addChild("map", new JsonValue(mapName));
        value.addChild("next_entity_id", new JsonValue(nextEntityId));
        // gegner
        JsonValue jsonEnemies = new JsonValue(JsonValue.ValueType.array);
        for (Enemy enemy : enemies) {
            jsonEnemies.addChild(enemy.toJson());
        }
        value.addChild("enemies", jsonEnemies);
        // türme
        JsonValue jsonTowers = new JsonValue(JsonValue.ValueType.array);
        for (Tower tower : towers) {
            jsonTowers.addChild(tower.toJson());
        }
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
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        enemies.clear();
        for (JsonValue jsonEnemy : jsonEnemies) {
            Enemy enemy;
            String enemyType = jsonEnemy.getString("type");
            if ("normal_enemy".equals(enemyType)) {
                enemy = new NormalEnemy(0, this, jsonEnemy.getInt("id")).debug();
            } else {
                throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
            }
            enemy.fromJson(jsonEnemy);
            enemies.add(enemy);
        }

        // towers
        JsonValue jsonTowers = json.get("towers");
        for (Tower tower : towers) {
            tower.dispose();
        }
        towers.clear();
        for (JsonValue jsonTower : jsonTowers) {
            Tower tower;
            String towerType = jsonTower.getString("type");
            if ("normal_tower".equals(towerType)) {
                tower = new NormalTower(new Vector2(), this).debug();
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
        for (Enemy e : this.enemies) {
            if (e.getId() == id) {
                enemy = e;
            }
        }
        return enemy;
    }

    public boolean canPlaceTower(@NotNull Vector2 pos, Tower tower) {
        TiledMapTileLayer groundLayer = (TiledMapTileLayer) map.getLayers().get("ground");
        TiledMapTileLayer.Cell cell = groundLayer.getCell((int)(pos.x / tileWidth),(int)(pos.y / tileHeight));
        if (cell == null) return false;

        if (!cell.getTile().getProperties().containsKey("placable")) return false;

        Rectangle towerHitbox = new Rectangle(tower.getHitbox());
        towerHitbox.setCenter(pos);
        for (Tower t : towers) {
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
