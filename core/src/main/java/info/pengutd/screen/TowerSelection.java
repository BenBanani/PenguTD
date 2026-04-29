package info.pengutd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TowerSelection implements Disposable {
    private final Stage uiStage;
    private final Texture backgroundTexture;

    public TowerSelection(Viewport viewport) {
        uiStage = new Stage(viewport);
        Gdx.input.setInputProcessor(new InputMultiplexer(Gdx.input.getInputProcessor(), uiStage));

        backgroundTexture = new Texture(Gdx.files.internal("game/tower_selection_screen/background.png"));


        // table auf ganzem Screen
        Table root = new Table();
        root.setFillParent(true);

        // sidebar table
        Table table = new Table();
        table.setBackground(
            new TextureRegionDrawable(new TextureRegion(backgroundTexture)).tint(new Color(1, 1, 1, 0.6f))  // 30% transparent
        );

        table.defaults().pad(10);
        table.padTop(20);

        // linker Bereich nimmt Rest ein
        root.add().expandX().growY();
        // rechte Sidebar
        root.add(table).width(200).growY();

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextButton cannonButton = new TextButton("Cannon", skin);
        TextButton iceButton = new TextButton("Ice", skin);
        TextButton sniperButton = new TextButton("Sniper", skin);

        table.add(cannonButton).width(180).height(60).padBottom(10);
        table.row();

        table.add(iceButton).width(180).height(60).padBottom(10);
        table.row();

        table.add(sniperButton).width(180).height(60);

        uiStage.addActor(root);
    }

    public void render(float delta) {
        uiStage.act(delta);
        uiStage.draw();
    }

    public void dispose() {
        uiStage.dispose();
        backgroundTexture.dispose();
    }
}
