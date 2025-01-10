package com.sunilpaulmathew.snotz.colorpicker.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import com.sunilpaulmathew.snotz.colorpicker.utils.PaintBuilder;
import com.sunilpaulmathew.snotz.colorpicker.utils.ColorPickerUtils;

public class AlphaSlider extends AbsCustomSlider {

	public int color;
	private final Paint alphaPatternPaint = PaintBuilder.newPaint().build();
	private final Paint barPaint = PaintBuilder.newPaint().build();
	private final Paint solid = PaintBuilder.newPaint().build();
	private final Paint clearingStroke = PaintBuilder.newPaint().color(0xffffffff).xPerMode(PorterDuff.Mode.CLEAR).build();
	private Bitmap clearBitmap;
	private Canvas clearBitmapCanvas;
	private ColorPickerView colorPicker;

	public AlphaSlider(Context context) {
		super(context);
	}

	public AlphaSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlphaSlider(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void createBitmaps() {
		super.createBitmaps();
		alphaPatternPaint.setShader(PaintBuilder.createAlphaPatternShader(barHeight * 2));
		clearBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		clearBitmapCanvas = new Canvas(clearBitmap);
	}

	@Override
	protected void drawBar(Canvas barCanvas) {
		int width = barCanvas.getWidth();
		int height = barCanvas.getHeight();

		barCanvas.drawRect(0, 0, width, height, alphaPatternPaint);
		int l = Math.max(2, width / 256);
		for (int x = 0; x <= width; x += l) {
			float alpha = (float) x / (width - 1);
			barPaint.setColor(color);
			barPaint.setAlpha(Math.round(alpha * 255));
			barCanvas.drawRect(x, 0, x + l, height, barPaint);
		}
	}

	@Override
	protected void onValueChanged(float value) {
		if (colorPicker != null)
			colorPicker.setAlphaValue(value);
	}

	@Override
	protected void drawHandle(Canvas canvas, float x, float y) {
		solid.setColor(color);
		solid.setAlpha(Math.round(value * 255));
		if (showBorder) canvas.drawCircle(x, y, handleRadius, clearingStroke);
		if (value < 1) {
			// this fixes the same artifact issue from ColorPickerView
			// happens when alpha pattern is drawn underneath a circle with the same size
			clearBitmapCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
			clearBitmapCanvas.drawCircle(x, y, handleRadius * 0.75f + 4, alphaPatternPaint);
			clearBitmapCanvas.drawCircle(x, y, handleRadius * 0.75f + 4, solid);

			Paint clearStroke = PaintBuilder.newPaint().color(0xffffffff).style(Paint.Style.STROKE).stroke(6).xPerMode(PorterDuff.Mode.CLEAR).build();
			clearBitmapCanvas.drawCircle(x, y, handleRadius * 0.75f + (clearStroke.getStrokeWidth() / 2), clearStroke);
			canvas.drawBitmap(clearBitmap, 0, 0, null);
		} else {
			canvas.drawCircle(x, y, handleRadius * 0.75f, solid);
		}
	}

	public void setColorPicker(ColorPickerView colorPicker) {
		this.colorPicker = colorPicker;
	}

	public void setColor(int color) {
		this.color = color;
		this.value = ColorPickerUtils.getAlphaPercent(color);
		if (bar != null) {
			updateBar();
			invalidate();
		}
	}
}