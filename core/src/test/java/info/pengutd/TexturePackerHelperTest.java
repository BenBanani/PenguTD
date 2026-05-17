package info.pengutd;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

class TexturePackerHelperTest {

    @Test
    void main_callsTexturePackerProcessForEveryAtlas() {
        MockedStatic<TexturePacker> mocked = mockStatic(TexturePacker.class);
        try {
            TexturePackerHelper.main(new String[]{});

            mocked.verify(() -> TexturePacker.process(
                    "assets/start_screen",
                    "assets/atlas",
                    "start_screen_ui"
            ));

            mocked.verify(() -> TexturePacker.process(
                    "assets/settings_screen",
                    "assets/atlas",
                    "settings_screen_ui"
            ));

            mocked.verify(() -> TexturePacker.process(
                    "assets//game/tower_selection_screen",
                    "assets/atlas",
                    "tower_selection_ui"
            ));

            mocked.verify(() -> TexturePacker.process(
                    "assets//game/pause_screen",
                    "assets/atlas",
                    "pause_screen_ui"
            ));

            mocked.verify(() -> TexturePacker.process(
                    "assets//game/enemy",
                    "assets/atlas",
                    "enemies"
            ));

            mocked.verify(() -> TexturePacker.process(
                    "assets//game/tower/projectile",
                    "assets/atlas",
                    "projectiles"
            ));

            mocked.verify(() -> TexturePacker.process(
                    "assets//game/tower/",
                    "assets/atlas",
                    "towers"
            ));

            // Ensure no unexpected extra calls were made
            mocked.verify(
                    () -> TexturePacker.process(anyString(), anyString(), anyString()),
                    times(7)
            );
        } finally {
            mocked.close();
        }
    }
}