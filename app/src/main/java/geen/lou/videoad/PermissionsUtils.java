package geen.lou.videoad;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PermissionsUtils {

    public interface PermissionCallBack{

        void hasPermission();

        void noPermission();
    }

    public static boolean hasPermission(Context context,String permission){
       return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void checkPermission(Activity context,String permission,int requestCode,PermissionCallBack permissionCallBack){

        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            //todo 有权限
            permissionCallBack.hasPermission();
        }else{
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            //注：如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don’t ask again 选项，此方法将返回 false。如果设备规范禁止应用具有该权限，此方法也会返回 false
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                ActivityCompat.requestPermissions(context,new String[]{permission}, requestCode);
            }else{
                //todo 用户已经点击了不再显示，弹出自己的提示 去引导用户到设置界面
                permissionCallBack.noPermission();
            }
        }
    }

    /***
     * 在fragment 中要接收权限 需要使用fragment request
     * @param fragment
     * @param permission
     * @param requestCode
     * @param permissionCallBack
     */
    public static void checkPermission(Fragment fragment, String permission, int requestCode, PermissionCallBack permissionCallBack){
        if(fragment==null || fragment.getActivity()==null || fragment.getActivity().isFinishing()){
            return;
        }
        if (ActivityCompat.checkSelfPermission(fragment.getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
            //todo 有权限
            permissionCallBack.hasPermission();
        }else{
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            //注：如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don’t ask again 选项，此方法将返回 false。如果设备规范禁止应用具有该权限，此方法也会返回 false
            if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(), permission)) {
                fragment.requestPermissions(new String[]{permission}, requestCode);
            }else{
                //todo 用户已经点击了不再显示，弹出自己的提示 去引导用户到设置界面
                permissionCallBack.noPermission();
            }
        }
    }

    public static void requestPermission(Activity context,String permission,int requestCode,PermissionCallBack permissionCallBack){

        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            //todo 有权限
            permissionCallBack.hasPermission();
        }else{
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            //注：如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don’t ask again 选项，此方法将返回 false。如果设备规范禁止应用具有该权限，此方法也会返回 false
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                ActivityCompat.requestPermissions(context,new String[]{permission}, requestCode);
            }else{
                //todo 用户已经点击了不再显示，弹出自己的提示 去引导用户到设置界面
                permissionCallBack.noPermission();
            }
        }
    }

    /***
     * app内需要的权限
     * @param activity
     */
    public static void requestAppPermission(Activity activity) {
        if ((ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
                ||
                (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1001);
        }
    }
}
