package info.pengutd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import info.pengutd.stats.GameStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/// Screen zum Auswählen des gespeicherten Spielstandes
public class SaveSelectionScreen implements Screen {
    private final @Nullable Screen previousScreen;
    private final Array<Table> levelButtons = new Array<>();
    private Skin skin;
    private Stage stage;
    private TextureAtlas atlas;
    private Texture bgTexture;
    private Image title;
    private ImageButton backButton;

    public SaveSelectionScreen(@Nullable Screen previousScreen) {
        this.previousScreen = previousScreen;
    }

    private static void openSavedWorld(int mapNumber) {
        assert PenguTD.getInstance().getProfileManager().getCurrentProfile() != null;
        FileHandle saveFile = Gdx.files.local("saves/" + PenguTD.getInstance().getProfileManager().getCurrentProfile().getName() + "/map" + mapNumber + ".json");
        assert saveFile.exists();

        World world = new World(true);
        JsonValue jsonWorld = new JsonReader().parse(saveFile);
        world.fromJson(jsonWorld);

        PenguTD.getInstance().setScreenAndDispose(world);
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(800, 480));
        Gdx.input.setInputProcessor(stage);

        loadAssets();
        buildUI();
    }

    private void loadAssets() {
        AssetManager assetManager = PenguTD.getInstance().getAssetManager();
        skin = assetManager.get(Assets.DEFAULT_SKIN);

        bgTexture = assetManager.get(Assets.UI_BACKGROUND);
        atlas = assetManager.get(Assets.SELECTION_SCREEN_ATLAS);
    }

    private void buildUI() {
        Image background = new Image(bgTexture);
        background.setFillParent(true);
        background.setScaling(Scaling.fill);
        stage.addActor(background);

        Table root = new Table();
        root.setFillParent(true);
        root.top().pad(20);

        title = new Image(Assets.findRegionOrMissing(atlas, "title"));
        root.add(title).width(300).height(100).pad(15).row();

        title.addAction(sequence(moveBy(0, 150), moveBy(0, -150, 0.5f, Interpolation.smoother)));

        for (int mapNumber = 1; mapNumber <= 3; mapNumber++) {
            //noinspection GDXJavaFlushInsideLoop (man muss flushen, da wir ja die map in den frameBuffer vom Image zeichnen müssen)
            Table levelButton = createMapSelectionButton(mapNumber);
            levelButtons.add(levelButton);
            int moveDistance = mapNumber % 2 == 0 ? 600 : -600;
            levelButton.addAction(sequence(moveBy(moveDistance, 0), moveBy(-moveDistance, 0, 0.5f, Interpolation.smoother)));
            root.add(levelButton).row();
        }

        stage.addActor(root);

        backButton = createBackButton();
        stage.addActor(backButton);
    }

    private @NotNull Table createMapSelectionButton(int mapNumber) {
        Table table = new Table();
        Image image = new Image(createSaveScreenshot(mapNumber));
        image.setScaling(Scaling.stretch);
        table.add(image).size(100, 90).center().padRight(50).padBottom(10);
        String text;
        switch (mapNumber) {
            case 1:
                text = "easy";
                break;
            case 2:
                text = "medium";
                break;
            case 3:
                text = "hard";
                break;
            default:
                throw new IllegalStateException("Unexpected map number: " + mapNumber);
        }
        table.add(new Label("Difficulty: " + text, skin)).size(200, 100).center();

        table.setTouchable(Touchable.enabled);
        assert PenguTD.getInstance().getProfileManager().getCurrentProfile() != null;
        FileHandle saveFile = getSavefile(mapNumber);
        if (saveFile.exists()) {
            table.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    animateClose();
                    stage.addAction(fadeOut(0.5f, Interpolation.smoother));
                    table.addAction(sequence(delay(0.5f), run(() -> openSavedWorld(mapNumber))));
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    image.addAction(scaleTo(1.05f, 1.05f, 0.1f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    image.addAction(scaleTo(1f, 1f, 0.1f));
                }
            });
        }

        return table;
    }

    private static FileHandle getSavefile(int mapNumber) {
        return Gdx.files.local("saves/" + PenguTD.getInstance().getProfileManager().getCurrentProfile().getName() + "/map" + mapNumber + ".json");
    }

    private void animateClose() {
        stage.getRoot().setTouchable(Touchable.disabled);

        for (int i = 0; i < levelButtons.size; i++) {
            Table levelButton = levelButtons.get(i);
            int moveDistance = i % 2 == 0 ? 600 : -600;
            levelButton.addAction(moveBy(moveDistance, 0, 0.5f, Interpolation.smoother));
        }

        title.addAction(moveBy(0, 150, 0.5f, Interpolation.smoother));
        backButton.addAction(moveBy(0, 100, 0.5f, Interpolation.smoother));
    }

    private @NotNull TextureRegion createSaveScreenshot(int mapNumber) {
        /// save file finden
        assert PenguTD.getInstance().getProfileManager().getCurrentProfile() != null;
        FileHandle saveFile = Gdx.files.local("saves/" + PenguTD.getInstance().getProfileManager().getCurrentProfile().getName() + "/map" + mapNumber + ".json");
        if (!saveFile.exists()) {
            return new TextureRegion(Assets.findRegionOrMissing(atlas, "no_save"));
        }

        GameStats oldStats = PenguTD.getInstance().getStatsManager().getGameStats();  // new World resettet die Game Stats
        /// World von save file konstruieren
        World world = new World(true);
        JsonValue jsonWorld = new JsonReader().parse(saveFile);
        world.fromJson(jsonWorld);
        world.show();
        Gdx.input.setInputProcessor(stage);  // world setzt den InputProcessor neu

        Viewport viewport = world.getViewport();
        viewport.apply(false);
        FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGBA8888, viewport.getScreenWidth(), viewport.getScreenHeight(), false); // frame buffer in den die World zeichnen kann

        buffer.begin();
        Gdx.gl.glViewport(0, 0, buffer.getWidth(), buffer.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int screenX = viewport.getScreenX();
        int screenY = viewport.getScreenY();
        viewport.setScreenPosition(0, 0); // screen bewegen damit es keine schwarzen Balken am Rand gibt

        world.render(0);

        buffer.end();
        viewport.setScreenPosition(screenX, screenY); // alte viewport position wiederherstellen

        Texture texture = buffer.getColorBufferTexture();
        TextureRegion region = new TextureRegion(texture);
        region.flip(false, true);

        stage.getViewport().apply(true);

        PenguTD.getInstance().getStatsManager().setGameStats(oldStats);

        return region;
    }

    private @NotNull ImageButton createBackButton() {
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "back_button")));
        backButton.setSize(50, 50);
        backButton.setPosition(25, stage.getHeight() - 75);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animateClose();

                backButton.addAction(sequence(delay(0.5f), run(() -> PenguTD.getInstance().setScreenAndDispose(previousScreen))));
            }
        });

        backButton.addAction(sequence(moveBy(0, 100), moveBy(0, -100, 0.5f, Interpolation.smoother)));
        return backButton;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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
