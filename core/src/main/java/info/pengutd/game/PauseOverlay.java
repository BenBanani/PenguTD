package info.pengutd.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class PauseOverlay implements Disposable {
    public static final int DIALOG_PADDING = 50;
    private final @NotNull World world;
    private final @NotNull Stage uiStage;
    private final @NotNull Table content;
    private final @NotNull Image title;
    private final @NotNull Image resumeButton;
    private final @NotNull Image settingsButton;
    private final @NotNull Image mainMenuButton;
    private @Nullable Table saveDialog;
    private boolean visible = false;

    public PauseOverlay(@NotNull World world) {
        this.world = world;

        uiStage = new Stage(new FitViewport(800f, 480f));
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.PAUSE_SCREEN_ATLAS);

        Table background = new Table();
        background.setFillParent(true);
        background.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.TOWER_SELECTION_ATLAS), "background")).tint(new Color(1, 1, 1, 0.6f)));
        uiStage.addActor(background);

        content = new Table();
        content.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "background_banner")));

        content.setSize(300, 350);
        content.setPosition((800 - 300 - DIALOG_PADDING) / 2f, (480 - 350) / 2f); // 50 nach links → nicht ganz mitte aber sieht gut aus wo es ist
        uiStage.addActor(content);

        title = new Image(Assets.findRegionOrMissing(atlas, "title_banner"));
        title.setSize(400, 100);
        title.setPosition((800 - 400 - DIALOG_PADDING) / 2f, 350);
        uiStage.addActor(title);

        resumeButton = new Image(Assets.findRegionOrMissing(atlas, "resume_button"));
        content.add(resumeButton).size(200, 60).pad(10).row();
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });

        settingsButton = new Image(Assets.findRegionOrMissing(atlas, "settings_button"));
        content.add(settingsButton).size(200, 60).pad(10).row();
        // todo mini settings

        mainMenuButton = new Image(Assets.findRegionOrMissing(atlas, "main_menu_button"));
        content.add(mainMenuButton).size(200, 60).pad(10).row();
        // todo speichern
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveDialog = getSaveDialog();
                uiStage.addActor(saveDialog);
            }
        });


        uiStage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    close();
                    return true;
                }
                return false;
            }
        });
    }

    private void close() {
        title.addAction(sequence(moveBy(0, 150, 0.5f, Interpolation.smoother), delay(0.1f), moveBy(0, -150, 0.5f)));
        resumeButton.addAction(sequence(moveBy(-600, 500, 0.5f, Interpolation.smoother), delay(0.1f), moveBy(600, -500, 0.5f)));
        settingsButton.addAction(sequence(moveBy(600, 500, 0.5f, Interpolation.smoother), delay(0.1f), moveBy(-600, -500, 0.5f)));
        mainMenuButton.addAction(sequence(moveBy(-600, 500, 0.5f, Interpolation.smoother), delay(0.1f), moveBy(600, -500, 0.5f)));
        content.addAction(sequence(moveBy(0, -500, 0.5f, Interpolation.smoother), delay(0.1f), moveBy(0, 500)));

        if (saveDialog != null) {
            saveDialog.remove();
        }

        uiStage.addAction(sequence(fadeOut(0.5f), run(() -> world.setPaused(false))));
    }

    /// muss aufgerufen werden bevor das PauseOverlay sichtbar wird
    public void show() {
        world.getInputProcessor().addProcessor(0, uiStage);

        title.addAction(sequence(moveBy(0, 150), moveBy(0, -150, 0.5f, Interpolation.smoother)));
        resumeButton.addAction(sequence(moveBy(-600, 500), moveBy(600, -500, 0.5f, Interpolation.smoother)));
        settingsButton.addAction(sequence(moveBy(600, 500), moveBy(-600, -500, 0.5f, Interpolation.smoother)));
        mainMenuButton.addAction(sequence(moveBy(-600, 500), moveBy(600, -500, 0.5f, Interpolation.smoother)));
        content.addAction(sequence(moveBy(0, -500), moveBy(0, 500, 0.5f, Interpolation.smoother)));

        uiStage.addAction(sequence(fadeIn(0.5f)));

        visible = true;
    }

    /// Zeichnet und updated das Overlay
    /// show sollte vorher aufgerufen worden sein
    public void render(float ignoredDelta) {
        if (!visible) throw new IllegalStateException("PauseOverlay.render() called without show()");
        uiStage.getViewport().apply(true);
        uiStage.draw();
    }

    public void act(float delta) {
        uiStage.act(delta);
    }

    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    /// Hide sollte aufgerufen werden wenn das PauseOverlay geschlossen wird.
    public void hide() {
        world.getInputProcessor().removeProcessor(uiStage);
        visible = false;
    }

    @Override
    public void dispose() {
        uiStage.dispose();
    }

    private Table getSaveDialog() {
        @Nullable Table dialogOverlay = new Table();
        dialogOverlay.setFillParent(true);

        dialogOverlay.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.TOWER_SELECTION_ATLAS), "background")).tint(new Color(0, 0, 0, 0.2f)));

        @Nullable Table dialogBox = new Table();
        dialogBox.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.PAUSE_SCREEN_ATLAS), "background_banner")));

        dialogBox.setSize(250, 180);
        dialogBox.setPosition((800 - 250 - DIALOG_PADDING) / 2f, (480 - 180) / 2f);

        dialogBox.setOrigin(Align.center);
        dialogBox.setTransform(true);
        dialogBox.addAction(sequence(scaleTo(0f, 0f), scaleTo(1f, 1f, 0.5f, Interpolation.smoother)));

        dialogOverlay.addActor(dialogBox);

        // todo button textur!
        Image yesButton = new Image(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.PAUSE_SCREEN_ATLAS), "resume_button"));


        // todo button textur!
        Image noButton = new Image(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.PAUSE_SCREEN_ATLAS), "settings_button"));

        yesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                world.saveGame();
                close();
                world.close();
            }
        });

        noButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
                world.close();
            }
        });

        dialogBox.add(yesButton).size(160, 50).pad(10).row();
        dialogBox.add(noButton).size(160, 50).pad(10);

        return dialogOverlay;
    }
}
