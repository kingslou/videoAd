package geen.lou.videoad;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 *
 * @author Shey
 * @date 16/7/15
 */

public class HandlerUtil {

    public static Handler sUiHandler = new Handler(Looper.getMainLooper());

    private static HandlerThread sHandlerThread = new HandlerThread("Background Thread");

    static {
        sHandlerThread.start();
    }

    public static Handler sBgHandler = new Handler(sHandlerThread.getLooper());
}
