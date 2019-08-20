package geen.lou.videoad.admedia;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import geen.lou.videoad.HandlerUtil;
import geen.lou.videoad.R;
import geen.lou.videoad.VideoDownloadUtils;
import geen.lou.videoadmodule.IjkVideoView;
import geen.lou.videoadmodule.PlayerConfig;
import geen.lou.videoadmodule.listener.ControllerListener;
import geen.lou.videoadmodule.listener.VideoListener;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MediaAdView extends FrameLayout implements ViewTreeObserver.OnWindowFocusChangeListener {

    private float modiRatio = 16 / 9f;
    public int sSnippicWidth;
    private MediaViewCallBack callBack;
    private IjkVideoView ijkVideoView;
    private boolean isPlayed = false;
    private boolean isFinishing = false;

    protected ImageView volume, playButton, imageThumb;

    private RelativeLayout playerPlace;
    private TextView adSourceName;
    private TextView btnAd;
    private TextView adTitle;
    private boolean voiceOff = true;
    private String adId;
    private DemoAdBean mediaAdBean;
    private Context mContext;
    private ImageView imageViewPlay;
    private String from;
    private LinearLayout adbottom;

    private int playCount = 0;

    private boolean hasUploadAdInfo;
    private View layoutView;

    private boolean clickPause = false;


    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);


    public interface MediaViewCallBack {

        void initFinish(IjkVideoView ijkVideoView);

        void initFail();
    }

    public void setMediaAdViewCallBack(MediaViewCallBack mediaAdViewCallBack) {
        this.callBack = mediaAdViewCallBack;
    }

    public MediaAdView(Context context, String adId, String from) {
        super(context);
        this.adId = adId;
        this.mContext = context;
        this.from = from;
        addVisibleListener();
    }

    private void clickAd(Context context) {
        if (ijkVideoView != null && ijkVideoView.isPlaying()) {
            ijkVideoView.pause();
        }
        Intent intent = new Intent();
        //上报点击信息
        AdMediaRequest.getInstance().clickAdInfoTask(mediaAdBean, adId, from, playCount, "4", context);
    }

    public void initView(Context context, DemoAdBean mediaAdBean) {

        sSnippicWidth = getScreenW();

        this.mediaAdBean = mediaAdBean;

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        layoutView = layoutInflater.inflate(R.layout.layout_ad, this);


        ijkVideoView = layoutView.findViewById(R.id.player);
        volume = layoutView.findViewById(R.id.iv_volume);
        playButton = layoutView.findViewById(R.id.iv_play);
        imageThumb = layoutView.findViewById(R.id.imageThumb);
        imageViewPlay = layoutView.findViewById(R.id.iv_cover_play);
        adbottom = layoutView.findViewById(R.id.adbottom);

        adSourceName = layoutView.findViewById(R.id.ad_source_name);
        btnAd = layoutView.findViewById(R.id.btnAd);
        adTitle = layoutView.findViewById(R.id.native_ad_title);
        adTitle.setText(mediaAdBean.getAds_title());


        if (!TextUtils.isEmpty(mediaAdBean.getAds_button())) {
            btnAd.setVisibility(View.VISIBLE);
            btnAd.setText(mediaAdBean.getAds_button());
        }

        if (!TextUtils.isEmpty(mediaAdBean.getAdvertiser_name())) {
            adSourceName.setVisibility(View.VISIBLE);
            adSourceName.setText(mediaAdBean.getAdvertiser_name());
        }

        RelativeLayout relativePlayer = layoutView.findViewById(R.id.relativePlayer);
        playerPlace = layoutView.findViewById(R.id.playerPlace);

        ViewGroup.LayoutParams params = relativePlayer.getLayoutParams();
        params.width = sSnippicWidth;
        params.height = (int) (sSnippicWidth * 1.0f / modiRatio);
        relativePlayer.setLayoutParams(params);

        //图片也设置
        ViewGroup.LayoutParams imageParams = imageThumb.getLayoutParams();
        imageParams.width = params.width;
        imageParams.height = params.height;
        imageThumb.setLayoutParams(imageParams);

        Glide.with(context.getApplicationContext()).load(mediaAdBean.getCover_image_url()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageThumb);


        DemoAdController adController = new DemoAdController(context);
        ijkVideoView.setPlayerConfig(new PlayerConfig.Builder()
                .useAndroidMediaPlayer()
                .build());
        //添加播放器失败回调
        ijkVideoView.setVideoListener(new VideoListener() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onPrepared() {
            }

            @Override
            public void onError() {
                //todo 广告播放失败
                if (callBack != null) {
                    callBack.initFail();
                }
            }

            @Override
            public void onInfo(int what, int extra) {
            }
        });
        ijkVideoView.setVideoController(adController);
        adController.setControllerListener(new ControllerListener() {

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void adPlaying() {
                isPlayed = true;
                isFinishing = false;

                playButton.setImageResource(R.mipmap.dkplayer_ic_action_pause);
                playerPlace.setVisibility(View.GONE);
                ijkVideoView.setVisibility(View.VISIBLE);
                imageViewPlay.setVisibility(View.GONE);
            }

            @Override
            public void adVideoPause() {
                playButton.setImageResource(R.mipmap.dkplayer_ic_action_play_arrow);
            }

            @Override
            public void adVideoFinish() {
                playButton.setImageResource(R.mipmap.dkplayer_ic_action_play_arrow);
                playerPlace.setVisibility(View.VISIBLE);
                ijkVideoView.setVisibility(View.INVISIBLE);
                //imageViewPlay.setVisibility(View.VISIBLE);
                isFinishing = true;
                playCount++;
            }
        });

        volume.setOnClickListener(v -> {
            if (ijkVideoView != null) {
                if (!voiceOff) {
                    ijkVideoView.setVoiceOff();
                    volume.setImageResource(R.mipmap.dkplayer_ic_action_volume_off);
                    voiceOff = true;
                } else {
                    ijkVideoView.setVoiceOpen();
                    volume.setImageResource(R.mipmap.dkplayer_ic_action_volume_up);
                    voiceOff = false;
                }
            }
        });

        imageViewPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ijkVideoView.start();
                HandlerUtil.sUiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //1 s
                        playerPlace.setVisibility(View.GONE);
                        ijkVideoView.setVisibility(View.VISIBLE);
                    }
                }, 500);
            }
        });


        playButton.setOnClickListener(v -> {
            if (ijkVideoView != null) {
                if (ijkVideoView.isPlaying()) {
                    ijkVideoView.pause();
                    clickPause = true;
                    // playButton.setImageResource(R.mipmap.dkplayer_ic_action_play_arrow);
                } else {
                    clickPause = false;
                    ijkVideoView.start();
                    // playButton.setImageResource(R.mipmap.dkplayer_ic_action_pause);
                    if (voiceOff) {
                        ijkVideoView.setVoiceOff();
                    } else {
                        ijkVideoView.setVoiceOpen();
                    }
                    HandlerUtil.sUiHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //1 s
                            playerPlace.setVisibility(View.GONE);
                            ijkVideoView.setVisibility(View.VISIBLE);
                        }
                    }, 500);

                }
            }
        });

        adTitle.setOnClickListener(v -> clickAd(context));

        adbottom.setOnClickListener(v -> clickAd(context));

        if (!TextUtils.isEmpty(VideoDownloadUtils.checkLocalVideoIsExistByUrl(mediaAdBean.getMaterial_url()))) {
            //已经下载过了
            if (callBack != null) {
                HandlerUtil.sUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ijkVideoView.setUrl(VideoDownloadUtils.getVideoPlayLocalPath(mediaAdBean.getMaterial_url()));
                        callBack.initFinish(ijkVideoView);
                    }
                });
            }
        } else {
            //先下载视频
            VideoDownloadUtils.downloadVideoInBackground(mediaAdBean.getMaterial_url(), new FileDownloadLargeFileListener() {
                @Override
                protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                }

                @Override
                protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                }

                @Override
                protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                }

                @Override
                protected void completed(BaseDownloadTask task) {
                    HandlerUtil.sUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ijkVideoView.setUrl(VideoDownloadUtils.getVideoPlayLocalPath(mediaAdBean.getMaterial_url()));
                            if (callBack != null) {
                                callBack.initFinish(ijkVideoView);
                            }
                        }
                    });

                }

                @Override
                protected void error(BaseDownloadTask task, Throwable e) {

                    HandlerUtil.sUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.initFail();
                            }
                        }
                    });
                }

                @Override
                protected void warn(BaseDownloadTask task) {

                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        inVisibleDo();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }


    public void visibleDo() {
        if (ijkVideoView != null) {
            if (clickPause) {
                return;
            }
            if (!isFinishing && !ijkVideoView.isPlaying()) {
                ijkVideoView.start();
            }
            if (voiceOff) {
                ijkVideoView.setVoiceOff();
                volume.setImageResource(R.mipmap.dkplayer_ic_action_volume_off);
            } else {
                ijkVideoView.setVoiceOpen();
                volume.setImageResource(R.mipmap.dkplayer_ic_action_volume_up);
            }
        }
    }

    public void inVisibleDo() {
        if (ijkVideoView != null) {
            playerPlace.setVisibility(View.VISIBLE);
            ijkVideoView.setVoiceOneOff();
            if (ijkVideoView.isPlaying()) {
                ijkVideoView.pause();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            Log.d("danxx--可见", "onWindowFocusChanged" + "---" + this.hashCode() + isVisible());
        } else {
            Log.d("danxx--不可见", "onWindowFocusChanged" + "---" + this.hashCode());
            inVisibleDo();
        }
    }

    private boolean isVisible() {
        return getGlobalVisibleRect(new Rect());
    }

    /***
     * 每500ms检测屏幕的可见性
     */
    private void addVisibleListener() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {

            boolean isVisible = isVisible(layoutView);
            if (isVisible) {
                //可见的时候 判断是否在播放
                upLoadAdInfo();
                //todo 尝试判断是否续播
                HandlerUtil.sUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        visibleDo();
                    }
                });

            } else {
                HandlerUtil.sUiHandler.post(() -> inVisibleDo());
            }
        }, 100, 500, TimeUnit.MILLISECONDS);
    }

    private void upLoadAdInfo() {
        if (!hasUploadAdInfo) {
            hasUploadAdInfo = true;
            AdMediaRequest.getInstance().uploadAdInfoTask(mediaAdBean, adId, mContext);
        }
    }

    public boolean isVisible(final View view) {
        if (view == null) {
            return false;
        }
        if (!view.isShown()) {
            return false;
        }
        final Rect actualPosition = new Rect();
        view.getGlobalVisibleRect(actualPosition);
        final Rect screen = new Rect(0, 0, getScreenW(), getScreenH());
        return actualPosition.intersect(screen);
    }

    /**
     * 获取屏幕宽度
     *
     * @return 屏幕宽度
     */
    public int getScreenW() {

        DisplayMetrics dm = getDisplayMetrics();
        int w = dm.widthPixels;
        return w;

    }

    /**
     * 获取屏幕高度
     *
     * @return 屏幕高度
     */
    public int getScreenH() {
        DisplayMetrics dm = getDisplayMetrics();
        int h = dm.heightPixels;
        return h;

    }

    private DisplayMetrics getDisplayMetrics() {
        return mContext.getResources().getDisplayMetrics();
    }

}
