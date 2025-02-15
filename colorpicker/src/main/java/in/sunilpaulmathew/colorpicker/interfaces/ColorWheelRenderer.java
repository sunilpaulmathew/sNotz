package in.sunilpaulmathew.colorpicker.interfaces;

import android.graphics.Canvas;

import in.sunilpaulmathew.colorpicker.utils.ColorCircle;

import java.util.List;

public interface ColorWheelRenderer {
	float GAP_PERCENTAGE = 0.025f;

	void draw(int density, float maxRadius, float lightness, float alpha, float strokeWidth, float cSize, Canvas targetCanvas);

	List<ColorCircle> getColorCircleList();
}