package in.sunilpaulmathew.colorpicker.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import in.sunilpaulmathew.colorpicker.R;
import in.sunilpaulmathew.colorpicker.interfaces.ColorWheelRenderer;
import in.sunilpaulmathew.colorpicker.interfaces.OnColorSelectedListener;
import in.sunilpaulmathew.colorpicker.renderer.FlowerColorWheelRenderer;
import in.sunilpaulmathew.colorpicker.utils.ColorCircle;
import in.sunilpaulmathew.colorpicker.utils.ColorCircleDrawable;
import in.sunilpaulmathew.colorpicker.utils.ColorPickerUtils;
import in.sunilpaulmathew.colorpicker.utils.PaintBuilder;

public class ColorPickerView extends View {

	private static final float STROKE_RATIO = 1.5f;
	private Bitmap colorWheel;
	private Canvas colorWheelCanvas;
	private Bitmap currentColor;
	private Canvas currentColorCanvas;
	private boolean showBorder;
	private int density = 8;
	private float lightness = 1;
	private float alpha = 1;
	private Integer[] initialColors = new Integer[] {
			null, null, null, null, null
	};
	private int colorSelection = 0;
	private Integer initialColor;
	private final Paint colorWheelFill = PaintBuilder.newPaint().color(0).build();
	private final Paint alphaPatternPaint = PaintBuilder.newPaint().build();
	private ColorCircle currentColorCircle;
	private final ArrayList<OnColorSelectedListener> listeners = new ArrayList<>();
	private LightnessSlider lightnessSlider;
	private AlphaSlider alphaSlider;
	private ColorWheelRenderer renderer;
	private int alphaSliderViewId, lightnessSliderViewId;

