package com.sunilpaulmathew.snotz.utils.serializableItems;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 13, 2026
 */
public class MenuEntry implements Serializable {

    private final String titleText, descriptionText;
    private final int id, drawable;
    private final boolean isSwitch, isEnabled;

    public MenuEntry(String titleText, int drawable, boolean isSwitch, boolean isEnabled, int id) {
        this.titleText = titleText;
        this.descriptionText = null;
        this.drawable = drawable;
        this.isSwitch = isSwitch;
        this.isEnabled = isEnabled;
        this.id = id;
    }

    public MenuEntry(String titleText, int drawable, int id) {
        this.titleText = titleText;
        this.descriptionText = null;
        this.drawable = drawable;
        this.id = id;
        this.isSwitch = false;
        this.isEnabled = false;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isSwitch() {
        return isSwitch;
    }

    public int getDrawable() {
        return drawable;
    }

    public int getID() {
        return id;
    }

    public String getDescription() {
        return descriptionText;
    }

    public String getTile() {
        return titleText;
    }

}