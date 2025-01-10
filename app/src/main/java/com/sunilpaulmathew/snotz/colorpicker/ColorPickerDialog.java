package com.sunilpaulmathew.snotz.colorpicker;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.colorpicker.interfaces.ColorPickerClickListener;
import com.sunilpaulmathew.snotz.colorpicker.interfaces.OnColorSelectedListener;
import com.sunilpaulmathew.snotz.colorpicker.views.AlphaSlider;
import com.sunilpaulmathew.snotz.colorpicker.views.ColorPickerView;
import com.sunilpaulmathew.snotz.colorpicker.views.LightnessSlider;

public class ColorPickerDialog {

	private final ColorPickerView colorPickerView;
	private final LinearLayoutCompat pickerContainer;
	private final MaterialAlertDialogBuilder builder;

	private final Integer[] initialColor = new Integer[] {
			null, null, null, null, null
	};

	private ColorPickerDialog(Context context) {
		this(context, 0);
	}

	private ColorPickerDialog(Context context, int theme) {
		int defaultMargin = getDimensionAsPx(context, R.dimen.default_slider_margin);
		int defaultMarginTop = getDimensionAsPx(context, R.dimen.default_margin_top);

		builder = new MaterialAlertDialogBuilder(context, theme);
		pickerContainer = new LinearLayoutCompat(context);
		pickerContainer.setOrientation(LinearLayoutCompat.VERTICAL);
		pickerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
		pickerContainer.setPadding(defaultMargin, defaultMarginTop, defaultMargin, 0);

		LinearLayoutCompat.LayoutParams layoutParamsForColorPickerView = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
		layoutParamsForColorPickerView.weight = 1;
		colorPickerView = new ColorPickerView(context);

		pickerContainer.addView(colorPickerView, layoutParamsForColorPickerView);

		builder.setView(pickerContainer);
	}

	public static ColorPickerDialog with(Context context) {
		return new ColorPickerDialog(context);
	}

	public static ColorPickerDialog with(Context context, int theme) {
		return new ColorPickerDialog(context, theme);
	}

	public ColorPickerDialog setTitle(String title) {
		builder.setTitle(title);
		return this;
	}

	public ColorPickerDialog setTitle(int titleId) {
		builder.setTitle(titleId);
		return this;
	}

	public ColorPickerDialog initialColor(int initialColor) {
		this.initialColor[0] = initialColor;
		return this;
	}

	public ColorPickerDialog density(int density) {
		colorPickerView.setDensity(density);
		return this;
	}

	public ColorPickerDialog setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
		colorPickerView.addOnColorSelectedListener(onColorSelectedListener);
		return this;
	}

	public ColorPickerDialog setPositiveButton(CharSequence text, final ColorPickerClickListener onClickListener) {
		builder.setPositiveButton(text, (dialog, which) -> positiveButtonOnClick(dialog, onClickListener));
		return this;
	}

	public ColorPickerDialog setPositiveButton(int textId, final ColorPickerClickListener onClickListener) {
		builder.setPositiveButton(textId, (dialog, which) -> positiveButtonOnClick(dialog, onClickListener));
		return this;
	}

	public ColorPickerDialog setNegativeButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
		builder.setNegativeButton(text, onClickListener);
		return this;
	}

	public ColorPickerDialog setNegativeButton(int textId, DialogInterface.OnClickListener onClickListener) {
		builder.setNegativeButton(textId, onClickListener);
		return this;
	}

	public MaterialAlertDialogBuilder build() {
		Context context = builder.getContext();
		colorPickerView.setInitialColors(initialColor, getStartOffset(initialColor));
		boolean isBorderEnabled = true;
		colorPickerView.setShowBorder(isBorderEnabled);

		LinearLayoutCompat.LayoutParams layoutParamsForLightnessBar = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimensionAsPx(context, R.dimen.default_slider_height));
        LightnessSlider lightnessSlider = new LightnessSlider(context);
        lightnessSlider.setLayoutParams(layoutParamsForLightnessBar);
        pickerContainer.addView(lightnessSlider);
        colorPickerView.setLightnessSlider(lightnessSlider);
        lightnessSlider.setColor(getStartColor(initialColor));
        lightnessSlider.setShowBorder(isBorderEnabled);

		LinearLayoutCompat.LayoutParams layoutParamsForAlphaBar = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimensionAsPx(context, R.dimen.default_slider_height));
		AlphaSlider alphaSlider = new AlphaSlider(context);
		alphaSlider.setLayoutParams(layoutParamsForAlphaBar);
		pickerContainer.addView(alphaSlider);
		colorPickerView.setAlphaSlider(alphaSlider);
		alphaSlider.setColor(getStartColor(initialColor));
		alphaSlider.setShowBorder(isBorderEnabled);

		return builder;
	}

	private Integer getStartOffset(Integer[] colors) {
		int start = 0;
		for (int i = 0; i < colors.length; i++) {
			if (colors[i] == null) {
				return start;
			}
			start = (i + 1) / 2;
		}
		return start;
	}

	private int getStartColor(Integer[] colors) {
		Integer startColor = getStartOffset(colors);
		return startColor == null ? Color.WHITE : colors[startColor];
	}

	private static int getDimensionAsPx(Context context, int rid) {
		return (int) (context.getResources().getDimension(rid) + .5f);
	}

	private void positiveButtonOnClick(DialogInterface dialog, ColorPickerClickListener onClickListener) {
		int selectedColor = colorPickerView.getSelectedColor();
		Integer[] allColors = colorPickerView.getAllColors();
		onClickListener.onClick(dialog, selectedColor, allColors);
	}

}