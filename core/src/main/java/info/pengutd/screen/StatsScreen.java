package info.pengutd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.stats.StatsManager;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class StatsScreen implements Screen {
    private final Screen previousScreen;
    private Skin skin;
    private TextureAtlas atlas;
    private Stage stage;

    private Texture bgTexture;
    private Image title;
    private Table lastGameStats;
    private Table profileStats;
    private Table globalStats;
    private ImageButton backButton;

    public StatsScreen(Screen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(800, 480));
        skin = PenguTD.getInstance().getAssetManager().get(Assets.DEFAULT_SKIN);
        bgTexture = PenguTD.getInstance().getAssetManager().get(Assets.UI_BACKGROUND);
        atlas = PenguTD.getInstance().getAssetManager().get(Assets.STATS_SCREEN_ATLAS);
        Gdx.input.setInputProcessor(stage);

        Image background = new Image(bgTexture);
        background.setFillParent(true);
        background.setScaling(Scaling.fill);
        stage.addActor(background);

        Table root = new Table();
        root.setFillParent(true);
        root.center().pad(20f);

        title = new Image(Assets.findRegionOrMissing(atlas, "title"));
        root.add(title).width(400).height(150).colspan(3).padBottom(15).row();

        backButton = new ImageButton(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "back_button")));
        backButton.setPosition(25, stage.getHeight() - 75);
        backButton.setSize(50, 50);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animateClose();
                backButton.addAction(sequence(delay(0.5f), run(() -> PenguTD.getInstance().setScreenAndDispose(previousScreen))));
            }
        });
        backButton.setTouchable(Touchable.enabled);

        stage.addActor(backButton);

        Drawable tableBg = new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "table_background"));

        lastGameStats = new Table();
        lastGameStats.setBackground(tableBg);
        lastGameStats.top().padTop(10);
        profileStats = new Table();
        profileStats.setBackground(tableBg);
        profileStats.top().padTop(10);
        globalStats = new Table();
        globalStats.setBackground(tableBg);
        globalStats.top().padTop(10);
        fillTables();

        int tableHeight = 220;
        int tableWidth = 200;

        root.add(lastGameStats).width(tableWidth).height(tableHeight).pad(10);
        root.add(profileStats).width(tableWidth).height(tableHeight).pad(10);
        root.add(globalStats).width(tableWidth).height(tableHeight).pad(10);
        stage.addActor(root);

        addAnimations();
    }

    private void animateClose() {
        title.addAction(moveBy(0, 200, 0.5f, Interpolation.smoother));
        backButton.addAction(moveBy(-75, 0, 0.5f, Interpolation.smoother));

        lastGameStats.addAction(moveBy(0, -400, 0.5f, Interpolation.smoother));
        profileStats.addAction(moveBy(0, -400, 0.5f, Interpolation.smoother));
        globalStats.addAction(moveBy(0, -400, 0.5f, Interpolation.smoother));
    }

    private void addAnimations() {
        title.addAction(sequence(moveBy(0, 200), moveBy(0, -200, 0.5f, Interpolation.smoother)));
        backButton.addAction(sequence(moveBy(-75, 0), moveBy(75, 0, 0.5f, Interpolation.smoother)));

        lastGameStats.addAction(sequence(moveBy(0, -400), moveBy(0, 400, 0.5f, Interpolation.smoother)));
        profileStats.addAction(sequence(moveBy(0, -400), moveBy(0, 400, 0.5f, Interpolation.smoother)));
        globalStats.addAction(sequence(moveBy(0, -400), moveBy(0, 400, 0.5f, Interpolation.smoother)));
    }

    private void fillTables() {
        Label label1 = new Label("Last Game:", skin);
        label1.setFontScale(1.3f);
        lastGameStats.add(label1).center().row();

        StatsManager manager = PenguTD.getInstance().getStatsManager();
        if (manager.getGameStats() == null) {
            lastGameStats.add(new Label("No stats from last game", skin)).left().row();
        } else {
            manager.getGameStats().getStatsAsPrintMap().forEach((stat, value) -> {
                lastGameStats.add(new Label(stat + ": " + value, skin)).left().row();
            });
        }


        Label label2 = new Label("Profile:", skin);
        label2.setFontScale(1.3f);
        profileStats.add(label2).center().row();

        if (manager.getProfileStats() == null) {
            profileStats.add(new Label("No profile selected", skin)).left().row();
        } else {
            manager.getProfileStats().getStatsAsPrintMap().forEach((stat, value) -> {
                profileStats.add(new Label(stat + ": " + value, skin)).left().row();
            });
        }

        Label label3 = new Label("Global:", skin);
        label3.setFontScale(1.3f);
        globalStats.add(label3).center().row();

        manager.getGlobalStats().getStatsAsPrintMap().forEach((stat, value) -> {
            globalStats.add(new Label(stat + ": " + value, skin)).left().row();
        });
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
