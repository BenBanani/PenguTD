package info.pengutd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;

import java.util.Arrays;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class StartScreen implements Screen {
    private static final float ANIM_TIME = 0.5f;
    private final ImageButton[] buttons = new ImageButton[6];
    private Stage stage;
    private TextureAtlas atlas;
    private Texture backgroundTexture;
    private Image title;
    private boolean firstOpenAnimation = true;
    // 0: new game
    // 1: load game
    // 2: settings
    // 3: account
    // 4: stats
    // 5: exit

    private static void animateButtonExit(ImageButton btn, float direction) {
        int moveDistance = 475;
        btn.addAction(moveBy(direction * moveDistance, 0, ANIM_TIME, Interpolation.smoother));
    }

    public void setFirstOpenAnimation(boolean firstOpenAnimation) {
        this.firstOpenAnimation = firstOpenAnimation;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(800, 480));
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = PenguTD.getInstance().getAssetManager().get(Assets.UI_BACKGROUND);
        atlas = PenguTD.getInstance().getAssetManager().get(Assets.START_SCREEN_ATLAS);

        buildUi();
        addAnimations();
        addListeners();
    }

    private void addListeners() {
        for (ImageButton button : buttons) {
            button.setTransform(true);
            button.setOrigin(Align.center);
            button.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    button.addAction(scaleTo(1.08f, 1.08f, 0.15f));
                    super.enter(event, x, y, pointer, fromActor);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    button.addAction(scaleTo(1, 1, 0.15f));
                    super.exit(event, x, y, pointer, toActor);
                }
            });
        }

        buttons[0].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animateClose();
                buttons[0].addAction(sequence(delay(ANIM_TIME), run(() -> startNewGame())));
            }
        });

        buttons[1].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animateClose();
                buttons[1].addAction(sequence(delay(ANIM_TIME), run(() -> loadGame())));
            }
        });

        buttons[2].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animateClose();
                buttons[2].addAction(sequence(delay(ANIM_TIME), run(() -> openSettings())));
            }
        });

        buttons[3].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animateClose();
                buttons[3].addAction(sequence(delay(ANIM_TIME), run(() -> openAccountSelection())));
            }
        });

        buttons[4].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animateClose();
                buttons[4].addAction(sequence(delay(ANIM_TIME), run(() -> openStats())));
            }
        });
        buttons[5].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animateClose();
                stage.addAction(fadeOut(ANIM_TIME, Interpolation.smoother));
                buttons[5].addAction(sequence(delay(ANIM_TIME, run(() -> exitGame()))));
            }
        });
    }

    private void addAnimations() {
        //  Animation: Title
        if (firstOpenAnimation) {
            title.addAction(sequence(moveBy(0, -100),
                parallel(run(PenguTD.getInstance().getAssetManager()::finishLoading), delay(ANIM_TIME)), // warte mindestens 0.5s maximal so lange bis alle texturen geladen sind
                // problem: wenn texturen laden länger dauert als 0.5s bewegen sich die buttons zu früh
                moveBy(0, 100, 1f, Interpolation.smooth), run(() -> firstOpenAnimation = false)));
        } else {
            title.addAction(sequence(moveBy(0, 200), moveBy(0, -200, ANIM_TIME, Interpolation.smooth)));
        }

        animateButton(buttons[0], -1);
        animateButton(buttons[1], -1);
        animateButton(buttons[2], 1);
        animateButton(buttons[3], -1);
        animateButton(buttons[4], 1);
        animateButton(buttons[5], 1);
    }

    private void animateClose() {
        stage.getRoot().setTouchable(Touchable.disabled);

        title.addAction(moveBy(0, 200f, ANIM_TIME, Interpolation.smoother));

        animateButtonExit(buttons[0], -1);
        animateButtonExit(buttons[1], 1);
        animateButtonExit(buttons[2], 1);
        animateButtonExit(buttons[3], -1);
        animateButtonExit(buttons[4], -1);
        animateButtonExit(buttons[5], 1);
    }

    private void animateButton(ImageButton btn, float direction) {
        int moveDistance = 475;

        btn.addAction(sequence(moveBy(direction * moveDistance, 0), delay(firstOpenAnimation ? 1.5f : 0f), moveBy(-direction * moveDistance, 0, ANIM_TIME, Interpolation.smoother)));
    }

    private void buildUi() {
        Image background = new Image(backgroundTexture);
        background.setScaling(Scaling.fill);
        background.setFillParent(true);

        title = new Image(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "title")));

        buttons[0] = new ImageButton(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "new_game_button")));
        buttons[1] = new ImageButton(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "load_game_button")));
        buttons[2] = new ImageButton(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "settings_button")));
        buttons[3] = new ImageButton(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "account_button")));
        buttons[4] = new ImageButton(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "stats_button")));
        buttons[5] = new ImageButton(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "exit_button")));

        Arrays.stream(buttons).forEach((btn) -> btn.getImage().setScaling(Scaling.fit));

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.defaults().size(150, 100).pad(5);

        table.add(title).size(450, 200).colspan(3).center().padBottom(-10).padTop(-50).row();

        table.add(buttons[0]);
        table.add(buttons[1]);
        table.add(buttons[2]).row();

        table.add(buttons[3]);
        table.add(buttons[4]);
        table.add(buttons[5]);

        stage.addActor(background);
        stage.addActor(table);
    }

    private void startNewGame() {
        if (PenguTD.getInstance().getProfileManager().getCurrentProfile() == null) {
            openAccountSelection();
            return;
        }
        //PenguTD.getInstance().setScreenAndDispose(new World());
        PenguTD.getInstance().setScreenAndDispose(new LevelSelectionScreen(this));
    }

    private void loadGame() {
        if (PenguTD.getInstance().getProfileManager().getCurrentProfile() == null) {
            openAccountSelection();
            return;
        }
        PenguTD.getInstance().setScreenAndDispose(new SaveSelectionScreen(this));
    }

    private void openSettings() {
        PenguTD.getInstance().setScreen(new SettingsScreen(PenguTD.getInstance().getScreen()));
    }

    private void openAccountSelection() {
        PenguTD.getInstance().setScreen(new AccountScreen(PenguTD.getInstance().getScreen()));
    }

    private void openStats() {
        PenguTD.getInstance().setScreen(new StatsScreen(PenguTD.getInstance().getScreen()));
    }

    private void exitGame() {
        Gdx.app.exit();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

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
