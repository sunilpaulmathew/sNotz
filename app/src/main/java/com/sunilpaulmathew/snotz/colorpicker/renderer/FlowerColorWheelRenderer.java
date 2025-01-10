package com.sunilpaulmathew.snotz.colorpicker.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.sunilpaulmathew.snotz.colorpicker.interfaces.ColorWheelRenderer;
import com.sunilpaulmathew.snotz.colorpicker.utils.ColorCircle;
import com.sunilpaulmathew.snotz.colorpicker.utils.PaintBuilder;

import java.util.ArrayList;
import java.util.List;

public class FlowerColorWheelRenderer implements ColorWheelRenderer {

	private final List<ColorCircle> colorCircleList = new ArrayList<>();

	@Override
	public void draw(int density, float maxRadius, float lightness, float alpha, float strokeWidth, float cSize, Canvas targetCanvas) {
		final int setSize = colorCircleList.size();
		final Paint selectorFill = PaintBuilder.newPaint().build();
		final float[] hsv = new float[3];
		int currentCount = 0;
		float half = targetCanvas.getWidth() / 2f;

		for (int i = 0; i < density; i++) {
			float p = (float) i / (density - 1); // 0~1
			float jitter = (i - density / 2f) / density; // -0.5 ~ 0.5
			float radius = maxRadius * p;
			float sizeJitter = 1.2f;
			float size = Math.max(1.5f + strokeWidth, cSize + (i == 0 ? 0 : cSize * sizeJitter * jitter));
			int total = Math.min(calcTotalCount(radius, size), density * 2);

			for (int j = 0; j < total; j++) {
				double angle = Math.PI * 2 * j / total + (Math.PI / total) * ((i + 1) % 2);
				float x = half + (float) (radius * Math.cos(angle));
				float y = half + (float) (radius * Math.sin(angle));
				hsv[0] = (float) (angle * 180 / Math.PI);
				hsv[1] = radius / maxRadius;
				hsv[2] = lightness;
				selectorFill.setColor(Color.HSVToColor(hsv));
				selectorFill.setAlpha(Math.round(alpha * 255));

				targetCanvas.drawCircle(x, y, size - strokeWidth, selectorFill);

				if (currentCount >= setSize) {
					colorCircleList.add(new ColorCircle(x, y, hsv));
				} else colorCircleList.get(currentCount).set(x, y, hsv);
				currentCount++;
			}
		}
	}

	public List<ColorCircle> getColorCircleList() {
		return colorCircleList;
	}

	protected int calcTotalCount(float radius, float size) {
		return Math.max(1, (int) ((1f - GAP_PERCENTAGE) * Math.PI / (Math.asin(size / radius)) + 0.5f));
	}
}