package geen.lou.videoadmodule;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import geen.lou.videoadmodule.listener.ControllerListener;
import geen.lou.videoadmodule.listener.MediaPlayerControl;

/**
 * 控制器基类
 *
 * @author Devlin_n
 * @date 2017/4/12
 */

public abstract class BaseVideoController extends FrameLayout {

    protected View controllerView;//控制器视图
    protected MediaPlayerControl mediaPlayer;//播放器
    protected boolean mShowing;//控制器是否处于显示状态
    protected boolean isLocked;
    protected int sDefaultTimeout = 3000;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    protected ControllerListener listener;
    protected int currentPlayState;
    public Context mContext;


    public BaseVideoController(@NonNull Context context) {
        this(context, null);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    protected void initView() {
        controllerView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        setClickable(true);
        setFocusable(true);
    }

    /**
     * 设置控制器布局文件，子类必须实现
     */
    protected abstract int getLayoutId();

    /**
     * 显示
     */
    public void show() {
    }

    /**
     * 隐藏
     */
    public void hide() {
    }

    public void setPlayState(int playState) {
        currentPlayState = playState;
    }

    public void setPlayerState(int playerState) {
    }

    public void setControllerListener(ControllerListener listener) {
        this.listener = listener;
    }

    protected void doPauseResume() {
        if (currentPlayState == IjkVideoView.STATE_BUFFERING) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }



    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mediaPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    protected int setProgress() {
        return 0;
    }

    /**
     * 获取当前系统时间
     */
    protected String getCurrentSystemTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    protected String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(mShowProgress);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            post(mShowProgress);
        }
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        return false;
    }

    public void setMediaPlayer(MediaPlayerControl mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
