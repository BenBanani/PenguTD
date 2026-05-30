package info.pengutd.game.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class VictoryOverlay {
    public static final int DIALOG_PADDING = 50;
    private final @NotNull World world;
    private final @NotNull Stage uiStage;
    private final @NotNull Table content;
    private final @NotNull Image title;
    private final @NotNull Image mainMenuButton;
    private final @NotNull Table stats;
    private final @NotNull Image pengus;

    public VictoryOverlay(@NotNull World world) {
        this.world = world;

        uiStage = new Stage(new FitViewport(800f, 480f));
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.VICTORY_SCREEN_ATLAS);
        Skin skin = PenguTD.getInstance().getAssetManager().get(Assets.DEFAULT_SKIN);

        Table background = new Table();
        background.setFillParent(true);
        background.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.TOWER_SELECTION_ATLAS), "background")).tint(new Color(1, 1, 1, 0.6f)));
        uiStage.addActor(background);

        content = new Table();
        content.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "background_banner")));

        content.setSize(450, 350);
        content.setPosition((800 - 450) / 2f - DIALOG_PADDING, (480 - 350) / 2f);
        uiStage.addActor(content);

        title = new Image(Assets.findRegionOrMissing(atlas, "title"));
        title.setSize(450, 150);
        title.setPosition((800 - 450) / 2f - DIALOG_PADDING, 275);
        uiStage.addActor(title);

        pengus = new Image(Assets.findRegionOrMissing(atlas, "pengus"));
        pengus.setSize(400, 100);
        pengus.setPosition((800 - 400) / 2f - DIALOG_PADDING, 75);
        pengus.setTouchable(Touchable.disabled);
        uiStage.addActor(pengus);

        stats = new Table();
        assert PenguTD.getInstance().getStatsManager().getGameStats() != null;
        Map<String, String> statsMap = PenguTD.getInstance().getStatsManager().getGameStats().getStatsAsPrintMap();
        statsMap.forEach((k, v) -> {
            Label label = new Label(k + ": " + v, skin);
            label.setAlignment(Align.left);
            stats.add(label).row();
        });
        content.add(stats).row();

        mainMenuButton = new Image(Assets.findRegionOrMissing(atlas, "main_menu_button"));
        content.add(mainMenuButton).size(200, 60);

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.getRoot().setTouchable(Touchable.disabled);
                close();
            }
        });
    }

    public void render(float delta) {
        uiStage.act(delta);
        uiStage.getViewport().apply();
        uiStage.draw();
    }

    private void close() {
        hide();
        world.close();
    }

    /// muss aufgerufen werden bevor das DefeatOverlay sichtbar wird
    public void show() {
        world.getInputProcessor().addProcessor(0, uiStage);

        // animate open
        content.addAction(sequence(
            moveBy(0, -500),
            moveBy(0, 500, 0.5f, Interpolation.smoother)
        ));

        title.addAction(sequence(
            moveBy(-800, 0),
            moveBy(800, 0, 0.5f, Interpolation.smoother)
        ));

        mainMenuButton.addAction(sequence(
            moveBy(600, 500),
            moveBy(-600, -500, 0.5f, Interpolation.smoother)
        ));

        pengus.addAction(sequence(moveBy(-800, 0), moveBy(800, 0, 0.5f, Interpolation.smoother)));


        uiStage.addAction(sequence(alpha(0), fadeIn(0.5f)));
    }


    /// Hide sollte aufgerufen werden, wenn das PauseOverlay geschlossen wird.
    public void hide() {
        world.getInputProcessor().removeProcessor(uiStage);

        content.addAction(sequence(
            moveBy(0, 500, 0.5f, Interpolation.smoother),
            moveBy(0, -500)
        ));

        title.addAction(sequence(
            moveBy(800, 0, 0.5f, Interpolation.smoother),
            moveBy(-800, 0)
        ));

        mainMenuButton.addAction(sequence(
            moveBy(-600, -500, 0.5f, Interpolation.smoother),
            moveBy(600, 500)
        ));

        pengus.addAction(sequence(
            moveBy(800, 0, 0.5f, Interpolation.smoother),
            moveBy(-800, 0)
        ));


        uiStage.addAction(fadeOut(0.5f));
    }

    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

}
