package geen.lou.videoad;

import android.os.Environment;

import java.io.File;

/**
 * @author youtui-geen
 * @date 2018/7/25
 * @desc
 */
public class AppConfig {

    public static File getAdVideoSaveDir(){
        try{
            File f = new File(Environment.getExternalStorageDirectory() + "/AdDemo/");
            if (!f.exists()) {
                f.mkdirs();
            }
            return f ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
