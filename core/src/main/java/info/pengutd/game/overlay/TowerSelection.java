package info.pengutd.game.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import info.pengutd.game.tower.FishTower;
import info.pengutd.game.tower.SniperTower;
import info.pengutd.game.tower.SnowballTower;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class TowerSelection implements Disposable {
    public static final float SIDEBAR_WIDTH = 130f;
    private final Stage uiStage;
    private final TextureAtlas atlas;
    private final TextureAtlas towerAtlas;
    private final World world;
    private Label moneyLabel;
    private Label hpLabel;
    @SuppressWarnings("StaticCollection")
    private static final HashMap<Integer, String> towerNames = new HashMap<>();
    {
        towerNames.put(1, FishTower.JSON_TYPE);
        towerNames.put(2, SnowballTower.JSON_TYPE);
        towerNames.put(3, SniperTower.JSON_TYPE);
        towerNames.put(4, "fire_tower");
        towerNames.put(5, "machine_gun_tower");
        towerNames.put(6, "mafia_tower");
    }

    public TowerSelection(World world) {
        this.world = world;
        // damit die TowerSelection die gleiche aspect ratio wie die world hat (sonst seltsames verschieben bei fenster scaling)
        float aspectRatio = world.getViewport().getWorldWidth() / world.getViewport().getWorldHeight();
        float worldWidth = 800f;
        float worldHeight = worldWidth / aspectRatio;

        uiStage = new Stage(new FitViewport(worldWidth, worldHeight));
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        atlas = PenguTD.getInstance().getAssetManager().get(Assets.TOWER_SELECTION_ATLAS);
        towerAtlas = PenguTD.getInstance().getAssetManager().get(Assets.TOWER_ATLAS);

        // sidebar table
        Table sidebar = new Table();
        sidebar.top();

        sidebar.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "background")).tint(new Color(1, 1, 1, 0.6f)));

        sidebar.add(topElement()).colspan(2).size(110).padTop(10).padBottom(20).row();

        addTowerRow(sidebar, 1, 2);
        addTowerRow(sidebar, 3, 4);
        addTowerRow(sidebar, 5, 6);

        sidebar.add(pauseButton()).width(110).height(50).colspan(2).padTop(8).row();

        Vector2 worldMapBottomRight = new Vector2(world.getViewport().getWorldWidth(), 0);
        Vector2 screenMapBottomRight = world.getViewport().project(worldMapBottomRight); // world => screen
        Vector2 uiMapBottomRight = uiStage.getViewport().unproject(screenMapBottomRight); // screen => ui

        sidebar.setSize(SIDEBAR_WIDTH, worldHeight);
        sidebar.setPosition(uiMapBottomRight.x - SIDEBAR_WIDTH, 0f);

        uiStage.addActor(sidebar);
    }

    private void addTowerRow(Table sidebar, int left, int right) {
        sidebar.add(towerElement(left))
            .padLeft(10).padRight(10).padBottom(10)
            .size(50);

        sidebar.add(towerElement(right))
            .padRight(10).padBottom(10)
            .size(50)
            .row();
    }

    public Stage getStage() {
        return uiStage;
    }

    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    private @NotNull Stack towerElement(int i) {
        Stack stack = new Stack();
        Image buttonBackground = new Image(Assets.findRegionOrMissing(atlas, "button_blue"));
        buttonBackground.getColor().a = 0.9f;
        buttonBackground.setScaling(Scaling.stretch);
        stack.add(buttonBackground);

        // Table, weil stack alle inhalte immer auf volle größe erweitert
        Table content = new Table().center();
        Image image = new Image(Assets.findRegionOrMissing(towerAtlas, towerNames.get(i) + "_idle"));
        content.add(image).width(32).height(32 * image.getHeight() / image.getWidth());  // richtige aspect ratio
        stack.add(content);

        content.setTouchable(Touchable.disabled);
        image.setTouchable(Touchable.disabled);

        buttonBackground.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                world.setSelectedTower(i);
            }
        });

        return stack;
    }

    private @NotNull Actor pauseButton() {
        Stack stack = new Stack();
        Image buttonBackground = new Image(Assets.findRegionOrMissing(atlas, "button_blue"));
        buttonBackground.getColor().a = 0.9f;
        buttonBackground.setScaling(Scaling.stretch);
        stack.add(buttonBackground);

        Table content = new Table().center();
        Label textLabel = new Label("Pause", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        content.add(textLabel);
        stack.add(content);

        stack.setTouchable(Touchable.enabled);
        stack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                world.setPaused(true);
            }
        });

        return stack;
    }

    /// das blaue rechteck oben, in dem Geld und Hp angezeigt werden
    private @NotNull Stack topElement() {
        Stack stack = new Stack();
        Image topBackground = new Image(Assets.findRegionOrMissing(atlas, "button_blue"));
        topBackground.getColor().a = 0.9f;
        topBackground.setScaling(Scaling.stretch);

        Table topContent = new Table();
        topContent.add(new Image(Assets.findRegionOrMissing(atlas, "money"))).size(28).pad(4);
        moneyLabel = new Label("" + world.getMoney(), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        topContent.add(moneyLabel).row();

        topContent.add(new Image(Assets.findRegionOrMissing(atlas, "hp"))).size(28).pad(4);
        hpLabel = new Label("" + world.getHp(), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        topContent.add(hpLabel).row();

        stack.add(topBackground);
        stack.add(topContent);
        return stack;
    }

    /// Updated den Text von den HP und dem Geld auf aktuelle Werte
    public void updateTopElement() {
        moneyLabel.setText("" + world.getMoney());
        hpLabel.setText("" + world.getHp());
    }

    ///  Zeichnet die TowerSelection
    public void render(float delta) {
        uiStage.getViewport().apply();
        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void dispose() {
        uiStage.dispose();
    }
}
