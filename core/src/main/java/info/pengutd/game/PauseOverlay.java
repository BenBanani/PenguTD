package info.pengutd.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import org.jetbrains.annotations.NotNull;

public class PauseOverlay implements Disposable {
    private final @NotNull World world;
    private final Stage uiStage;
    private boolean visible = false;

    public PauseOverlay(@NotNull World world) {
        this.world = world;

        uiStage = new Stage(new FitViewport(800f, 480f));
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.PAUSE_SCREEN_ATLAS);

        Table background = new Table();
        background.setFillParent(true);
        background.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(
            PenguTD.getInstance().getAssetManager().get(Assets.TOWER_SELECTION_ATLAS), "background"))
            .tint(new Color(1, 1, 1, 0.6f)));
        uiStage.addActor(background);

        Table content = new Table();
        content.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "background_banner")));

        content.setSize(300, 350);
        content.setPosition((800 - 300 - 50) / 2f, (480 - 350) / 2f); // 50 nach links → nicht ganz mitte aber sieht gut aus wo es ist
        uiStage.addActor(content);

        Image title = new Image(Assets.findRegionOrMissing(atlas, "title_banner"));
        title.setSize(400, 75);
        title.setPosition((800 - 400 - 50) / 2f, 370);
        uiStage.addActor(title);

        Image resumeButton = new Image(Assets.findRegionOrMissing(atlas, "resume_button"));
        content.add(resumeButton).size(200, 60).pad(10).row();

        Image settingsButton = new Image(Assets.findRegionOrMissing(atlas, "settings_button"));
        content.add(settingsButton).size(200, 60).pad(10).row();

        Image mainMenuButton = new Image(Assets.findRegionOrMissing(atlas, "main_menu_button"));
        content.add(mainMenuButton).size(200, 60).pad(10).row();


        uiStage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    world.setPaused(false);
                    return true;
                }
                return false;
            }
        });
    }

    /// Rechnet die Koordinaten der rechten Kante des spielbaren Bereichs aus (linke kante von TowerSelection)
    private float computeUsableMapEnd() {
        float blackBar = world.getViewport().getScreenWidth() - world.getViewport().getRightGutterWidth(); // anfang schwarzer streifen rechts in screen pixeln
        return uiStage.getViewport().unproject(new Vector2(blackBar, 0)).x;
    }

    /// Rechnet die Koordinaten der linken Kante des spielbaren Bereichs aus (rechtes ende vom schwarzen Balken links)
    private float computUsableMapBegin() {
        float blackBar = world.getViewport().getLeftGutterWidth(); // dicke schwarzer streifen links in screen pixeln
        return uiStage.getViewport().unproject(new Vector2(blackBar, 0)).x; // screen => ui
    }

    public void show() {
        world.getInputProcessor().addProcessor(0, uiStage);
        visible = true;
    }

    public void render(float delta) {
        if (!visible) throw new IllegalStateException("PauseOverlay.render() called without show()");
        uiStage.getViewport().apply(true);
        uiStage.act(delta);
        uiStage.draw();
    }

    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    public void hide() {
        world.getInputProcessor().removeProcessor(uiStage);
        visible = false;
    }

    @Override
    public void dispose() {
        uiStage.dispose();
    }
}
