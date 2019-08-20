package geen.lou.videoadmodule;


/**
 * 播放器配置类
 * Created by xinyu on 2018/4/3.
 */

public class PlayerConfig {

    public boolean useAndroidMediaPlayer;//是否使用AndroidMediaPlayer
    public boolean isLooping;//是否循环播放
    public boolean mAutoRotate;//是否旋转屏幕
    public boolean isCache;//是否开启缓存
    public boolean addToPlayerManager;//是否添加到播放管理器
    public boolean useSurfaceView;//是否使用TextureView
    public boolean enableMediaCodec;//是否启用硬解码
    public boolean savingProgress;//是否保存进度


    private PlayerConfig(PlayerConfig origin) {
        this.useAndroidMediaPlayer = origin.useAndroidMediaPlayer;
        this.isLooping = origin.isLooping;
        this.mAutoRotate = origin.mAutoRotate;
        this.isCache = origin.isCache;
        this.addToPlayerManager = origin.addToPlayerManager;
        this.useSurfaceView = origin.useSurfaceView;
        this.enableMediaCodec = origin.enableMediaCodec;
        this.savingProgress = origin.savingProgress;
    }

    private PlayerConfig() {

    }

    public static class Builder {

        private PlayerConfig target;

        public Builder() {
            target = new PlayerConfig();
        }

        /**
         * 开启缓存
         */
        public Builder enableCache() {
            target.isCache = true;
            return this;
        }

        /**
         * 添加到{@link },如需集成到RecyclerView或ListView请开启此选项
         */
        public Builder addToPlayerManager() {
            target.addToPlayerManager = true;
            return this;
        }

        /**
         * 启用SurfaceView
         */
        public Builder useSurfaceView() {
            target.useSurfaceView = true;
            return this;
        }

        /**
         * 启用{@link android.media.MediaPlayer}
         */
        public Builder useAndroidMediaPlayer() {
            target.useAndroidMediaPlayer = true;
            return this;
        }

        /**
         * 设置自动旋转
         */
        public Builder autoRotate() {
            target.mAutoRotate = true;
            return this;
        }

        /**
         * 开启循环播放
         */
        public Builder setLooping() {
            target.isLooping = true;
            return this;
        }

        /**
         * 开启硬解码，只对IjkPlayer有效
         */
        public Builder enableMediaCodec() {
            target.enableMediaCodec = true;
            return this;
        }

        /**
         * 保存播放进度
         */
        public Builder savingProgress() {
            target.savingProgress = true;
            return this;
        }


        public PlayerConfig build() {
            return new PlayerConfig(target);
        }
    }
}
