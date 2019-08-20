package geen.lou.videoadmodule;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.OrientationEventListener;
import android.widget.FrameLayout;
import geen.lou.videoadmodule.listener.MediaEngineInterface;
import geen.lou.videoadmodule.listener.MediaPlayerControl;
import geen.lou.videoadmodule.listener.VideoListener;


/**
 * 播放器
 * @author Devlin_n
 * @date 2017/4/7
 */

public abstract class BaseIjkVideoView extends FrameLayout implements MediaPlayerControl, MediaEngineInterface {

    protected BaseMediaEngine mMediaPlayer;//播放引擎
    protected BaseVideoController mVideoController;//控制器
    protected VideoListener listener;
    protected int bufferPercentage;//缓冲百分比
    protected boolean isMute;//是否静音

    protected String mCurrentUrl;//当前播放视频的地址
    protected long mCurrentPosition;//当前正在播放视频的位置
    protected String mCurrentTitle = "";//当前正在播放视频的标题

    //播放器的各种状态
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_BUFFERING = 6;
    public static final int STATE_BUFFERED = 7;

    public static final int STATE_Portrait = 8;


    protected int mCurrentPlayState = STATE_IDLE;//当前播放器的状态

    public static final int PLAYER_NORMAL = 10;        // 普通播放器
    public static final int PLAYER_FULL_SCREEN = 11;   // 全屏播放器
    protected int mCurrentPlayerState = PLAYER_NORMAL;

    protected AudioManager mAudioManager;//系统音频管理器

    protected AudioFocusHelper mAudioFocusHelper = new AudioFocusHelper();

    protected int currentOrientation = 0;
    protected static final int PORTRAIT = 1;
    protected static final int LANDSCAPE = 2;
    protected static final int REVERSE_LANDSCAPE = 3;

    protected boolean isLockFullScreen;//是否锁定屏幕
    protected PlayerConfig mPlayerConfig;//播放器配置

    public final int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    public final int MEDIA_INFO_BUFFERING_START = 701;
    public final int MEDIA_INFO_BUFFERING_END = 702;

    /**
     * 加速度传感器监听
     */
    protected OrientationEventListener orientationEventListener = new OrientationEventListener(getContext()) { // 加速度传感器监听，用于自动旋转屏幕
        @Override
        public void onOrientationChanged(int orientation) {
            if (mVideoController == null) {
                return;
            }
            Activity activity = WindowUtil.scanForActivity(mVideoController.getContext());
            if (activity == null) {
                return;
            }
            if (orientation >= 340) { //屏幕顶部朝上
                onOrientationPortrait(activity);
            } else if (orientation >= 260 && orientation <= 280) { //屏幕左边朝上
                onOrientationLandscape(activity);
            } else if (orientation >= 70 && orientation <= 90) { //屏幕右边朝上
                onOrientationReverseLandscape(activity);
            }
        }
    };

