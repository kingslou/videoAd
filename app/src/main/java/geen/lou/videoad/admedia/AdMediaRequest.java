package geen.lou.videoad.admedia;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import geen.lou.videoadmodule.IjkVideoView;

/***
 * Media 广告请求
 */
public class AdMediaRequest {

    private String from;

    public interface MediaAdCallBack{
        void loadSuccess(DemoAdBean mediaAdBean, View AdView, IjkVideoView ijkVideoView);

        void loadFail();
    }

    private static class AdLoadHolder {
        private static final AdMediaRequest INSTANCE = new AdMediaRequest();
    }

    public static AdMediaRequest getInstance() {
        return AdMediaRequest.AdLoadHolder.INSTANCE;
    }


    public void loadAd(String adId, Activity context, MediaAdCallBack mediaAdCallBack){
        loadAd(adId,"",context,mediaAdCallBack);
    }

    public void loadAd(String adId, String from, Activity context, MediaAdCallBack mediaAdCallBack){
        this.from = from;

    }

    public void clickAdInfoTask(DemoAdBean mediaAdBean, String adId, String from, int count, String duration, Context context){


    }

    public void uploadAdInfoTask(DemoAdBean mediaAdBean, String adId, Context context){


    }
}
