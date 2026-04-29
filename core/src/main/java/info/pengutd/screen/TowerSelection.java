package info.pengutd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TowerSelection {
    private final Stage uiStage;
    private final Table table;
    private Texture backGroundTexture;

    public TowerSelection() {
        uiStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(new InputMultiplexer(Gdx.input.getInputProcessor(), uiStage));

        table = new Table();
        table.setFillParent(true);
        table.top().right();
        table.pad(20);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextButton cannonButton = new TextButton("Cannon", skin);
        TextButton iceButton = new TextButton("Ice", skin);
        TextButton sniperButton = new TextButton("Sniper", skin);

        // Vertikal anordnen
        table.add(cannonButton).width(180).height(60).padBottom(10);
        table.row();

        table.add(iceButton).width(180).height(60).padBottom(10);
        table.row();

        table.add(sniperButton).width(180).height(60);

        uiStage.addActor(table);
    }

    public void render(float delta) {
        uiStage.act(delta);
        uiStage.draw();
    }
}
