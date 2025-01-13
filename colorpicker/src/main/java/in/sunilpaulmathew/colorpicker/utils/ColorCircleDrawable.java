package in.sunilpaulmathew.colorpicker.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;

public class ColorCircleDrawable extends ColorDrawable {
	private float strokeWidth;
	private final Paint strokePaint = PaintBuilder.newPaint().style(Paint.Style.STROKE).stroke(strokeWidth).color(0xff9e9e9e).build();
	private final Paint fillPaint = PaintBuilder.newPaint().style(Paint.Style.FILL).color(0).build();
	private final Paint fillBackPaint = PaintBuilder.newPaint().shader(PaintBuilder.createAlphaPatternShader(26)).build();

	public ColorCircleDrawable(int color) {
		super(color);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(0);

		int width = getBounds().width();
		float radius = width / 2f;
		strokeWidth = radius / 8f;

		this.strokePaint.setStrokeWidth(strokeWidth);
		this.fillPaint.setColor(getColor());
		canvas.drawCircle(radius, radius, radius - strokeWidth, fillBackPaint);
		canvas.drawCircle(radius, radius, radius - strokeWidth, fillPaint);
		canvas.drawCircle(radius, radius, radius - strokeWidth, strokePaint);
	}

	@Override
	public void setColor(int color) {
		super.setColor(color);
		invalidateSelf();
	}
}