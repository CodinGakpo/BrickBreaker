package com.example.brickbreaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.text.method.Touch;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class GameView extends View {
    long currentTime = System.currentTimeMillis();
    private static final int INITIAL_VELOCITY_X = 15;
    private static final int INITIAL_VELOCITY_Y = 32;
    private static final int TEXT_SIZE = 120;
    private static final int BRICK_ROWS = 10;    //4
    private static final int BRICK_COLUMNS = 10; //6
    private static final float BALL_INITIAL_Y_POSITION = 2.5f;
    private static final int PADDLE_Y_POSITION_RATIO = 4;
    private static final int LIFE_MAX = 3;
    private static final long UPDATE_MS = 8;

    private Context context;
    private float ballX, ballY;
    private Velocity velocity = new Velocity(INITIAL_VELOCITY_X, INITIAL_VELOCITY_Y);
    private Handler handler;
    private Runnable runnable;
    private Paint textPaint = new Paint();
    private Paint healthPaint = new Paint();
    private Paint brickPaint = new Paint();
    private Paint hard_brickPaint = new Paint();
    private Paint brickPaint1 = new Paint();
    private float paddleX, paddleY;
    private float oldX, oldPaddleX;
    private int points = 0;
    private int life = LIFE_MAX;
    private Bitmap heart;
    private Bitmap ball, paddle, BG;
    private int dWidth, dHeight;
    private int ballWidth;
    private Random random;
    private Brick[] bricks = new Brick[BRICK_ROWS * BRICK_COLUMNS];
    int numBricks = 0;
    int tempLives = 0;
    private int brokenBricks = 0;
    private boolean gameOver = false;
    private boolean isPaused = false;

    public GameView(Context context) {
        super(context);
        this.context = context;
        //ball = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.smolball);
        paddle = BitmapFactory.decodeResource(getResources(), R.drawable.pad);
        //BG = BitmapFactory.decodeResource(getResources(), R.drawable.forestbg);
        //pause = BitmapFactory.decodeResource(getResources(),R.drawable.pup);

        handler = new Handler();
        runnable = this::invalidate;

        textPaint.setColor(Color.RED);
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);

        //healthPaint.setColor(Color.GREEN);
        brickPaint.setColor(Color.argb(1.0f, 1.0f, 0.8f, 0.6f));
        hard_brickPaint.setColor(Color.argb(1.0f, .24f, 0.44f, 0.22f));

        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;

        random = new Random();
        ballX = random.nextInt(dWidth - 50);
        ballY = dHeight / BALL_INITIAL_Y_POSITION;
        paddleY = dHeight - (dHeight / (PADDLE_Y_POSITION_RATIO + 10));
        paddleX = (dWidth - paddle.getWidth()) / 2;

        ballWidth = ball.getWidth();
        createBricks();
    }
    private void createBricks() {
        int brickWidth = dWidth / (BRICK_COLUMNS);
        int brickHeight = dHeight / 32;
        for (int column = 0; column < BRICK_COLUMNS; column++) {
            for (int row = 2; row < BRICK_ROWS + 3; row++) {
                if(row == 4 || column == 2)
                {
                    continue;
                }
                else
                {
                bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight, 1);
                if(row == column + 3 || row == BRICK_COLUMNS - column + 2){
                    bricks[numBricks].BrickLife = 2;
                    tempLives++;
                }
                numBricks++;
                }

            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap BG1 = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(),R.drawable.ssbg)),dWidth,dHeight + 250,false);
        canvas.drawBitmap(BG1, 0, 0, null);
        Bitmap pause1 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.pup),270,258,false);
        canvas.drawBitmap(pause1,350,-60,null);
        if(isPaused)
        {
            handler.postDelayed(runnable,UPDATE_MS);
            return;
        }

        ballX += velocity.getX();
        ballY += velocity.getY();

        if ((ballX >= dWidth - ball.getWidth()) || ballX <= 0) {
            velocity.setX(velocity.getX() * -1);
        }

        if (ballY <= 0) {
            velocity.setY(velocity.getY() * -1);
        }

        if (ballY > paddleY + paddle.getHeight()) {
            ballX = random.nextInt(dWidth - ball.getWidth());
            ballY = dHeight / BALL_INITIAL_Y_POSITION;
            velocity.setY(12);
            life--;

            if (life == 0) {
                gameOver = true;
                launchGameOver();
            }
        }

        if (((ballX + ball.getWidth()) >= paddleX) &&
                (ballX <= (paddleX + paddle.getWidth())) &&
                ((ballY + ball.getHeight()) >= paddleY) &&
                ((ballY + ball.getHeight()) <= paddleY + paddle.getHeight())) {
            //velocity.setX(velocity.getX());
            velocity.setY(velocity.getY() * -1);
            float hitPoint = (ballX + ballWidth / 2 - paddleX) / paddle.getWidth() - 0.5f;
            velocity.setX(velocity.getX() + (int)(hitPoint * 50)); // 1 is a multiplier for impact sensitivity
        }
        //Bitmap ball = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.roundball),oldball.getWidth(),oldball.getHeight(),false);
        canvas.drawBitmap(ball, ballX, ballY, null);
        canvas.drawBitmap(paddle, paddleX, paddleY, null);

        for (int i = 0; i < numBricks; i++) {
            if (bricks[i].getVisibility()) {
                if(bricks[i].BrickLife == 1)
                    brickPaint1 = brickPaint;
                else
                    brickPaint1 = hard_brickPaint;
                canvas.drawRect(
                        bricks[i].column * bricks[i].width + 1,
                        bricks[i].row * bricks[i].height + 1,
                        bricks[i].column * bricks[i].width + bricks[i].width - 1,
                        bricks[i].row * bricks[i].height + bricks[i].height - 1,
                        brickPaint1
                );
            }
        }

        canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);

        /*if (life == 2) {
            healthPaint.setColor(Color.YELLOW);
        } else if (life == 1) {
            healthPaint.setColor(Color.RED);
        }*/
        //canvas.drawRect(dWidth - 200, 30, dWidth - 200 + 60 * life, 80, healthPaint);
        Bitmap heart = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.heart),80,80,false);
        for (int i = 0; i < life; i++) {
            canvas.drawBitmap(heart, dWidth - 200 + 55 * i, 30, null);
        }

        for (int i = 0; i < numBricks; i++) {
            if (bricks[i].getVisibility()) {

                if (ballX + ballWidth >= (bricks[i].column * bricks[i].width) &&
                        ballX <= (bricks[i].column * bricks[i].width + bricks[i].width) &&
                        ballY + ballWidth >= (bricks[i].row * bricks[i].height) &&
                        ballY <= (bricks[i].row * bricks[i].height + bricks[i].height)) {

                    // Determine if the collision is horizontal or vertical
                    boolean hitFromTopOrBottom = ballY + ballWidth - velocity.getY() <= bricks[i].row * bricks[i].height ||
                            ballY - velocity.getY() >= bricks[i].row * bricks[i].height + bricks[i].height;
                    boolean hitFromSides = ballX + ballWidth - velocity.getX() <= bricks[i].column * bricks[i].width ||
                            ballX - velocity.getX() >= bricks[i].column * bricks[i].width + bricks[i].width;

                    if (hitFromTopOrBottom) {
                        velocity.setY(velocity.getY() * -1);
                    } else if (hitFromSides) {
                        velocity.setX(velocity.getX() * -1);
                    }
                    if (bricks[i].BrickLife == 1) {
                        bricks[i].setInvisible();
                    } else {
                        bricks[i].BrickLife--;
                    }
                    points += 10;
                    brokenBricks++;
                }
            }
        }


        if (brokenBricks == numBricks + tempLives) {
            gameOver = true;
            launchGameOver();
        }

        if (!gameOver) {
            handler.postDelayed(runnable, UPDATE_MS);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
                if (touchX >= 350 && touchX <= 350 + 270 && touchY >= -60 && touchY <= -60 + 258) {
                    isPaused = !isPaused;
                    return true;
                }
            if (touchY >= paddleY && !isPaused) {
                oldX = event.getX();
                oldPaddleX = paddleX;
            }
        } else if (action == MotionEvent.ACTION_MOVE && !isPaused) {
            float targetX = oldPaddleX - (oldX - touchX);
            if (targetX + paddle.getWidth() < 0) {

                paddleX = dWidth - paddle.getWidth()/2;
            } else if (targetX > dWidth) {
                paddleX = -paddle.getWidth()/2;
            } else {
                paddleX = targetX;
            }
        }
        return true;
    }


    private void launchGameOver() {
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra("points", points);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

}
