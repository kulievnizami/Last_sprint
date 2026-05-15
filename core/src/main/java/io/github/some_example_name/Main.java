package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import java.util.Random;
import java.util.ArrayList;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture background;

    private float gameSpeed = 500f;

    private Random random = new Random();

    private Viewport viewport;

    private Sound coinSound;

    private BitmapFont font;

    private int score = 0;

    private Character player;

    private ArrayList<Coin> coins;

    private float bgX = 0;

    @Override
    public void create() {

        Animations.load();

        coinSound = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/coin_play.mp3")
        );

        font = new BitmapFont();

        font.getData().setScale(2);

        font.setColor(Color.WHITE);

        batch = new SpriteBatch();

        background = new Texture("bg_place.png");

        viewport = new ScreenViewport();

        player = new Character(200, 100);

        coins = new ArrayList<>();


        for (int i = 0; i < 6; i++) {
            spawnCoin(700 + i * 300);
        }
    }

    @Override
    public void render() {

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        float delta = Gdx.graphics.getDeltaTime();

        player.update(delta);


        bgX -= (gameSpeed * 0.4f) * delta;

        if (bgX <= -viewport.getWorldWidth()) {
            bgX = 0;
        }


        for (Coin coin : coins) {
            coin.update(delta);
        }


        for (int i = coins.size() - 1; i >= 0; i--) {

            Coin coin = coins.get(i);


            if (isColliding(player, coin)) {

                coinSound.play();

                coins.remove(i);

                score++;

                if (score % 15 == 0) {
                    gameSpeed += 699f;
                }

                spawnCoin(2200);
            }


            else if (coin.getX() < -100) {

                coins.remove(i);

                spawnCoin(2200);
            }
        }

        viewport.apply();

        batch.setProjectionMatrix(
            viewport.getCamera().combined
        );

        batch.begin();

        float bgWidth = viewport.getWorldWidth();

        batch.draw(
            background,
            bgX,
            0,
            bgWidth,
            viewport.getWorldHeight()
        );

        batch.draw(
            background,
            bgX + bgWidth,
            0,
            bgWidth,
            viewport.getWorldHeight()
        );


        font.draw(
            batch,
            "Score: " + score,
            30,
            viewport.getWorldHeight() - 30
        );


        for (Coin coin : coins) {
            coin.render(batch);
        }


        player.render(batch);

        batch.end();
    }

    private void spawnCoin(float x) {

        float randomY = 100 + random.nextInt(300);

        coins.add(
            new Coin(x, randomY, gameSpeed)
        );
    }

    private boolean isColliding(Character player, Coin coin) {

        return player.getX() < coin.getX() + coin.getWidth() &&
            player.getX() + player.getWidth() > coin.getX() &&
            player.getY() < coin.getY() + coin.getHeight() &&
            player.getY() + player.getHeight() > coin.getY();
    }

    @Override
    public void resize(int width, int height) {

        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {

        batch.dispose();

        background.dispose();

        Animations.dispose();

        player.dispose();

        font.dispose();

        coinSound.dispose();
    }
}
