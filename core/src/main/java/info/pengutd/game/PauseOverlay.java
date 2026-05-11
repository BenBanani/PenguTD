package info.pengutd.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import org.jetbrains.annotations.NotNull;

public class PauseOverlay implements Disposable {
    private final @NotNull World world;
    private Stage uiStage;
    private Table table;
    private boolean visible = false;

    public PauseOverlay(@NotNull World world) {
        this.world = world;

        uiStage = new Stage(world.getViewport());
        table = new Table().center();
        table.setFillParent(true);
        table.add(new Label("Paused", new Label.LabelStyle(new BitmapFont(), Color.BLACK))).row();
        uiStage.addActor(table);
        table.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("game/tower_selection_screen/background.png"))).tint(new Color(1, 1, 1, 0.2f))); // todo

        uiStage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    world.setPaused(false);
                    return true;
                }
                return super.keyDown(event, keycode);
            }
        });
    }

    public void show() {
        world.getInputProcessor().addProcessor(uiStage);
        visible = true;
    }

    public void render(float delta) {
        if (!visible) throw new IllegalStateException("PauseOverlay.render() called without show()");
        uiStage.act(delta);
        uiStage.draw();
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
