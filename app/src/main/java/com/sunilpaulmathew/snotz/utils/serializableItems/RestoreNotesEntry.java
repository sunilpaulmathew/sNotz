package com.sunilpaulmathew.snotz.utils.serializableItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 16, 2026
 */
public class RestoreNotesEntry extends sNotzEntry {

    private boolean mSelected;

    public RestoreNotesEntry(sNotzEntry entry, boolean selected) {
        super(entry.getNote(), entry.getTimeStamp(), entry.isHidden(), entry.getColorBackground(), entry.getColorText(), entry.getNoteID());
        this.mSelected = selected;
    }

    public boolean isSelected() {
        return this.mSelected;
    }

    public void isSelected(boolean selected) {
        this.mSelected = selected;
    }

}