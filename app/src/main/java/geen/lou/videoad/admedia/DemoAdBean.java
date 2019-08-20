package geen.lou.videoad.admedia;

import java.io.Serializable;

public class DemoAdBean implements Serializable {

    private String ads_title;

    private String material_url;

    private String material_type;

    private String click_url;

    private String advertiser_icon;

    private String advertiser_name;

    private String ads_button;

    private String material_id;

    private String cover_image_url;



    public String getAds_title() {
        return ads_title;
    }

    public void setAds_title(String ads_title) {
        this.ads_title = ads_title;
    }

    public String getMaterial_url() {
        return material_url;
    }

    public void setMaterial_url(String material_url) {
        this.material_url = material_url;
    }

    public String getMaterial_type() {
        return material_type;
    }

    public void setMaterial_type(String material_type) {
        this.material_type = material_type;
    }

    public String getClick_url() {
        return click_url;
    }

    public void setClick_url(String click_url) {
        this.click_url = click_url;
    }

    public String getAdvertiser_icon() {
        return advertiser_icon;
    }

    public void setAdvertiser_icon(String advertiser_icon) {
        this.advertiser_icon = advertiser_icon;
    }

    public String getAdvertiser_name() {
        return advertiser_name;
    }

    public void setAdvertiser_name(String advertiser_name) {
        this.advertiser_name = advertiser_name;
    }

    public String getAds_button() {
        return ads_button;
    }

    public void setAds_button(String ads_button) {
        this.ads_button = ads_button;
    }

    public String getMaterial_id() {
        return material_id;
    }

    public void setMaterial_id(String material_id) {
        this.material_id = material_id;
    }

    public void setCover_image_url(String cover_image_url) {
        this.cover_image_url = cover_image_url;
    }

    public String getCover_image_url() {
        return cover_image_url;
    }
}
