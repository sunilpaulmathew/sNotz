package com.sunilpaulmathew.snotz.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.sunilpaulmathew.snotz.R;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 16, 2020
 * Based on the original implementation of BorderCircleView by Willi Ye <williye97@gmail.com>
 * Ref: https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/views/BorderCircleView.java
 */

public class sNotzColor extends FrameLayout {

    public static int mColorSelection = -1;
    public static final SparseArray<String> sAccentColors = new SparseArray<>();

    static {
        sAccentColors.put(R.color.color_red, "red_accent");
        sAccentColors.put(R.color.color_pink, "pink_accent");
        sAccentColors.put(R.color.color_purple, "purple_accent");
        sAccentColors.put(R.color.color_blue, "blue_accent");
        sAccentColors.put(R.color.color_green, "green_accent");
        sAccentColors.put(R.color.color_orange, "orange_accent");
        sAccentColors.put(R.color.color_black, "black_accent");
        sAccentColors.put(R.color.color_white, "white_accent");
        sAccentColors.put(R.color.color_blue_grey, "blue_grey_accent");
        sAccentColors.put(R.color.color_teal, "teal_accent");
    }

    private final Drawable mCheck;
    private boolean mChecked;
    private final Paint mPaint;
    private final Paint mPaintBorder;

    public sNotzColor(Context context) {
        this(context, null);
    }

    public sNotzColor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public sNotzColor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mCheck = ContextCompat.getDrawable(context, R.drawable.ic_done);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaintBorder.setStrokeWidth((5));
        mPaintBorder.setStyle(Paint.Style.STROKE);

        assert mCheck != null;
        DrawableCompat.setTint(mCheck, Color.DKGRAY);

        mPaint.setColor(setAccentColor(Utils.mTextColor ? "text_color" : "note_background", getContext()));
        mPaintBorder.setColor(ContextCompat.getColor(context, R.color.color_brown));
        setWillNotDraw(false);
    }

    public void setCircleColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        float radius = Math.min(width, height) / 2f - 4f;

        canvas.drawCircle(width / 2, height / 2, radius, mPaint);
        canvas.drawCircle(width / 2, height / 2, radius, mPaintBorder);

        if (mChecked) {
            mCheck.setBounds(Math.round(width / 2 - radius), 0, Math.round(width / 2 + radius), height);
            mCheck.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float desiredWidth = 125;
        float desiredHeight = 125;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float width;
        float height;

        if (widthMode == MeasureSpec.EXACTLY) width = widthSize;
        else if (widthMode == MeasureSpec.AT_MOST) width = Math.min(desiredWidth, widthSize);
        else width = desiredWidth;

        if (heightMode == MeasureSpec.EXACTLY) height = heightSize;
        else if (heightMode == MeasureSpec.AT_MOST) height = Math.min(desiredHeight, heightSize);
        else height = desiredHeight;

        setMeasuredDimension((int) width, (int) height);
    }

    public static void colorDialog(int selection, String item, Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = 25;
        linearLayout.setPadding(padding, padding, padding, padding);

        final List<sNotzColor> circles = new ArrayList<>();

        LinearLayout subView = null;
        for (int i = 0; i < sAccentColors.size(); i++) {
            if (subView == null || i % 5 == 0) {
                subView = new LinearLayout(context);
                subView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.addView(subView);
            }
            sNotzColor circle = new sNotzColor(context);
            circle.setChecked(i == selection);
            circle.setCircleColor(ContextCompat.getColor(context, sAccentColors.keyAt(i)));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            params.setMargins(5, 5, 5, 5);
            circle.setLayoutParams(params);
            circle.setOnClickListener(v -> {
                for (sNotzColor sNotzColor : circles) {
                    if (v == sNotzColor) {
                        sNotzColor.setChecked(true);
                        mColorSelection = circles.indexOf(sNotzColor);
                    } else {
                        sNotzColor.setChecked(false);
                    }
                }
            });

            circles.add(circle);
            subView.addView(circle);
        }

        new AlertDialog.Builder(context).setView(linearLayout)
                .setTitle(R.string.choose_color)
                .setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> {
                })
                .setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
                    if (mColorSelection >= 0) {
                        Utils.saveString(item,
                                sAccentColors.valueAt(mColorSelection), context);
                    }
                    Utils.reloadUI(context);
                }).setOnDismissListener(dialog -> mColorSelection = -1).show();
    }

    public static int setAccentColor(String item, Context context) {
        String accent = Utils.getString(item, item.equals("text_color") ? "white_accent" : "teal_accent", context);
        int color = 0;
        switch (accent) {
            case "purple_accent":
                color = context.getResources().getColor(R.color.color_purple);
                break;
            case "red_accent":
                color = context.getResources().getColor(R.color.color_red);
                break;
            case "pink_accent":
                color = context.getResources().getColor(R.color.color_pink);
                break;
            case "blue_accent":
                color = context.getResources().getColor(R.color.color_blue);
                break;
            case "green_accent":
                color = context.getResources().getColor(R.color.color_green);
                break;
            case "orange_accent":
                color = context.getResources().getColor(R.color.color_orange);
                break;
            case "black_accent":
                color = context.getResources().getColor(R.color.color_black);
                break;
            case "white_accent":
                color = context.getResources().getColor(R.color.color_white);
                break;
            case "blue_grey_accent":
                color = context.getResources().getColor(R.color.color_blue_grey);
                break;
            case "teal_accent":
                color = context.getResources().getColor(R.color.color_teal);
                break;
        }
        return color;
    }

    public static List<Integer> getColors(Context context) {
        List<Integer> sColors = new ArrayList<>();
        for (int i = 0; i < sAccentColors.size(); i++) {
            sColors.add(sAccentColors.keyAt(i));
        }
        for (int i = 0; i < sColors.size(); i++) {
            sColors.set(i, ContextCompat.getColor(context, sColors.get(i)));
        }
        return sColors;
    }

    public static int getAccentColor(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        return value.data;
    }

}