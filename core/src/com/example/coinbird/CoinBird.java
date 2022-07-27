package com.example.coinbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.Random;

public class CoinBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] bird;
	Texture hit;
	int birdState = 0;
	int pause = 0;
	float gravity = 0.3f;
	float velocity = 0;
	int birdY = 0;
	int score = 0;
	int gameState = 0;
	Rectangle birdRect;
	BitmapFont font;
	ArrayList<Integer> coinX = new ArrayList<>();
	ArrayList<Integer> coinY = new ArrayList<>();
	ArrayList<Rectangle>  coinRect = new ArrayList<>();

	Texture coin;
	int coinCount;
	ArrayList<Integer> bombX = new ArrayList<>();
	ArrayList<Integer> bombY = new ArrayList<>();
	ArrayList<Rectangle>  bombRect = new ArrayList<>();
	Texture bomb;
	int bombCount;
	Random random;
	int level = 4;
	int i = 0;
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		bird = new Texture[4];
		bird[0] = new Texture("frame-1.png");
		bird[1] = new Texture("frame-2.png");
		bird[2] = new Texture("frame-3.png");
		bird[3] = new Texture("frame-4.png");
		hit = new Texture("hit-1.png");
		birdY = Gdx.graphics.getHeight()/2;
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

	}
	public void makeCoins(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();

		coinY.add((int)height);
		coinX.add(Gdx.graphics.getWidth());
	}

	public void makeBombs(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();

		bombY.add((int)height);
		bombX.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1){				 //Game is active

			//		Level (Fast)
			i++;
			if(i > 400 && level < 13){
				i = 0;
				level++;
			}

			//BOMBS
			if(bombCount < 220) {
				bombCount++;
			} else{
				bombCount = 0;
				makeBombs();
			}

			bombRect.clear();
			for(int i = 0; i < bombX.size(); i++) {
				batch.draw(bomb, bombX.get(i), bombY.get(i));
				bombX.set(i, bombX.get(i) - (int)1.5*level);
				bombRect.add(new Rectangle(bombX.get(i), bombY.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			//COINS
			if(coinCount < 90) {
				coinCount++;
			} else{
				coinCount = 0;
				makeCoins();
			}

			coinRect.clear();
			for(int i = 0; i < coinX.size(); i++){
				batch.draw(coin, coinX.get(i), coinY.get(i));
				coinX.set(i, coinX.get(i) - level);
				coinRect.add(new Rectangle(coinX.get(i), coinY.get(i) , coin.getWidth(), coin.getHeight()));
			}

			velocity += gravity;
			birdY -= velocity;

			if(birdY <= 0) {
				birdY = 0;
			}

		}
		else if(gameState == 0) {   	// waiting for user touch

			if (Gdx.input.justTouched()) {
				gameState = 1;
			}

		}
		else if(gameState == 2){		// bomb collided
			if (Gdx.input.justTouched()) {
				gameState = 1;
				birdY = Gdx.graphics.getHeight() / 2;
				velocity = 0;
				score = 0;
				coinX.clear();
				coinY.clear();
				coinRect.clear();
				coinCount = 0;
				bombX.clear();
				bombY.clear();
				bombRect.clear();
				bombCount = 0;
			}
		}



		//Jump If touched
		if(Gdx.input.justTouched()){
			velocity = -10;
		}

		//GRAVITY
		if(pause < 6) pause++;
		else {
			pause = 0;
			if (birdState < 3) {
				birdState++;
			} else {
				birdState = 0;
			}
		}

		if(gameState == 2){
			batch.draw(hit, Gdx.graphics.getWidth() / 2 - 300, birdY);
		}else {
			batch.draw(bird[birdState], Gdx.graphics.getWidth() / 2 - 300, birdY);
		}
		birdRect = new Rectangle(Gdx.graphics.getWidth()/2 - 300, birdY, bird[birdState].getWidth(), bird[birdState].getHeight());

		for(int i = 0; i < coinRect.size(); i++){
			if(Intersector.overlaps(birdRect, coinRect.get(i))){
				score++;

				coinRect.remove(i);
				coinY.remove(i);
				coinX.remove(i);
				break;
			}
		}

		for(int i = 0; i < bombRect.size(); i++){
			if(Intersector.overlaps(birdRect, bombRect.get(i))){
				gameState = 2;
			}
		}
		font.draw(batch, String.valueOf(score), 50, Gdx.graphics.getHeight() - 50);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
