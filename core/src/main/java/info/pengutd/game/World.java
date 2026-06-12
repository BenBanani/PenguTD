package info.pengutd.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import info.pengutd.PenguTD;
import info.pengutd.game.enemy.*;
import info.pengutd.game.overlay.DefeatOverlay;
import info.pengutd.game.overlay.PauseOverlay;
import info.pengutd.game.overlay.TowerSelection;
import info.pengutd.game.overlay.VictoryOverlay;
import info.pengutd.game.tower.*;
import info.pengutd.game.tower.projectile.FishProjectile;
import info.pengutd.game.tower.projectile.IceProjectile;
import info.pengutd.game.tower.projectile.Projectile;
import info.pengutd.game.tower.projectile.SnowballProjectile;
import info.pengutd.save.JsonSerializable;
import info.pengutd.screen.StartScreen;
import info.pengutd.stats.GameStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class World implements Screen, InputProcessor, JsonSerializable {
    private static final int START_MONEY = 100;
    private static final int START_HP = 100;
    private final @NotNull Array<Enemy> enemies = new Array<>();
    private final @NotNull Array<Tower> towers = new Array<>();
    private final @NotNull Array<Projectile> projectiles = new Array<>();
    private final @NotNull Array<SpeedModifier> speedModifiers = new Array<>();
    private final boolean fromJson;
    private final @NotNull GameStats stats; // referenz zu StatsManager.gameStats
    private SpriteBatch batch;
    private Viewport viewport;
    private String mapName = "map3";
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    /// in tiles
    private int mapWidth;
    ///  in tiles
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;
    private TowerSelection towerSelection;
    private PauseOverlay pauseOverlay;
    private DefeatOverlay defeatOverlay;
    private VictoryOverlay victoryOverlay;
    private InputMultiplexer multiplexer;
    private int nextEntityId = 0;
    private int nextTowerId = 0;
    private @Nullable Tower previewTower;
    ///  game state
    private int money = START_MONEY;
    private int hp = START_HP;
    private int score;
    private boolean paused = false;
    private boolean won = false;

    /// Normaler Konstruktor für eine neue Welt
    public World() {
        this(false);
    }

    ///  Konstruktor muss aufgerufen werden, wenn die Welt aus einer Json Datei geladen wird,
    /// da show() möglicherweise nach toJson() aufgerufen wird und somit sonst alles selbst neu initialisiert
    public World(boolean fromJson) {
        this.fromJson = fromJson;

        stats = PenguTD.getInstance().getStatsManager().createGameStats();
    }

    public Viewport getViewport() {
        return viewport;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        if (this.paused == paused) return; // nicht mehrmals aufrufen

        this.paused = paused;
        if (paused) {
            pauseOverlay.show();
        } else {
            pauseOverlay.hide();
        }
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        if (!fromJson) {
            map = new TmxMapLoader().load("map/" + mapName + ".tmx");
            mapRenderer = new OrthogonalTiledMapRenderer(map);

            for (TiledMapTile tile : map.getTileSets().getTileSet(0)) {
                Texture texture = tile.getTextureRegion().getTexture();
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }

            mapWidth = map.getProperties().get("width", Integer.class);
            mapHeight = map.getProperties().get("height", Integer.class);
            tileWidth = map.getProperties().get("tilewidth", Integer.class);
            tileHeight = map.getProperties().get("tileheight", Integer.class);

            viewport = new FitViewport(mapWidth * tileWidth, mapHeight * tileHeight);
            viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        }

        createTowerSelection();

        pauseOverlay = new PauseOverlay(this);
        defeatOverlay = new DefeatOverlay(this);
        victoryOverlay = new VictoryOverlay(this);
    }

    public @NotNull Array<Enemy> getEnemies() {
        return enemies;
    }

    public @NotNull Array<Tower> getTowers() {
        return towers;
    }

    public @NotNull Array<Projectile> getProjectiles() {
        return projectiles;
    }

    public @NotNull Array<SpeedModifier> getSpeedModifiers() {
        return speedModifiers;
    }

    /// @return die map oder null, wenn noch keine map geladen wurde
    public @NotNull TiledMap getMap() {
        return map;
    }

    @Override
    public void render(float delta) {
        updateCamera();
        if (!paused) {
            updateLogic(delta);
        }
        updateGraphics(delta);
    }

    /// rundet die Kameraposition auf ganze Zahlenwerte. Dadurch sollen render Fehler in der Map minimiert werden
    private void updateCamera() {
        OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();

        camera.position.x = Math.round(camera.position.x);
        camera.position.y = Math.round(camera.position.y);

        camera.update();
    }

    /// Zeichnet alle Elemente auf den Bildschirm
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

        projectiles.forEach(p -> p.draw(batch));

        if (previewTower != null) {
            previewTower.draw(batch);
        }

        batch.end();

        towerSelection.render(delta);

        pauseOverlay.act(delta); // act immer, damit animationen fertig werden
        if (paused) {
            pauseOverlay.render(delta);
        } else if (hp <= 0) {
            defeatOverlay.render(delta);
        } else if (won) {
            victoryOverlay.render(delta);
        }
    }

    /// Updated alle Objekte in der Welt
    private void updateLogic(float delta) {
        enemies.forEach(e -> e.update(delta));
        towers.forEach(t -> t.update(delta));
        projectiles.forEach(p -> p.update(delta));

        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            if (!e.isAlive()) {
                enemies.removeIndex(i).dispose();
            }
        }

        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            if (!p.isAlive()) {
                projectiles.removeIndex(i).dispose();
            }
        }
    }

    /// Zeichnet Hindernisse der map
    private void renderMapObjects() {
        MapLayer objectLayer = map.getLayers().get("detail_tiles");
        if (objectLayer == null) {
            return;
        }

        Array<TextureMapObject> objects = new Array<>();

        for (MapObject obj : objectLayer.getObjects()) {
            if (obj instanceof TextureMapObject) {
                TextureMapObject texObj = (TextureMapObject) obj;
                objects.add(texObj);
            }
        }
        /// Depth sorting damit Objekte weiter vorne auch über anderen gezeichnet werden
        objects.sort((a, b) -> Float.compare(b.getY(), a.getY()));
        objects.forEach((obj) -> batch.draw(obj.getTextureRegion(), obj.getX(), obj.getY(), obj.getOriginX(), obj.getOriginY(), obj.getTextureRegion().getRegionWidth(), obj.getTextureRegion().getRegionHeight(), obj.getScaleX(), obj.getScaleY(), obj.getRotation()));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        towerSelection.resize(width, height);
        pauseOverlay.resize(width, height);
        defeatOverlay.resize(width, height);
        victoryOverlay.resize(width, height);
    }

    @Override
    public void pause() {
        setPaused(true);
    }

    @Override
    public void resume() {
        setPaused(false);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
        towerSelection.dispose();
        pauseOverlay.dispose();
        enemies.forEach(Disposable::dispose);
        towers.forEach(Disposable::dispose);
        projectiles.forEach(Disposable::dispose);
        if (previewTower != null) {
            previewTower.dispose();
        }
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (paused) return false;  // keine Tower platzieren während pausiert ist
        if (button == Input.Buttons.RIGHT) {
            setSelectedTower(0);
        }
        double d = Math.random();
        if (d < 0.25) {
            addEnemy(new FatEnemy(this, createEntityId()));
        } else if (d < 0.5) {
            addEnemy(new CoolEnemy(this, createEntityId()));
        } else if (d < 0.75) {
            addEnemy(new BushEnemy(this, createEntityId()));
        } else {
            addEnemy(new WarriorEnemy(4, this, createEntityId()));
        }

        if (previewTower != null && canPlaceTower(previewTower.getPos(), previewTower) && spendMoney(previewTower.getCost())) {
            addTower(previewTower.place());
            PenguTD.getInstance().getStatsManager().addPlacedTower();
            setSelectedTower(0);
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            setSelectedTower(0);
        } else if (keycode == Input.Keys.S) {
            saveGame();
        } else if (keycode == Input.Keys.V) {
            won = true;
            victoryOverlay.show();
        } else if (keycode == Input.Keys.M) {
            addMoney(10);
        } else if (keycode == Input.Keys.X) {
            damageHp(START_HP);
        }
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
        if (paused) return false; // keine Tower platzieren während pausiert ist
        if (previewTower == null) return false;

        if (previewTower.getCost() > money) {
            setSelectedTower(0);
            return false;
        }

        previewTower.setPos(viewport.unproject(new Vector2(screenX, screenY)));
        return false;
    }

    ///  Setzt den aktuell ausgewählten Preview Tower
    ///
    /// @param type: 0 => Kein Tower
    ///                                                                               1 => Fish Tower
    ///
    public void setSelectedTower(int type) {
        if (paused) return; // keine Tower platzieren während pausiert ist
        if (type == 0) {
            previewTower = null;
            return;
        }

        switch (type) {
            case 1:
                previewTower = new FishTower(this, new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE), createTowerId()).preview();  // für außerhalb vom Feld → nicht sichtbar bis Maus Bewegt
                break;
            case 2:
                previewTower = new SnowballTower(this, new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE), createTowerId()).preview();
                break;
            case 3:
                previewTower = new SniperTower(this, new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE), createTowerId()).preview();
                break;
            case 4:
                previewTower = new BeaconTower(this, new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE), createTowerId()).preview();
                break;
        }
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    /// @return map height in tiles
    public int getMapHeight() {
        return mapHeight;
    }

    /// @return map width in tiles
    public int getMapWidth() {
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
        value.addChild("next_tower_id", new JsonValue(nextTowerId));
        value.addChild("money", new JsonValue(money));
        value.addChild("hp", new JsonValue(hp));
        value.addChild("score", new JsonValue(score));
        // gegner
        JsonValue jsonEnemies = new JsonValue(JsonValue.ValueType.array);
        enemies.forEach(e -> jsonEnemies.addChild(e.toJson()));
        value.addChild("enemies", jsonEnemies);
        // türme
        JsonValue jsonTowers = new JsonValue(JsonValue.ValueType.array);
        towers.forEach(t -> jsonTowers.addChild(t.toJson()));
        value.addChild("towers", jsonTowers);
        // projektile
        JsonValue jsonProjectiles = new JsonValue(JsonValue.ValueType.array);
        projectiles.forEach(p -> jsonProjectiles.addChild(p.toJson()));
        value.addChild("projectiles", jsonProjectiles);
        // game stats
        value.addChild("stats", stats.toJson());
        return value;
    }

    /// @throws IllegalArgumentException wenn ungültige enemy types in der json sind
    /// @throws IllegalStateException    wenn fromJson false ist
    @Override
    public void fromJson(@NotNull JsonValue json) {
        if (!fromJson) {
            throw new IllegalStateException("fromJson ist false, aber fromJson() wurde aufgerufen");
        }

        PenguTD.getInstance().getStatsManager().addGamePlayed();
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
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        createTowerSelection();  // neuer Viewport desewegen neue TowerSelection

        nextEntityId = json.getInt("next_entity_id");
        nextTowerId = json.getInt("next_tower_id");
        money = json.getInt("money");
        hp = json.getInt("hp");
        score = json.getInt("score");
        // enemies
        JsonValue jsonEnemies = json.get("enemies");
        enemies.forEach(Disposable::dispose);
        enemies.clear();
        for (JsonValue jsonEnemy : jsonEnemies) {
            Enemy enemy;
            String enemyType = jsonEnemy.getString("type");
            switch (enemyType) {
                case WarriorEnemy.JSON_TYPE:
                    enemy = new WarriorEnemy(0, this, jsonEnemy.getInt("id"));
                    break;
                case FatEnemy.JSON_TYPE:
                    enemy = new FatEnemy(this, jsonEnemy.getInt("id"));
                    break;
                case BushEnemy.JSON_TYPE:
                    enemy = new BushEnemy(this, jsonEnemy.getInt("id"));
                    break;
                case CoolEnemy.JSON_TYPE:
                    enemy = new CoolEnemy(this, jsonEnemy.getInt("id"));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
            }
            enemy.fromJson(jsonEnemy);
            addEnemy(enemy);
        }

        // towers
        JsonValue jsonTowers = json.get("towers");
        towers.forEach(Disposable::dispose);
        towers.clear();
        for (JsonValue jsonTower : jsonTowers) {
            Tower tower;
            String towerType = jsonTower.getString("type");
            switch (towerType) {
                case FishTower.JSON_TYPE:
                    tower = new FishTower(this, new Vector2(), jsonTower.getInt("id"));
                    break;
                case SnowballTower.JSON_TYPE:
                    tower = new SnowballTower(this, new Vector2(), jsonTower.getInt("id"));
                    break;
                case SniperTower.JSON_TYPE:
                    tower = new SniperTower(this, new Vector2(), jsonTower.getInt("id"));
                    break;
                case BeaconTower.JSON_TYPE:
                    tower = new BeaconTower(this, new Vector2(), jsonTower.getInt("id"));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown tower type: " + towerType);
            }
            tower.fromJson(jsonTower);
            addTower(tower);
        }

        // projectiles
        JsonValue jsonProjectiles = json.get("projectiles");
        projectiles.forEach(Disposable::dispose);
        projectiles.clear();
        for (JsonValue jsonProjectile : jsonProjectiles) {
            Projectile projectile;
            String projectileType = jsonProjectile.getString("type");
            switch (projectileType) {
                case FishProjectile.JSON_TYPE:
                    //noinspection DataFlowIssue
                    projectile = new FishProjectile(this, new Vector2(), new Vector2(), null, 0);  // tower als null ist ok, da dieser sowieso in fromJson updated wird
                    break;
                case SnowballProjectile.JSON_TYPE:
                    //noinspection DataFlowIssue
                    projectile = new SnowballProjectile(this, new Vector2(), new Vector2(), null, 0, 0f);  // tower als null ist ok, da dieser sowieso in fromJson updated wird
                    break;
                case IceProjectile.JSON_TYPE:
                    //noinspection DataFlowIssue
                    projectile = new IceProjectile(this, new Vector2(), new Vector2(), null, 0);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown projectile type: " + projectileType);
            }
            projectile.fromJson(jsonProjectile);
            addProjectile(projectile);
        }

        stats.fromJson(json.get("stats"));
    }

    private void createTowerSelection() {
        if (towerSelection != null) {
            towerSelection.dispose();
        }

        towerSelection = new TowerSelection(this);

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(towerSelection.getStage());
        multiplexer.addProcessor(this);

        Gdx.input.setInputProcessor(multiplexer);
    }

    public int createEntityId() {
        return nextEntityId++;
    }

    public int createTowerId() {
        return nextTowerId++;
    }

    /// @return ein Enemy mit der gegebenen ID oder null, wenn es keinen gibt
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

    public @Nullable Tower getTowerFromId(int id) {
        for (int i = 0; i < towers.size; i++) {
            Tower tower = towers.get(i);
            if (tower.getId() == id) {
                return tower;
            }
        }
        return null;
    }

    public void saveGame() {
        // todo richtige datei speichern
        JsonValue json = toJson();
        FileHandle handle = Gdx.files.local("saves/test.json");
        handle.writeString(json.prettyPrint(JsonWriter.OutputType.json, 1), false);
    }

    /// @return ob der tower an der Stelle in der Welt platziert werden kann
    /// pos könnte entfernt werden, und tower.pos verwendet werden
    public boolean canPlaceTower(@NotNull Vector2 pos, @NotNull Tower tower) {
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

                Rectangle objectBounds = new Rectangle(textureObject.getX(), textureObject.getY(), width, height);

                if (objectBounds.overlaps(towerHitbox)) {
                    return false;
                }
            }
        }

        return true;
    }

    /// fügt ein Projektil in die Welt hinzu.
    public void addProjectile(@NotNull Projectile projectile) {
        projectiles.add(projectile);
        if (projectile instanceof SpeedModifier) {  // auch projectile für später FeuerProjectile
            speedModifiers.add((SpeedModifier) projectile);
        }
    }

    /// Fügt ein Gegner in die Welt hinzu
    public void addEnemy(@NotNull Enemy enemy) {
        enemies.add(enemy);
        if (enemy instanceof SpeedModifier) {
            speedModifiers.add(((SpeedModifier) enemy));
        }
    }

    public void addTower(@NotNull Tower tower) {
        towers.add(tower);
        if (tower instanceof SpeedModifier) {
            speedModifiers.add(((SpeedModifier) tower));
        }
    }

    public int getMoney() {
        return money;
    }

    public int getHp() {
        return hp;
    }

    public void addMoney(int amount) {
        money += amount;
        PenguTD.getInstance().getStatsManager().addMoneyEarned(amount);
        towerSelection.updateTopElement();
    }

    public boolean spendMoney(int amount) {
        if (money < amount) return false;

        money -= amount;
        towerSelection.updateTopElement();
        return true;
    }

    public void damageHp(int amount) {
        hp -= amount;

        if (hp <= 0) {
            hp = 0;
            if (!defeatOverlay.isShown()) {
                defeatOverlay.show();
            }
        }
        towerSelection.updateTopElement();
    }

    /// @return input processor von World & TowerSelection
    /// muss genutzt werden, wenn der inputHandler von World verwendet wird
    public InputMultiplexer getInputProcessor() {
        return multiplexer;
    }

    ///  Welt schließen und zurück zum Startbildschirm kommen
    public void close() {
        stats.close();
        StartScreen newScreen = new StartScreen();
        newScreen.setFirstOpenAnimation(false);
        PenguTD.getInstance().setScreenAndDispose(newScreen);
    }
}
