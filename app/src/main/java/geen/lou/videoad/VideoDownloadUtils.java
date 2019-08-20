package geen.lou.videoad;
import android.net.Uri;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;


/**
 * @author youtui
 */
public class VideoDownloadUtils {

    public static void downloadVideoInBackground(String url, FileDownloadListener fileDownloadListener){
        String adFileName = url;
        adFileName = adFileName.replace("/","");
        adFileName = adFileName.replace(":","");
        String fileName = AppConfig.getAdVideoSaveDir().getAbsolutePath()+"/"+adFileName;
        FileDownloader.getImpl().create(url).setPath(fileName).setForceReDownload(true).setListener(fileDownloadListener).start();
    }

    /***
     * 检查本地是否有下载
     * @param url
     * @return
     */
    public static String checkLocalVideoIsExistByUrl(String url) {
        if (AppConfig.getAdVideoSaveDir()== null) {
            return "";
        }
        String adFileName = url;
        adFileName = adFileName.replace("/","");
        adFileName = adFileName.replace(":","");
        String fileName = AppConfig.getAdVideoSaveDir().getAbsolutePath() + "/" + adFileName;
        File thisFile = new File(fileName);
        return thisFile.exists() ? thisFile.getAbsolutePath() : "";
    }

    /***
     * 拼装本地文件播放路径
     * @param url
     * @return
     */
    public static String getVideoPlayLocalPath(String url){
        String realPath = "";
        String localcache = checkLocalVideoIsExistByUrl(url);
        File file = new File(localcache);
        if (file.exists()) {
            realPath = Uri.parse("file://" + file.getAbsolutePath()).toString();
        }
        return realPath;
    }

    final static FileDownloadListener queueTarget = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {

        }

        @Override
        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {

        }

        @Override
        protected void completed(BaseDownloadTask task) {

        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {

        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    };

}
