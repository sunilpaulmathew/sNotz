package com.sunilpaulmathew.snotz.utils.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.CheckListActivity;
import com.sunilpaulmathew.snotz.interfaces.EditTextInterface;
import com.sunilpaulmathew.snotz.utils.CheckLists;

import java.io.File;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 18, 2023
 */
public class CreateChecklistDialog {

    public CreateChecklistDialog(Activity activity) {
        new EditTextInterface(null, activity.getString(R.string.check_list_create_question), activity) {
            @Override
            public void positiveButtonLister(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    if (sFileUtils.exist(new File(activity.getExternalFilesDir("checklists"), s.toString().trim()))) {
                        new MaterialAlertDialogBuilder(activity)
                                .setMessage(activity.getString(R.string.check_list_exist_warning))
                                .setNegativeButton(activity.getString(R.string.change_name), (dialogInterface, i) -> new CreateChecklistDialog(activity))
                                .setPositiveButton(activity.getString(R.string.replace), (dialogInterface, i) -> {
                                    CheckLists.setCheckListName(s.toString().trim());
                                    Intent createCheckList = new Intent(activity, CheckListActivity.class);
                                    activity.startActivity(createCheckList);
                                }).show();
                    } else {
                        CheckLists.setCheckListName(s.toString().trim());
                        Intent createCheckList = new Intent(activity, CheckListActivity.class);
                        activity.startActivity(createCheckList);
                    }
                } else {
                    sCommonUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.check_list_name_empty_message)).show();
                }
            }
        }.show();
    }

}