    /**
     * 竖屏
     */
    protected void onOrientationPortrait(Activity activity) {
        if (isLockFullScreen || !mPlayerConfig.mAutoRotate || currentOrientation == PORTRAIT) {
            return;
        }
        if ((currentOrientation == LANDSCAPE || currentOrientation == REVERSE_LANDSCAPE) && !isFullScreen()) {
            currentOrientation = PORTRAIT;
            return;
        }
        currentOrientation = PORTRAIT;

        try {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopFullScreen();
    }

    /**
     * 横屏
     */
    protected void onOrientationLandscape(Activity activity) {
        if (currentOrientation == LANDSCAPE) {
            return;
        }
        if (currentOrientation == PORTRAIT && isFullScreen()) {
            currentOrientation = LANDSCAPE;
            return;
        }
        currentOrientation = LANDSCAPE;
        if (!isFullScreen()) {
            startFullScreen();
        }
        if (isFullScreen()) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

    }

    /**
     * 反向横屏
     */
    protected void onOrientationReverseLandscape(Activity activity) {
        if (currentOrientation == REVERSE_LANDSCAPE) {
            return;
        }
        if (currentOrientation == PORTRAIT && isFullScreen()) {
            currentOrientation = REVERSE_LANDSCAPE;
            return;
        }
        currentOrientation = REVERSE_LANDSCAPE;
        if (!isFullScreen()) {
            startFullScreen();
        }
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    }

    public BaseIjkVideoView( Context context) {
        this(context, null);
    }


    public BaseIjkVideoView( Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseIjkVideoView( Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAudioManager = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    protected void initPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new AndroidMediaEngine();
            ((AndroidMediaEngine)mMediaPlayer).setMediaEngineInterface(this);
            mMediaPlayer.initPlayer();
        }
    }

    protected abstract void setPlayState(int playState);

    protected abstract void setPlayerState(int playerState);

    /**
     * 开始准备播放（直接播放）
     */
    protected void startPrepare() {
        if (mCurrentUrl == null || TextUtils.isEmpty(mCurrentUrl.trim())) {
            return;
        }
        mMediaPlayer.reset();
        mMediaPlayer.setLooping(mPlayerConfig.isLooping);
        try {
            if (mPlayerConfig.isCache) {
            } else {
                mMediaPlayer.setDataSource(mCurrentUrl);
            }
            mMediaPlayer.prepareAsync();
            mCurrentPlayState = STATE_PREPARING;
            setPlayState(mCurrentPlayState);
            mCurrentPlayerState = isFullScreen() ? PLAYER_FULL_SCREEN : PLAYER_NORMAL;
            setPlayerState(mCurrentPlayerState);
        } catch (Exception e) {
            mCurrentPlayState = STATE_ERROR;
            setPlayState(mCurrentPlayState);
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (mCurrentPlayState == STATE_IDLE) {
            startPlay();
        } else if (isInPlaybackState()) {
            startInPlaybackState();
        }
        setKeepScreenOn(true);
        mAudioFocusHelper.requestFocus();
    }

    /**
     * 第一次播放
     */
    protected void startPlay() {
        if (mPlayerConfig.savingProgress) {
            mCurrentPosition = ProgressUtil.getSavedProgress(mCurrentUrl);
        }
        if (mPlayerConfig.mAutoRotate) {
            orientationEventListener.enable();
        }
        initPlayer();
        startPrepare();
    }

    /**
     * 播放状态下开始播放
     */
    protected void startInPlaybackState() {
        mMediaPlayer.start();
        mCurrentPlayState = STATE_PLAYING;
        setPlayState(mCurrentPlayState);
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentPlayState = STATE_PAUSED;
                setPlayState(mCurrentPlayState);
                setKeepScreenOn(false);
                mAudioFocusHelper.abandonFocus();
            }
        }
    }


    public void resume() {
        if (isInPlaybackState() && !mMediaPlayer.isPlaying() && mCurrentPlayState != STATE_PLAYBACK_COMPLETED) {
            mMediaPlayer.start();
            mCurrentPlayState = STATE_PLAYING;
            setPlayState(mCurrentPlayState);
            mAudioFocusHelper.requestFocus();
            setKeepScreenOn(true);
        }
    }

    public void stopPlayback() {

        if (mPlayerConfig.savingProgress && isInPlaybackState()) {
            ProgressUtil.saveProgress(mCurrentUrl, mCurrentPosition);
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mCurrentPlayState = STATE_IDLE;
            setPlayState(mCurrentPlayState);
            mAudioFocusHelper.abandonFocus();
            setKeepScreenOn(false);
        }

        orientationEventListener.disable();


        isLockFullScreen = false;
        mCurrentPosition = 0;
    }

    public void release() {
        if (mPlayerConfig.savingProgress && isInPlaybackState()) {
            ProgressUtil.saveProgress(mCurrentUrl, mCurrentPosition);
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentPlayState = STATE_IDLE;
            setPlayState(mCurrentPlayState);
            mAudioFocusHelper.abandonFocus();
            setKeepScreenOn(false);
        }
        orientationEventListener.disable();


        isLockFullScreen = false;
        mCurrentPosition = 0;
    }

    public void setVideoListener(VideoListener listener) {
        this.listener = listener;
    }

    protected boolean isInPlaybackState() {
        return (mMediaPlayer != null && mCurrentPlayState != STATE_ERROR
                && mCurrentPlayState != STATE_IDLE && mCurrentPlayState != STATE_PREPARING);
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            mCurrentPosition = (int) mMediaPlayer.getCurrentPosition();
            return (int)mCurrentPosition;
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(pos);
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mMediaPlayer != null ? bufferPercentage : 0;
    }

    /**
     * 设置静音
     */
    @Override
    public void setMute() {
        if (isMute) {
            mMediaPlayer.setVolume(1, 1);
            isMute = false;
        } else {
            mMediaPlayer.setVolume(0, 0);
            isMute = true;
        }
    }

