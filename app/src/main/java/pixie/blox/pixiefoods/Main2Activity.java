package pixie.blox.pixiefoods;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.Timer;
import java.util.TimerTask;

public class Main2Activity extends AppCompatActivity {


    HomeWatcher mHomeWatcher;


    private ImageView box;
    private ImageView orange;
    private ImageView black;
    private ImageView black2;
    private ImageView pink;
    private TextView scoreLabel;
    private FrameLayout frame;
    private Button pauseBtn;
    private Button tryAgainButton;
    private boolean action_left, action_right;
    private Button BackButton;
    private Drawable pixieGameOver;


    private int frameHeight;
    public int frameWidth;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;

    private int orangeX;
    private int orangeY;
    private float pinkY;
    private float pinkX;
    private int blackX;
    private int blackY;
    private int black2X;
    private int black2Y;


    private int boxY;
    private int score = 0;

    private boolean isGameOver = false;
    private Handler handler = new Handler();
    private Timer timer = new Timer();


    private boolean action_flg = false;
    private boolean start_flg = false;

    int count = 0;
    Thread t;

    private boolean pause_flg = false;

    Animation topAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        box = (ImageView) findViewById(R.id.box);
        black = (ImageView) findViewById(R.id.black);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        frame = findViewById(R.id.frame);
        pauseBtn = (Button) findViewById(R.id.pauseBtn);
        black2 = (ImageView) findViewById(R.id.black2);
        tryAgainButton = (Button) findViewById(R.id.button);
        BackButton = (Button) findViewById(R.id.BackButton);
        pixieGameOver = getResources().getDrawable(R.drawable.pixiegameover);

        boxSize = box.getHeight();

        black.setX(-400);
        black.setY(-400);
        black2.setX(-400);
        black2.setY(-400);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);

        scoreLabel.setText("Score : 0 ");

        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        tryAgainButton.setVisibility(View.INVISIBLE);

        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }

            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();
    }

    public void pausePushed(View view) {
        if (isGameOver) return;
        if (pause_flg == false) {

            pause_flg = true;
            if (timer != null)
                timer.cancel();
            timer = null;

            pauseBtn.setText("CONTINUE");
        } else {

            pause_flg = false;

            pauseBtn.setText("PAUSE");

            startGame();
        }
    }

    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        stopService(music);
    }

    public void changePos() {

        if (hitCheck()) {
            onHit();
        }

        blackX -= 12;
        if (blackX < 0) {
            blackX = screenWidth + 20;
            blackY = (int) Math.floor(Math.random() * (frameHeight - black.getHeight()));
            score += 1;
        }
        black.setX(blackX);
        black.setY(blackY);

        black2X -= 12;
        if (black2X < 0) {
            black2X = screenWidth + 20;
            black2Y = (int) Math.floor(Math.random() * (frameHeight - black2.getHeight()));

        }
        black2.setX(black2X);
        black2.setY(black2Y);

        blackX -= 12;
        if (blackX < 0) {
            blackX = screenWidth + 20;
            blackY = (int) Math.floor(Math.random() * (frameHeight - black.getHeight()));

        }
        black.setX(blackX);
        black.setY(blackY);

        black2X -= 12;
        if (black2X < 0) {
            black2X = screenWidth + 20;
            black2Y = (int) Math.floor(Math.random() * (frameHeight - black2.getHeight()));

        }
        black2.setX(black2X);
        black2.setY(black2Y);


        if (action_flg == true) {
            boxY -= 20;

        } else {
            boxY += 20;
        }

        if (boxY < 0) boxY = 0;

        if (boxY > frameHeight - boxSize) boxY = frameHeight - boxSize;

        box.setY(boxY);

        scoreLabel.setText("Score : " + score);
    }

    public boolean hitCheck() {
        return checkBlack(black) || checkBlack(black2);
    }

    private boolean checkBlack(ImageView blackImage) {
        int centerX = (int)(blackImage.getWidth() / 2 + blackImage.getX());
        int centerY = (int)(blackImage.getHeight() / 2 + blackImage.getY());
        if (0 <= centerX && centerX <= boxSize &&
                boxY <= centerY && centerY <= boxY + boxSize) {
            return true;
        }
        return false;
    }

    private void onHit() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;

        tryAgainButton.setVisibility(View.VISIBLE);
        Animation anim = getAnim();
        tryAgainButton.setAnimation(topAnim);
        tryAgainButton.setAnimation(anim);
        box.setImageDrawable(pixieGameOver);
        isGameOver = true;
    }

    private Animation getAnim() {
        Animation anim = new ScaleAnimation(
                1f, 1.2f,
                1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(1000);

        return anim;
    }

    public boolean onTouchEvent(MotionEvent me) {

        if (start_flg == false) {

            start_flg = true;

            FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
            frameHeight = frame.getHeight();
            frameWidth = frame.getWidth();

            boxY = (int) box.getY();

            boxSize = box.getHeight();

            startGame();
        } else {
            if (me.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;

            } else if (me.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }
        return true;
    }

    private void startGame() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changePos();
                    }
                });
            }
        }, 0, 20);
        isGameOver = false;
    }

    public void tryAgain(View view) {
        startActivity(new Intent(getApplicationContext(), Main2Activity.class));
    }

    public void mainMenu(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}


