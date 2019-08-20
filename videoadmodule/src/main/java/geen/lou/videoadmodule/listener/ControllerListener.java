package geen.lou.videoadmodule.listener;

/**
 *
 * @author Devlin_n
 * @date 2017/6/26
 */

public interface ControllerListener {

    void onProgress(int progress);

    void adPlaying();

    void adVideoPause();

    void adVideoFinish();



}
