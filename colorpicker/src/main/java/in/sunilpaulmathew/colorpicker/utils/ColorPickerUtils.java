package in.sunilpaulmathew.colorpicker.utils;

import android.graphics.Color;

public class ColorPickerUtils {
	public static float getAlphaPercent(int argb) {
		return Color.alpha(argb) / 255f;
	}

	public static int alphaValueAsInt(float alpha) {
		return Math.round(alpha * 255);
	}

	public static int adjustAlpha(float alpha, int color) {
		return alphaValueAsInt(alpha) << 24 | (0x00ffffff & color);
	}

	public static int colorAtLightness(int color, float lightness) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] = lightness;
		return Color.HSVToColor(hsv);
	}

	public static float lightnessOfColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		return hsv[2];
	}

    public static String getHexString(int color, boolean showAlpha) {
        int base = showAlpha ? 0xFFFFFFFF : 0xFFFFFF;
        String format = showAlpha ? "#%08X" : "#%06X";
        return String.format(format, (base & color)).toUpperCase();
    }

}