	public ColorPickerView(Context context) {
		super(context);
		initWith(context, null);
	}

	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWith(context, attrs);
	}

	public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initWith(context, attrs);
	}

	private void initWith(Context context, AttributeSet attrs) {
		@SuppressLint("CustomViewStyleable")
		final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerPreference);

		density = typedArray.getInt(R.styleable.ColorPickerPreference_density, 10);
		initialColor = typedArray.getInt(R.styleable.ColorPickerPreference_initialColor, 0xffffffff);

		renderer = new FlowerColorWheelRenderer();

		alphaSliderViewId = typedArray.getResourceId(R.styleable.ColorPickerPreference_alphaSliderView, 0);
		lightnessSliderViewId = typedArray.getResourceId(R.styleable.ColorPickerPreference_lightnessSliderView, 0);

		setRenderer(renderer);
		setDensity(density);
		setInitialColor(initialColor, true);

		typedArray.recycle();
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		updateColorWheel();
		currentColorCircle = findNearestByColor(initialColor);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (alphaSliderViewId != 0)
			setAlphaSlider(getRootView().findViewById(alphaSliderViewId));
		if (lightnessSliderViewId != 0)
			setLightnessSlider(getRootView().findViewById(lightnessSliderViewId));

		updateColorWheel();
		currentColorCircle = findNearestByColor(initialColor);
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		updateColorWheel();
	}

	private void updateColorWheel() {
		if (getMeasuredWidth() <= 0)
			return;
		if (colorWheel == null || colorWheel.getWidth() != getMeasuredWidth()) {
			colorWheel = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredWidth(), Bitmap.Config.ARGB_8888);
			colorWheelCanvas = new Canvas(colorWheel);
			alphaPatternPaint.setShader(PaintBuilder.createAlphaPatternShader(26));
		}
		if (currentColor == null || currentColor.getWidth() != getMeasuredWidth()) {
			currentColor = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredWidth(), Bitmap.Config.ARGB_8888);
			currentColorCanvas = new Canvas(currentColor);
		}
		drawColorWheel();
		invalidate();
	}

	private void drawColorWheel() {
		colorWheelCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		currentColorCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

		if (renderer == null) return;

		float half = colorWheelCanvas.getWidth() / 2f;
		float strokeWidth = STROKE_RATIO * (1f + ColorWheelRenderer.GAP_PERCENTAGE);
		float maxRadius = half - strokeWidth - half / density;
		float cSize = maxRadius / (density - 1) / 2;
		renderer.draw(this.density, maxRadius, lightness, alpha, strokeWidth, cSize, colorWheelCanvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int width = 0;
		if (widthMode == MeasureSpec.UNSPECIFIED)
			width = widthMeasureSpec;
		else if (widthMode == MeasureSpec.AT_MOST)
			width = MeasureSpec.getSize(widthMeasureSpec);
		else if (widthMode == MeasureSpec.EXACTLY)
			width = MeasureSpec.getSize(widthMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int height = 0;
		if (heightMode == MeasureSpec.UNSPECIFIED)
			height = heightMeasureSpec;
		else if (heightMode == MeasureSpec.AT_MOST)
			height = MeasureSpec.getSize(heightMeasureSpec);
		else if (heightMode == MeasureSpec.EXACTLY)
			height = MeasureSpec.getSize(heightMeasureSpec);
        int squareDimen = Math.min(height, width);
		setMeasuredDimension(squareDimen, squareDimen);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE: {
				currentColorCircle = findNearestByPosition(event.getX(), event.getY());
				int selectedColor = getSelectedColor();

				initialColor = selectedColor;
				setColorToSliders(selectedColor);
				updateColorWheel();
				invalidate();
				break;
			}
			case MotionEvent.ACTION_UP: {
				int selectedColor = getSelectedColor();
				for (OnColorSelectedListener listener : listeners) {
					try {
						listener.onColorSelected(selectedColor);
					} catch (Exception ignored) {
					}
				}
				setColorToSliders(selectedColor);
				setColorText(selectedColor);
				setColorPreviewColor(selectedColor);
				invalidate();
				break;
			}
		}
		return true;
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		super.onDraw(canvas);
		int backgroundColor = 0x00000000;
		canvas.drawColor(backgroundColor);

		float maxRadius = getWidth() / (1f + ColorWheelRenderer.GAP_PERCENTAGE);
		float size = maxRadius / density / 2;
		if (colorWheel != null && currentColorCircle != null) {
			colorWheelFill.setColor(Color.HSVToColor(currentColorCircle.getHsvWithLightness(this.lightness)));
			colorWheelFill.setAlpha((int) (alpha * 0xff));

			// a separate canvas is used to erase an issue with the alpha pattern around the edges
			// draw circle slightly larger than it needs to be, then erase edges to proper dimensions
			currentColorCanvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size + 4, alphaPatternPaint);
			currentColorCanvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size + 4, colorWheelFill);

			Paint selectorStroke = PaintBuilder.newPaint().color(0xffffffff).style(Paint.Style.STROKE).stroke(size * (STROKE_RATIO - 1)).xPerMode(PorterDuff.Mode.CLEAR).build();

			if (showBorder) colorWheelCanvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size + (selectorStroke.getStrokeWidth() / 2f), selectorStroke);
			canvas.drawBitmap(colorWheel, 0, 0, null);

			currentColorCanvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size + (selectorStroke.getStrokeWidth() / 2f), selectorStroke);
			canvas.drawBitmap(currentColor, 0, 0, null);
		}
	}

	private ColorCircle findNearestByPosition(float x, float y) {
		ColorCircle near = null;
		double minDist = Double.MAX_VALUE;

		for (ColorCircle colorCircle : renderer.getColorCircleList()) {
			double dist = colorCircle.sqDist(x, y);
			if (minDist > dist) {
				minDist = dist;
				near = colorCircle;
			}
		}

		return near;
	}

	private ColorCircle findNearestByColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		ColorCircle near = null;
		double minDiff = Double.MAX_VALUE;
		double x = hsv[1] * Math.cos(hsv[0] * Math.PI / 180);
		double y = hsv[1] * Math.sin(hsv[0] * Math.PI / 180);

		for (ColorCircle colorCircle : renderer.getColorCircleList()) {
			float[] hsv1 = colorCircle.getHsv();
			double x1 = hsv1[1] * Math.cos(hsv1[0] * Math.PI / 180);
			double y1 = hsv1[1] * Math.sin(hsv1[0] * Math.PI / 180);
			double dx = x - x1;
			double dy = y - y1;
			double dist = dx * dx + dy * dy;
			if (dist < minDiff) {
				minDiff = dist;
				near = colorCircle;
			}
		}

		return near;
	}

	public int getSelectedColor() {
		int color = 0;
		if (currentColorCircle != null)
			color = ColorPickerUtils.colorAtLightness(currentColorCircle.getColor(), this.lightness);
		return ColorPickerUtils.adjustAlpha(this.alpha, color);
	}

	public Integer[] getAllColors() {
		return initialColors;
	}

	public void setInitialColors(Integer[] colors, int selectedColor) {
		this.initialColors = colors;
		this.colorSelection = selectedColor;
		Integer initialColor = this.initialColors[this.colorSelection];
		if (initialColor == null) initialColor = 0xffffffff;
		setInitialColor(initialColor, true);
	}

	public void setInitialColor(int color, boolean updateText) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);

		this.alpha = ColorPickerUtils.getAlphaPercent(color);
		this.lightness = hsv[2];
		this.initialColors[this.colorSelection] = color;
		this.initialColor = color;
		setColorPreviewColor(color);
		setColorToSliders(color);
		if (updateText)
			setColorText(color);
		currentColorCircle = findNearestByColor(color);
	}

	public void setLightness(float lightness) {
		this.lightness = lightness;
		if (currentColorCircle != null) {
			this.initialColor = Color.HSVToColor(ColorPickerUtils.alphaValueAsInt(this.alpha), currentColorCircle.getHsvWithLightness(lightness));
			new TextInputEditText(this.getContext()).setText(ColorPickerUtils.getHexString(this.initialColor, this.alphaSlider != null));
			if (this.alphaSlider != null && this.initialColor != null)
				this.alphaSlider.setColor(this.initialColor);

			updateColorWheel();
			invalidate();
		}
	}

	public void setAlphaValue(float alpha) {
		this.alpha = alpha;
		this.initialColor = Color.HSVToColor(ColorPickerUtils.alphaValueAsInt(this.alpha), currentColorCircle.getHsvWithLightness(this.lightness));
		new TextInputEditText(this.getContext()).setText(ColorPickerUtils.getHexString(this.initialColor, this.alphaSlider != null));
		if (this.lightnessSlider != null && this.initialColor != null)
			this.lightnessSlider.setColor(this.initialColor);

		updateColorWheel();
		invalidate();
	}

	public void addOnColorSelectedListener(OnColorSelectedListener listener) {
		this.listeners.add(listener);
	}

	public void setLightnessSlider(LightnessSlider lightnessSlider) {
		this.lightnessSlider = lightnessSlider;
		if (lightnessSlider != null) {
			this.lightnessSlider.setColorPicker(this);
			this.lightnessSlider.setColor(getSelectedColor());
		}
	}

	public void setAlphaSlider(AlphaSlider alphaSlider) {
		this.alphaSlider = alphaSlider;
		if (alphaSlider != null) {
			this.alphaSlider.setColorPicker(this);
			this.alphaSlider.setColor(getSelectedColor());
		}
	}

	public void setDensity(int density) {
		this.density = Math.max(2, density);
		invalidate();
	}

	public void setRenderer(ColorWheelRenderer renderer) {
		this.renderer = renderer;
		invalidate();
	}

	public void setShowBorder(boolean showBorder) {
		this.showBorder = showBorder;
	}

	private void setColorPreviewColor(int newColor) {
		if (initialColors == null || colorSelection > initialColors.length || initialColors[colorSelection] == null)
			return;
		new AppCompatImageView(this.getContext()).setImageDrawable(new ColorCircleDrawable(newColor));
	}

	private void setColorText(int argb) {
		new TextInputEditText(this.getContext()).setText(ColorPickerUtils.getHexString(argb, this.alphaSlider != null));
	}

	private void setColorToSliders(int selectedColor) {
		if (lightnessSlider != null)
			lightnessSlider.setColor(selectedColor);
		if (alphaSlider != null)
			alphaSlider.setColor(selectedColor);
	}
}
