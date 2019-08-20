package geen.lou.videoad.admedia;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import geen.lou.videoad.R;
import geen.lou.videoadmodule.BaseVideoController;
import geen.lou.videoadmodule.IjkVideoView;
import geen.lou.videoadmodule.listener.ControllerListener;

/***
 * 广告控制器 一些Ui
 */
public class DemoAdController extends BaseVideoController implements View.OnClickListener {
    protected ControllerListener listener;

    public DemoAdController(@NonNull Context context) {
        super(context);
    }

    public DemoAdController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ad_content;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            case IjkVideoView.STATE_PLAYING:
                post(mShowProgress);
                if(listener!=null){
                    listener.adPlaying();
                }
                break;
            case IjkVideoView.STATE_PAUSED:
                if(listener!=null){
                    listener.adVideoPause();
                }
                break;


            case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                if(listener!=null){
                    listener.adVideoFinish();
                }

                break;
            default:

                break;
        }
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        switch (playerState) {
            case IjkVideoView.PLAYER_NORMAL:
                break;
            case IjkVideoView.PLAYER_FULL_SCREEN:

                break;
                default:
                    break;
        }
    }

    @Override
    protected int setProgress() {
        if (mediaPlayer == null) {
            return 0;
        }
        int position = (int) mediaPlayer.getCurrentPosition();
        int duration = (int) mediaPlayer.getDuration();
        if(listener!=null){
            listener.onProgress(position);
        }
        return position;
    }

    @Override
    public boolean onBackPressed() {

        return super.onBackPressed();
    }

    @Override
    public void setControllerListener(ControllerListener listener) {
        this.listener = listener;
    }
}
