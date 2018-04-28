package com.ihomey.linkuphome.widget.toprightmenu;

/**
 * Author：Bro0cL on 2016/12/26.
 */

public class MenuItem {

    private int id;
    private int normalIcon;
    private int selectIcon;
    private int textRes;

    public MenuItem(int normalIcon, int textRes) {
        this.id = normalIcon;
        this.normalIcon = normalIcon;
        this.textRes = textRes;
    }

    public MenuItem(int normalIcon, int selectIcon, int textRes) {
        this.id = normalIcon;
        this.normalIcon = normalIcon;
        this.selectIcon = selectIcon;
        this.textRes = textRes;
    }

    public int getNormalIcon() {
        return normalIcon;
    }

    public void setNormalIcon(int normalIcon) {
        this.normalIcon = normalIcon;
    }

    public int getSelectIcon() {
        return selectIcon;
    }

    public void setSelectIcon(int selectIcon) {
        this.selectIcon = selectIcon;
    }

    public int getTextRes() {
        return textRes;
    }

    public void setTextRes(int textRes) {
        this.textRes = textRes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
