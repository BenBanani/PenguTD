package info.pengutd.game;

import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.game.enemy.BushEnemy;
import info.pengutd.game.enemy.CoolEnemy;
import info.pengutd.game.enemy.FatEnemy;
import info.pengutd.game.enemy.WarriorEnemy;
import info.pengutd.save.JsonSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class WaveManager implements JsonSerializable {
    private final @NotNull World world;
    private final @NotNull HashMap<Integer, ArrayList<Integer>> waves = new HashMap<>();
    private int currentWave = 1;
    private boolean waveFinished = false;
    private float spawnTimer = 0f;
    private float spawnDelay = 0.7f; // time between spawns
    private int waveIndex = 0;
    public WaveManager(@NotNull World world) {
        this.world = world;
        if (world.getMapName().equals("map1")) {
                    waves.put(1, wave(1, 4, 1, 4, 4, 1, 4, 1));
                    waves.put(2, wave(1, 1, 4, 1, 4, 4, 1, 4, 1));
                    waves.put(3, wave(1, 4, 1, 4, 1, 4, 4, 1, 4, 1));
                    waves.put(4, wave(4, 1, 1, 4, 1, 4, 4, 1, 4, 1, 4));
                    waves.put(5, wave(1, 4, 4, 1, 4, 1, 1, 4, 4, 1));
                    waves.put(6, wave(1, 4, 1, 4, 4, 1, 4, 4, 1, 1, 4, 4));
                    waves.put(7, wave(4, 1, 4, 4, 1, 1, 4, 4, 1, 4, 1));
                    waves.put(8, wave(1, 1, 4, 4, 1, 4, 4, 1, 1, 4, 4, 1, 4));
                    waves.put(9, wave(4, 4, 1, 1, 4, 4, 1, 4, 1, 4, 4, 1));
                    waves.put(10, wave(1, 4, 4, 1, 4, 4, 1, 1, 4, 4, 1, 4, 4, 1));
                    waves.put(11, wave(4, 1, 4, 5, 1, 4, 1, 5, 4, 1, 4, 5, 1));
                    waves.put(12, wave(1, 5, 4, 1, 4, 5, 1, 4, 5, 4, 1, 5, 4, 1));
                    waves.put(13, wave(5, 1, 4, 5, 4, 1, 5, 4, 1, 5, 4, 1, 5, 4));
                    waves.put(14, wave(1, 5, 5, 4, 1, 5, 4, 5, 1, 4, 5, 5, 1, 4, 5));
                    waves.put(15, wave(5, 4, 1, 5, 5, 4, 1, 5, 5, 4, 1, 5, 5, 4, 1));
                    waves.put(16, wave(1, 5, 5, 4, 5, 1, 5, 5, 4, 5, 1, 5, 5, 4, 5, 1));
                    waves.put(17, wave(5, 1, 5, 5, 5, 4, 1, 5, 5, 5, 4, 5, 1, 5, 5, 5));
                    waves.put(18, wave(1, 5, 5, 5, 5, 1, 5, 5, 5, 5, 1, 5, 5, 5, 4, 5, 1));
                    waves.put(19, wave(5, 5, 1, 5, 5, 5, 1, 5, 5, 5, 5, 1, 5, 5, 5, 5, 1));
                    waves.put(20, wave(1, 5, 5, 5, 5, 5, 1, 5, 5, 5, 5, 5, 1, 5, 5, 5, 5, 5));
                    waves.put(21, wave(5, 5, 6, 1, 5, 5, 6, 5, 1, 5, 6, 5, 5, 1, 6, 5, 5));
                    waves.put(22, wave(1, 6, 5, 5, 6, 1, 5, 6, 5, 6, 1, 5, 6, 5, 6, 1, 5));
                    waves.put(23, wave(6, 1, 5, 6, 6, 5, 1, 6, 6, 5, 1, 6, 6, 5, 1, 6, 6));
                    waves.put(24, wave(1, 6, 6, 6, 1, 6, 6, 6, 1, 6, 6, 6, 1, 6, 6, 6, 5));
                    waves.put(25, wave(6, 6, 1, 6, 6, 6, 1, 6, 6, 6, 6, 1, 6, 6, 6, 6, 1));
                    waves.put(26, wave(1, 6, 6, 6, 6, 6, 1, 6, 6, 6, 6, 6, 1, 6, 6, 6, 6, 6));
                    waves.put(27, wave(6, 6, 6, 1, 6, 6, 6, 6, 1, 6, 6, 6, 6, 6, 1, 6, 6, 6, 6));
                    waves.put(28, wave(1, 6, 6, 6, 6, 6, 6, 1, 6, 6, 6, 6, 6, 6, 1, 6, 6, 6, 6, 6));
                    waves.put(29, wave(6, 6, 6, 6, 1, 6, 6, 6, 6, 6, 1, 6, 6, 6, 6, 6, 6, 1, 6, 6, 6));
                    waves.put(30, wave(1, 6, 6, 6, 6, 6, 6, 6, 1, 6, 6, 6, 6, 6, 6, 6, 1, 6, 6, 6, 6, 6, 6));

        } else if (world.getMapName().equals("map2")) {

                    waves.put(1, wave(2, 2, 4, 2, 2, 4, 2, 4, 2, 2));
                    waves.put(2, wave(2, 4, 2, 2, 4, 2, 4, 2, 2, 4, 2));
                    waves.put(3, wave(4, 2, 2, 4, 2, 2, 4, 2, 4, 2, 2, 4));
                    waves.put(4, wave(2, 2, 4, 2, 4, 2, 2, 4, 2, 4, 2, 2, 4));
                    waves.put(5, wave(4, 2, 4, 2, 2, 4, 2, 4, 2, 2, 4, 2, 4));
                    waves.put(6, wave(2, 4, 2, 4, 2, 2, 4, 2, 4, 4, 2, 4, 2, 4));
                    waves.put(7, wave(4, 2, 4, 4, 2, 4, 2, 4, 4, 2, 4, 2, 4, 4, 2));
                    waves.put(8, wave(2, 4, 4, 2, 4, 4, 2, 4, 4, 5, 2, 4, 4, 2, 4, 5));
                    waves.put(9, wave(4, 5, 2, 4, 4, 5, 2, 4, 5, 2, 4, 5, 4, 2, 5, 4, 2));
                    waves.put(10, wave(2, 5, 4, 5, 2, 4, 5, 5, 2, 4, 5, 5, 2, 4, 5, 5, 2, 4));
                    waves.put(11, wave(5, 5, 2, 4, 5, 5, 5, 2, 4, 5, 5, 5, 2, 5, 5, 5, 2, 4, 5));
                    waves.put(12, wave(2, 5, 5, 5, 5, 2, 4, 5, 5, 5, 5, 2, 5, 5, 5, 5, 2, 4, 5, 5));
                    waves.put(13, wave(5, 5, 5, 2, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5));
                    waves.put(14, wave(2, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5));
                    waves.put(15, wave(5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5, 5, 2));
                    waves.put(16, wave(2, 5, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5));
                    waves.put(17, wave(5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5, 5, 2));
                    waves.put(18, wave(2, 5, 5, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5));
                    waves.put(19, wave(5, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5, 5, 5));
                    waves.put(20, wave(2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5));
                    waves.put(21, wave(5, 6, 5, 5, 5, 5, 5, 2, 6, 5, 5, 5, 5, 5, 5, 2, 6, 5, 5, 5, 5, 5, 5, 6, 2));
                    waves.put(22, wave(2, 6, 5, 5, 5, 6, 5, 5, 5, 2, 6, 5, 5, 5, 6, 5, 5, 5, 2, 6, 5, 5, 5, 6, 5, 5));
                    waves.put(23, wave(6, 5, 5, 2, 6, 6, 5, 5, 2, 6, 6, 5, 5, 2, 6, 6, 5, 5, 2, 6, 6, 5, 5, 6, 6, 2));
                    waves.put(24, wave(2, 6, 6, 6, 5, 2, 6, 6, 6, 5, 2, 6, 6, 6, 5, 2, 6, 6, 6, 5, 2, 6, 6, 6, 5, 6));
                    waves.put(25, wave(6, 6, 6, 2, 6, 6, 6, 6, 2, 6, 6, 6, 6, 2, 6, 6, 6, 6, 2, 6, 6, 6, 6, 2, 6, 6, 6));
                    waves.put(26, wave(2, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 2, 6, 6, 6));
                    waves.put(27, wave(6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 6, 2, 6, 6));
                    waves.put(28, wave(2, 6, 6, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6));
                    waves.put(29, wave(6, 6, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 6));
                    waves.put(30, wave(2, 6, 6, 6, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 6, 6, 6, 2, 6, 6, 6, 6, 6, 6, 6));


        } else { // map3// 30 waves
                    waves.put(1, wave(4, 4, 4, 4, 5, 4, 4, 4, 5, 4));
                    waves.put(2, wave(4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5));
                    waves.put(3, wave(5, 4, 4, 5, 4, 5, 4, 4, 5, 4, 4, 5));
                    waves.put(4, wave(4, 5, 5, 4, 4, 5, 5, 4, 4, 5, 4, 5, 4));
                    waves.put(5, wave(5, 4, 5, 5, 4, 4, 5, 5, 4, 5, 4, 5, 4));
                    waves.put(6, wave(4, 5, 5, 4, 5, 5, 4, 5, 5, 4, 5, 4, 5, 5));
                    waves.put(7, wave(5, 5, 4, 5, 5, 4, 5, 5, 5, 4, 5, 5, 4, 5, 5));
                    waves.put(8, wave(4, 5, 5, 5, 4, 5, 5, 5, 4, 5, 5, 5, 4, 5, 5, 5));
                    waves.put(9, wave(5, 5, 5, 4, 5, 5, 5, 5, 4, 5, 5, 5, 4, 5, 5, 5, 5));
                    waves.put(10, wave(5, 5, 5, 5, 4, 5, 5, 5, 5, 4, 5, 5, 5, 5, 4, 5, 5, 5));
                    waves.put(11, wave(5, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5, 4, 5));
                    waves.put(12, wave(4, 5, 5, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5));
                    waves.put(13, wave(5, 5, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5, 5));
                    waves.put(14, wave(4, 5, 5, 5, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5, 5));
                    waves.put(15, wave(5, 5, 5, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5, 5, 5));
                    waves.put(16, wave(5, 6, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5, 6));
                    waves.put(17, wave(5, 5, 6, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 6, 5, 5));
                    waves.put(18, wave(6, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5));
                    waves.put(19, wave(5, 6, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5));
                    waves.put(20, wave(6, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5));
                    waves.put(21, wave(5, 6, 6, 5, 5, 5, 5, 5, 5, 6, 6, 5, 5, 5, 5, 5, 5, 6, 6, 5, 5, 5, 5, 5, 5, 6, 6, 5, 5));
                    waves.put(22, wave(6, 6, 5, 5, 5, 5, 5, 5, 6, 6, 5, 5, 5, 5, 5, 5, 6, 6, 5, 5, 5, 5, 5, 5, 6, 6, 5, 5, 5, 5));
                    waves.put(23, wave(5, 6, 6, 6, 5, 5, 5, 5, 5, 6, 6, 6, 5, 5, 5, 5, 5, 6, 6, 6, 5, 5, 5, 5, 5, 6, 6, 6, 5, 5));
                    waves.put(24, wave(6, 6, 6, 5, 5, 5, 5, 5, 6, 6, 6, 6, 5, 5, 5, 5, 6, 6, 6, 6, 5, 5, 5, 5, 6, 6, 6, 6, 5, 5, 5));
                    waves.put(25, wave(5, 6, 6, 6, 6, 5, 5, 5, 5, 6, 6, 6, 6, 5, 5, 5, 5, 6, 6, 6, 6, 5, 5, 5, 5, 6, 6, 6, 6, 5, 5, 5));
                    waves.put(26, wave(6, 6, 6, 6, 6, 5, 5, 5, 6, 6, 6, 6, 6, 5, 5, 5, 6, 6, 6, 6, 6, 5, 5, 5, 6, 6, 6, 6, 6, 5, 5, 5, 6));
                    waves.put(27, wave(5, 6, 6, 6, 6, 6, 6, 5, 5, 6, 6, 6, 6, 6, 6, 5, 5, 6, 6, 6, 6, 6, 6, 5, 5, 6, 6, 6, 6, 6, 6, 5, 5));
                    waves.put(28, wave(6, 6, 6, 6, 6, 6, 5, 5, 6, 6, 6, 6, 6, 6, 6, 5, 5, 6, 6, 6, 6, 6, 6, 6, 5, 5, 6, 6, 6, 6, 6, 6, 6, 5));
                    waves.put(29, wave(5, 6, 6, 6, 6, 6, 6, 6, 6, 5, 6, 6, 6, 6, 6, 6, 6, 6, 5, 6, 6, 6, 6, 6, 6, 6, 6, 5, 6, 6, 6, 6, 6, 6, 6));
                    waves.put(30, wave(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6));
        }
    }

    private static @NotNull ArrayList<Integer> wave(int @NotNull ... enemies) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int e : enemies) list.add(e);
        return list;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public boolean isWaveFinished() {
        return waveFinished;
    }

    public void update(float delta) {
        //unpack wave
        processWave(delta);

        if (currentWave >= waves.size()) {
            world.win();
        }
    }

    private void processWave(float delta) {
        ArrayList<Integer> wave = waves.get(currentWave);
        if (wave == null || wave.isEmpty()) return;

        if (waveFinished) return; // schon fertig, nichts tun

        spawnTimer += delta;

        if (spawnTimer >= spawnDelay && waveIndex < wave.size()) {
            spawnTimer = 0;
            spawnEnemy(wave, waveIndex);
            waveIndex++;
        }

        // Nach dem Spawn prüfen ob alle gespawnt wurden
        if (waveIndex >= wave.size()) {
            waveFinished = true;
        }
    }

    private void spawnEnemy(@NotNull ArrayList<Integer> wave, int i) {
        switch (wave.get(i)) {
            case 1:
                world.addEnemy(new FatEnemy(world, world.createEntityId()));
                break;
            case 2:
                world.addEnemy(new CoolEnemy(world, world.createEntityId()));
                break;
            case 3:
                world.addEnemy(new BushEnemy(world, world.createEntityId()));
                break;
            case 4:
                world.addEnemy(new WarriorEnemy(1, world, world.createEntityId()));
                break;
            case 5:
                world.addEnemy(new WarriorEnemy(2, world, world.createEntityId()));
                break;
            case 6:
                world.addEnemy(new WarriorEnemy(3, world, world.createEntityId()));
                break;
            case 7:
                world.addEnemy(new WarriorEnemy(4, world, world.createEntityId()));
                break;
        }
    }

    public void startWave() {
        if (waveFinished && world.getEnemies().size == 0 && currentWave < waves.size()) {
            currentWave++;
            waveFinished = false;
            waveIndex = 0;
            spawnTimer = 0f;
            world.nextWave();
        }
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        value.addChild("current_wave", new JsonValue(currentWave));
        value.addChild("wave_finished", new JsonValue(waveFinished));
        value.addChild("wave_index", new JsonValue(waveIndex));
        return value;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        currentWave = json.getInt("current_wave");
        waveFinished = json.getBoolean("wave_finished");
        waveIndex = json.getInt("wave_index");
    }
}