    /***
     * 设置一次静音
     */
    public void setVoiceOneOff(){
        if(mMediaPlayer!=null){
            mMediaPlayer.setVolume(0, 0);
        }
    }

    /**
     * 设置当前播放器生命周期静音
     */
    public void setVoiceOff(){
        if(mMediaPlayer!=null){
            mMediaPlayer.setVolume(0, 0);
            isMute = true;
        }
    }

    public void setVoiceOpen(){
        if(mMediaPlayer!=null){
            mMediaPlayer.setVolume(1, 1);
            isMute = false;
        }
    }

    @Override
    public boolean isMute() {
        return isMute;
    }

    @Override
    public void setLock(boolean isLocked) {
            this.isLockFullScreen = isLocked;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public String getTitle() {
        return mCurrentTitle;
    }


    @Override
    public void onError() {
        mCurrentPlayState = STATE_ERROR;
        if (listener != null) {
            listener.onError();
        }
        setPlayState(mCurrentPlayState);
        mCurrentPosition = getCurrentPosition();
    }

    @Override
    public void onCompletion() {
        mCurrentPlayState = STATE_PLAYBACK_COMPLETED;
        if (listener != null) {
            listener.onComplete();
        }
        setPlayState(mCurrentPlayState);
        setKeepScreenOn(false);
    }

    @Override
    public void onInfo(int what, int extra) {
        if (listener != null) {
            listener.onInfo(what, extra);
        }
        switch (what) {
            case MEDIA_INFO_BUFFERING_START:
                mCurrentPlayState = STATE_BUFFERING;
                setPlayState(mCurrentPlayState);
                break;
            case MEDIA_INFO_BUFFERING_END:
                mCurrentPlayState = STATE_BUFFERED;
                setPlayState(mCurrentPlayState);
                break;
            case MEDIA_INFO_VIDEO_RENDERING_START: // 视频开始渲染
                mCurrentPlayState = STATE_PLAYING;
                setPlayState(mCurrentPlayState);
                if (getWindowVisibility() != VISIBLE) {
                    pause();
                }
                break;
                default:
                    break;
        }
    }

    @Override
    public void onBufferingUpdate(int position) {
        if (!mPlayerConfig.isCache) {
            bufferPercentage = position;
        }
    }

    @Override
    public void onPrepared() {
        mCurrentPlayState = STATE_PREPARED;
        if (listener != null) {
            listener.onPrepared();
        }
        setPlayState(mCurrentPlayState);
        if (mCurrentPosition > 0) {
            seekTo((int)mCurrentPosition);
        }
    }

    public void setPlayerConfig(PlayerConfig config) {
        this.mPlayerConfig = config;
    }

    /**
     * 获取当前播放器的状态
     */
    public int getCurrentPlayerState() {
        return mCurrentPlayerState;
    }

    /**
     * 获取当前的播放状态
     */
    public int getCurrentPlayState() {
        return mCurrentPlayState;
    }

    /**
     * 音频焦点改变监听
     */
    private class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
        boolean startRequested = false;
        boolean pausedForLoss = false;
        int currentFocus = 0;

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (currentFocus == focusChange) {
                return;
            }

            currentFocus = focusChange;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                    if (startRequested || pausedForLoss) {
                        start();
                        startRequested = false;
                        pausedForLoss = false;
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (isPlaying()) {
                        pausedForLoss = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (isPlaying()) {
                        pausedForLoss = true;
                        pause();
                    }
                    break;
                    default:
                        break;
            }
        }

        /**
         * Requests to obtain the audio focus
         *
         * @return True if the focus was granted
         */
        boolean requestFocus() {
            if (currentFocus == AudioManager.AUDIOFOCUS_GAIN) {
                return true;
            }

            if (mAudioManager == null) {
                return false;
            }

            int status = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status) {
                currentFocus = AudioManager.AUDIOFOCUS_GAIN;
                return true;
            }

            startRequested = true;
            return false;
        }

        /**
         * Requests the system to drop the audio focus
         *
         * @return True if the focus was lost
         */
        boolean abandonFocus() {

            if (mAudioManager == null) {
                return false;
            }

            startRequested = false;
            int status = mAudioManager.abandonAudioFocus(this);
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status;
        }
    }
}
