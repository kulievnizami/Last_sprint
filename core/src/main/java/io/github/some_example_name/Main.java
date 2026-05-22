package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import java.util.Random;
import com.badlogic.gdx.Input;
import java.util.ArrayList;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Main extends ApplicationAdapter {

    enum GameState { MENU, RUNNING, GAME_OVER, RECORDS, SETTINGS }
    private GameState state = GameState.MENU;

    private boolean isRussian;
    private float volume;

    private SpriteBatch batch;
    private Texture backgroundGame;
    private Music bgMusic;
    private Music gameMusic;

    private Texture backgroundMenu;
    private Texture darkOverlay;
    private Texture meteoriteTexture;

    private ArrayList<FallingLog> logs;
    private Texture logTexture;

    private Texture islandWindowTexture;
    private Texture buttonTexture;

    private float gameSpeed;


    private Random random = new Random();
    private Viewport viewport;
    private Sound coinSound;
    private BitmapFont font;

    private int point = 0;
    private float score = 0;
    private float nextMeteorSpawnScore = 0;

    private int highPoints = 0;
    private int highScore = 0;
    private Preferences prefs;

    private Character player;
    private ArrayList<Coin> coins;
    private ArrayList<Platform> platforms;
    private ArrayList<Meteorite> meteorites;
    private ArrayList<Warning> warnings;
    private Texture warningTexture;

    private float bgX = 0;
    private float lastPlatformY = 100f;

    private Rectangle menuPlayButton;
    private Rectangle menuRecordsButton;
    private Rectangle menuSettingsButton;
    private Rectangle menuExitButton;

    private Rectangle settingsVolumeButton;
    private Rectangle settingsLangButton;
    private Rectangle settingsBackButton;

    private Rectangle gameOverContinueButton;
    private Rectangle gameOverExitButton;

    private Rectangle recordsBackButton;

    private float windowWidth = 400;
    private float windowHeight = 350;

    @Override
    public void create() {
        Animations.load();
        Platform.load();
        Coin.load();

        prefs = Gdx.app.getPreferences("RetroRunnerSettings");
        highPoints = prefs.getInteger("highPoints", 0);
        highScore = prefs.getInteger("highScore", 0);
        isRussian = prefs.getBoolean("isRussian", true);
        volume = prefs.getFloat("volume", 1.0f);

        meteoriteTexture = new Texture("meteor.png");
        warnings = new ArrayList<>();
        warningTexture = new Texture("Warning.png");
        logs = new ArrayList<>();
        logTexture = new Texture("log.png");
        meteoriteTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        coinSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/coin_play.mp3"));

        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/bg_music.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(volume);

        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/game_music.mp3"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(volume);

        bgMusic.play();

        loadFont();

        batch = new SpriteBatch();

        backgroundGame = new Texture("bg_place2.png");
        backgroundMenu = new Texture("bg_menu.png");

        viewport = new ScreenViewport();

        Pixmap pixmapOverlay = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapOverlay.setColor(0, 0, 0, 0.6f);
        pixmapOverlay.fill();
        darkOverlay = new Texture(pixmapOverlay);
        pixmapOverlay.dispose();

        Pixmap pixmapWindow = new Pixmap(400, 350, Pixmap.Format.RGBA8888);
        pixmapWindow.setColor(0.22f, 0.16f, 0.12f, 0.95f);
        pixmapWindow.fillRectangle(0, 0, 400, 350);
        pixmapWindow.setColor(0.45f, 0.35f, 0.25f, 1f);
        pixmapWindow.drawRectangle(0, 0, 400, 350);
        pixmapWindow.drawRectangle(1, 1, 398, 348);
        islandWindowTexture = new Texture(pixmapWindow);
        pixmapWindow.dispose();

        Pixmap pixmapBtn = new Pixmap(220, 50, Pixmap.Format.RGBA8888);
        pixmapBtn.setColor(0.32f, 0.26f, 0.22f, 1f);
        pixmapBtn.fillRectangle(0, 0, 220, 50);
        pixmapBtn.setColor(0.55f, 0.45f, 0.35f, 1f);
        pixmapBtn.drawRectangle(0, 0, 220, 50);
        buttonTexture = new Texture(pixmapBtn);
        pixmapBtn.dispose();

        player = new Character(200, 300);
        coins = new ArrayList<>();
        platforms = new ArrayList<>();
        meteorites = new ArrayList<>();

        initGame();
    }

    private void loadFont() {
        if (font != null) {
            font.dispose();
        }
        FreeTypeFontGenerator generator;
        if (isRussian) {
            generator = new FreeTypeFontGenerator(Gdx.files.internal("Robot-russian.ttf"));
        } else {
            generator = new FreeTypeFontGenerator(Gdx.files.internal("Robot_anglich.ttf"));
        }
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 28;
        parameter.color = Color.WHITE;
        parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюяABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789:|-.,! %";
        font = generator.generateFont(parameter);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        generator.dispose();
    }

    private void initGame() {
        point = 0;
        score = 0;
        gameSpeed = 550f;
        platforms.clear();
        coins.clear();
        meteorites.clear();
        logs.clear();
        warnings.clear();

        nextMeteorSpawnScore = 100f + random.nextInt(200);

        float platformHeight = 45f;
        lastPlatformY = 100f;

        platforms.add(new Platform(0, lastPlatformY, 800, platformHeight));
        player.reset(200, lastPlatformY + platformHeight);

        for (int i = 0; i < 5; i++) {
            spawnPlatform();
        }
    }

    private void spawnPlatform() {
        float platformHeight = 45f;
        float width = 520f;
        float gap = 350f;

        float yChange = -15 + random.nextInt(30);
        float y = lastPlatformY + yChange;
        boolean isInitializing = true;

        if (y < 70) y = 70;
        if (y > 200) y = 200;

        float x = 0;
        if (!platforms.isEmpty()) {
            Platform lastPlatform = platforms.get(platforms.size() - 1);
            x = lastPlatform.getX() + lastPlatform.getWidth() + gap;
        }

        platforms.add(new Platform(x, y, width, platformHeight));
        lastPlatformY = y;

        if (random.nextInt(100) < 70) {
            float gapCenterX = x - gap / 2f;
            warnings.add(new Warning(gapCenterX));
        }


        if (random.nextInt(100) < 60) {
            float coinX = x + width / 2 - 32;
            float coinY = y + 85f;
            coins.add(new Coin(coinX, coinY));
        }
    }

    private void spawnMeteorite() {
        float startX = viewport.getWorldWidth() + 50;
        float startY = lastPlatformY + 70 + random.nextInt(150);
        float meteorSpeed = gameSpeed + 150f + random.nextInt(150);
        meteorites.add(new Meteorite(startX, startY, meteorSpeed));
    }

    private void checkAndSaveRecords() {
        boolean isUpdated = false;

        if (point > highPoints) {
            highPoints = point;
            prefs.putInteger("highPoints", highPoints);
            isUpdated = true;
        }

        if ((int)score > highScore) {
            highScore = (int)score;
            prefs.putInteger("highScore", highScore);
            isUpdated = true;
        }

        if (isUpdated) {
            prefs.flush();
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        float delta = Gdx.graphics.getDeltaTime();
        boolean holdTime = Gdx.input.isKeyPressed(Input.Keys.R) && player.isOnGround();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float wx = (viewport.getWorldWidth() - windowWidth) / 2;
        float wy = (viewport.getWorldHeight() - windowHeight) / 2;

        menuPlayButton = new Rectangle(wx + 90, wy + 230, 220, 50);
        menuRecordsButton = new Rectangle(wx + 90, wy + 165, 220, 50);
        menuSettingsButton = new Rectangle(wx + 90, wy + 100, 220, 50);
        menuExitButton = new Rectangle(wx + 90, wy + 35, 220, 50);

        settingsLangButton = new Rectangle(wx + 90, wy + 165, 220, 50);
        settingsVolumeButton = new Rectangle(wx + 90, wy + 100, 220, 50);
        settingsBackButton = new Rectangle(wx + 90, wy + 35, 220, 50);

        gameOverContinueButton = new Rectangle(wx + 90, wy + 110, 220, 50);
        gameOverExitButton = new Rectangle(wx + 90, wy + 40, 220, 50);

        recordsBackButton = new Rectangle(wx + 90, wy + 40, 220, 50);



        if (state == GameState.RUNNING) {
            bgMusic.stop();
            gameMusic.play();
            gameSpeed += 3f * delta;
            if (!holdTime) {score += gameSpeed * delta * 0.05f;}
            float moveDistance = gameSpeed * delta;

            if (!holdTime) {
                player.update(delta, platforms);

                bgX -= (moveDistance * 0.4f);
                if (bgX <= -viewport.getWorldWidth()) bgX = 0;
            }

            if (score >= nextMeteorSpawnScore) {
                spawnMeteorite();
                nextMeteorSpawnScore = score + 100f + random.nextInt(200);
            }

            for (int i = meteorites.size() - 1; i >= 0; i--) {
                Meteorite m = meteorites.get(i);
                m.update(delta);
                if (isColliding(player, m)) {
                    state = GameState.GAME_OVER;
                    checkAndSaveRecords();
                } else if (m.getX() + m.getWidth() < -100) {
                    meteorites.remove(i);
                }
            }
            for (int i = logs.size() - 1; i >= 0; i--) {

                FallingLog log = logs.get(i);

                log.update(delta);
                if (!holdTime) log.move(moveDistance);

                if (isColliding(player, log)) {
                    state = GameState.GAME_OVER;
                    checkAndSaveRecords();
                }

                if (log.getY() + log.getHeight() < 0) {
                    logs.remove(i);
                }
            }

            for (int i = warnings.size() - 1; i >= 0; i--) {

                Warning w = warnings.get(i);

                w.update(delta);

                if (w.isFinished()) {

                    float logX = w.getX() - 110f;

                    logs.add(new FallingLog(
                        logX,
                        viewport.getWorldHeight() + 100
                    ));

                    warnings.remove(i);
                }
            }



            if (!holdTime) {
                for (int i = platforms.size() - 1; i >= 0; i--) {
                    Platform p = platforms.get(i);
                    p.move(moveDistance);

                    if (p.getX() + p.getWidth() < 0) {
                        platforms.remove(i);
                        spawnPlatform();
                    }
                }
                for (Warning w : warnings) {
                    w.move(moveDistance);
                }
            }

            for (int i = coins.size() - 1; i >= 0; i--) {
                Coin coin = coins.get(i);
                if (!holdTime) {
                    coin.move(moveDistance);
                }
                coin.updateTime(delta);
                if (isColliding(player, coin)) {
                    coinSound.play(volume);
                    coins.remove(i);
                    point++;
                } else if (coin.getX() + coin.getWidth() < 0) {
                    coins.remove(i);
                }
            }

            if (player.getY() < -player.getHeight()) {
                state = GameState.GAME_OVER;
                checkAndSaveRecords();
            }
        }

        if (Gdx.input.justTouched()) {
            Vector2 touch = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            if (state == GameState.MENU) {
                gameMusic.stop();
                bgMusic.play();
                if (menuPlayButton.contains(touch.x, touch.y)) {
                    initGame();
                    state = GameState.RUNNING;
                } else if (menuRecordsButton.contains(touch.x, touch.y)) {
                    state = GameState.RECORDS;
                } else if (menuSettingsButton.contains(touch.x, touch.y)) {
                    state = GameState.SETTINGS;
                } else if (menuExitButton.contains(touch.x, touch.y)) {
                    Gdx.app.exit();
                }
            }
            else if (state == GameState.SETTINGS) {
                if (settingsLangButton.contains(touch.x, touch.y)) {
                    isRussian = !isRussian;
                    prefs.putBoolean("isRussian", isRussian);
                    prefs.flush();
                    loadFont();
                } else if (settingsVolumeButton.contains(touch.x, touch.y)) {
                    volume -= 0.25f;
                    if (volume < 0f) volume = 1.0f;
                    prefs.putFloat("volume", volume);
                    prefs.flush();
                    bgMusic.setVolume(volume);
                } else if (settingsBackButton.contains(touch.x, touch.y)) {
                    state = GameState.MENU;
                }
            }
            else if (state == GameState.GAME_OVER) {
                if (gameOverContinueButton.contains(touch.x, touch.y)) {
                    initGame();
                    state = GameState.RUNNING;
                } else if (gameOverExitButton.contains(touch.x, touch.y)) {
                    state = GameState.MENU;
                }
            }
            else if (state == GameState.RECORDS) {
                if (recordsBackButton.contains(touch.x, touch.y)) {
                    state = GameState.MENU;
                }
            }
        }

        batch.begin();
        float bgWidth = viewport.getWorldWidth();




        if (state == GameState.RUNNING || state == GameState.GAME_OVER) {
            batch.draw(backgroundGame, bgX, 0, bgWidth, viewport.getWorldHeight());
            batch.draw(backgroundGame, bgX + bgWidth - 2f, 0, bgWidth, viewport.getWorldHeight());

            for (Warning w : warnings) {w.render(batch, warningTexture, viewport);}
            for (Platform platform : platforms) platform.render(batch);
            for (Coin coin : coins) coin.render(batch);
            for (Meteorite m : meteorites) m.render(batch, meteoriteTexture);
            for (FallingLog log : logs) {log.render(batch, logTexture);}
            player.render(batch);




            if (state == GameState.RUNNING) {
                font.setColor(Color.WHITE);
                font.draw(batch, (isRussian ? "Очки: " : "Points: ") + point, 30, viewport.getWorldHeight() - 30);
                font.draw(batch, (isRussian ? "Счет: " : "Score: ") + (int) score, 30, viewport.getWorldHeight() - 70);
            } else {
                batch.draw(darkOverlay, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
                batch.draw(islandWindowTexture, wx, wy, windowWidth, windowHeight);

                font.setColor(Color.RED);
                font.draw(batch, isRussian ? "ИГРА ОКОНЧЕНА" : "GAME OVER", wx + (isRussian ? 80 : 120), wy + 280);
                font.setColor(Color.WHITE);
                font.draw(batch, (isRussian ? "Очки: " : "Points: ") + point + (isRussian ? "  |  Счет: " : "  |  Score: ") + (int) score, wx + (isRussian ? 45 : 60), wy + 210);

                batch.draw(buttonTexture, gameOverContinueButton.x, gameOverContinueButton.y, gameOverContinueButton.width, gameOverContinueButton.height);
                batch.draw(buttonTexture, gameOverExitButton.x, gameOverExitButton.y, gameOverExitButton.width, gameOverExitButton.height);

                font.draw(batch, isRussian ? "ЗАНОВО" : "RESTART", gameOverContinueButton.x + (isRussian ? 65 : 60), gameOverContinueButton.y + 35);
                font.draw(batch, isRussian ? "МЕНЮ" : "MENU", gameOverExitButton.x + (isRussian ? 75 : 80), gameOverExitButton.y + 35);
            }
        }
        else if (state == GameState.MENU) {
            batch.draw(backgroundMenu, 0, 0, bgWidth, viewport.getWorldHeight());
            batch.draw(islandWindowTexture, wx, wy, windowWidth, windowHeight);

            font.setColor(Color.GOLD);
            font.draw(batch, isRussian ? "ГЛАВНОЕ МЕНЮ" : "MAIN MENU", wx + (isRussian ? 90 : 125), wy + 310);

            batch.draw(buttonTexture, menuPlayButton.x, menuPlayButton.y, menuPlayButton.width, menuPlayButton.height);
            batch.draw(buttonTexture, menuRecordsButton.x, menuRecordsButton.y, menuRecordsButton.width, menuRecordsButton.height);
            batch.draw(buttonTexture, menuSettingsButton.x, menuSettingsButton.y, menuSettingsButton.width, menuSettingsButton.height);
            batch.draw(buttonTexture, menuExitButton.x, menuExitButton.y, menuExitButton.width, menuExitButton.height);

            font.setColor(Color.WHITE);
            font.draw(batch, isRussian ? "ИГРАТЬ" : "PLAY", menuPlayButton.x + (isRussian ? 65 : 85), menuPlayButton.y + 35);
            font.draw(batch, isRussian ? "РЕКОРДЫ" : "RECORDS", menuRecordsButton.x + (isRussian ? 50 : 60), menuRecordsButton.y + 35);
            font.draw(batch, isRussian ? "НАСТРОЙКИ" : "SETTINGS", menuSettingsButton.x + (isRussian ? 35 : 55), menuSettingsButton.y + 35);
            font.draw(batch, isRussian ? "ВЫХОД" : "EXIT", menuExitButton.x + (isRussian ? 70 : 85), menuExitButton.y + 35);
        }
        else if (state == GameState.SETTINGS) {
            batch.draw(backgroundMenu, 0, 0, bgWidth, viewport.getWorldHeight());
            batch.draw(islandWindowTexture, wx, wy, windowWidth, windowHeight);

            font.setColor(Color.GOLD);
            font.draw(batch, isRussian ? "НАСТРОЙКИ" : "SETTINGS", wx + (isRussian ? 120 : 135), wy + 310);

            batch.draw(buttonTexture, settingsLangButton.x, settingsLangButton.y, settingsLangButton.width, settingsLangButton.height);
            batch.draw(buttonTexture, settingsVolumeButton.x, settingsVolumeButton.y, settingsVolumeButton.width, settingsVolumeButton.height);
            batch.draw(buttonTexture, settingsBackButton.x, settingsBackButton.y, settingsBackButton.width, settingsBackButton.height);

            font.setColor(Color.WHITE);
            font.draw(batch, isRussian ? "ЯЗЫК: РУС" : "LANG: ENG", settingsLangButton.x + 40, settingsLangButton.y + 35);
            font.draw(batch, (isRussian ? "ЗВУК: " : "VOL: ") + (int)(volume * 100) + "%", settingsVolumeButton.x + 50, settingsVolumeButton.y + 35);
            font.draw(batch, isRussian ? "НАЗАД" : "BACK", settingsBackButton.x + (isRussian ? 70 : 85), settingsBackButton.y + 35);
        }
        else if (state == GameState.RECORDS) {
            batch.draw(backgroundMenu, 0, 0, bgWidth, viewport.getWorldHeight());
            batch.draw(islandWindowTexture, wx, wy, windowWidth, windowHeight);

            font.setColor(Color.GOLD);
            font.draw(batch, isRussian ? "РЕКОРДЫ" : "HIGH SCORES", wx + (isRussian ? 130 : 110), wy + 310);

            font.setColor(Color.WHITE);
            font.draw(batch, (isRussian ? "Макс. Очки: " : "Best Points: ") + highPoints, wx + 60, wy + 210);
            font.draw(batch, (isRussian ? "Макс. Счет: " : "Best Score: ") + highScore, wx + 60, wy + 160);

            batch.draw(buttonTexture, recordsBackButton.x, recordsBackButton.y, recordsBackButton.width, recordsBackButton.height);
            font.draw(batch, isRussian ? "НАЗАД" : "BACK", recordsBackButton.x + (isRussian ? 70 : 85), recordsBackButton.y + 35);
        }

        batch.end();
    }

    private boolean isColliding(Character player, Coin coin) {
        return player.getX() < coin.getX() + coin.getWidth() &&
            player.getX() + player.getWidth() > coin.getX() &&
            player.getY() < coin.getY() + coin.getHeight() &&
            player.getY() + player.getHeight() > coin.getY();
    }

    private boolean isColliding(Character player, Meteorite m) {
        float hitShrink = 12f;
        return player.getX() < m.getX() + m.getWidth() - hitShrink &&
            player.getX() + player.getWidth() > m.getX() + hitShrink &&
            player.getY() < m.getY() + m.getHeight() - hitShrink &&
            player.getY() + player.getHeight() > m.getY() + hitShrink;

    }
    private boolean isColliding(Character player, FallingLog log) {
        float hitShrink = 12f;

        return player.getX() < log.getX() + log.getWidth() - hitShrink &&
            player.getX() + player.getWidth() > log.getX() + hitShrink &&
            player.getY() < log.getY() + log.getHeight() - hitShrink &&
            player.getY() + player.getHeight() > log.getY() + hitShrink;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundGame.dispose();
        backgroundMenu.dispose();
        meteoriteTexture.dispose();
        darkOverlay.dispose();
        islandWindowTexture.dispose();
        buttonTexture.dispose();
        Animations.dispose();
        Platform.dispose();
        player.dispose();
        font.dispose();
        coinSound.dispose();
        Coin.dispose();
    }
}
