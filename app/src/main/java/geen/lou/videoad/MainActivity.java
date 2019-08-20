package geen.lou.videoad;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.liulishuo.filedownloader.FileDownloader;

import geen.lou.videoad.admedia.DemoAdBean;
import geen.lou.videoad.admedia.MediaAdView;
import geen.lou.videoadmodule.IjkVideoView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout rootAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileDownloader.setupOnApplicationOnCreate(this.getApplication());

        PermissionsUtils.requestAppPermission(this);

        rootAd = findViewById(R.id.rootAd);

        if(PermissionsUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            DemoAdBean mediaAdBean = new DemoAdBean();

            mediaAdBean.setAds_title("test");

            mediaAdBean.setMaterial_url("http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4");

            mediaAdBean.setMaterial_id("mock");

            MediaAdView mediaAdView = new MediaAdView(this,"1","test");

            mediaAdView.setMediaAdViewCallBack(new MediaAdView.MediaViewCallBack() {
                @Override
                public void initFinish(IjkVideoView ijkVideoView) {

                    rootAd.removeAllViews();

                    rootAd.addView(mediaAdView);
                }

                @Override
                public void initFail() {

                }
            });

            mediaAdView.initView(this,mediaAdBean);
        }else{
            PermissionsUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 100, new PermissionsUtils.PermissionCallBack() {
                @Override
                public void hasPermission() {

                }

                @Override
                public void noPermission() {

                }
            });
        }
    }


}
