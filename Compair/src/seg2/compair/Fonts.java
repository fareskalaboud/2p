package seg2.compair;

import android.app.Activity;
import android.graphics.Typeface;

/**
 * Created by faresalaboud on 21/11/14.
 */
public class Fonts {

    public static Typeface LATO_BLACK;
    public static Typeface LATO_HEAVY;
    public static Typeface LATO_BOLD;
    public static Typeface LATO_MEDIUM;
    public static Typeface LATO_REGULAR;
    public static Typeface LATO_LIGHT;
    public static Typeface LATO_LIGHTITALIC;
    public static Typeface LATO_THIN;
    public static Typeface LATO_HAIRLINE;
    public static Typeface FONTAWESOME;
    public static boolean done = false;

    public static void makeFonts(Activity context) {
        if (!done) return;

        done = true;

        LATO_BLACK = Typeface.createFromAsset(context.getAssets(), "Lato-Black.ttf");
        LATO_HEAVY = Typeface.createFromAsset(context.getAssets(), "Lato-Heavy.ttf");
        LATO_BOLD = Typeface.createFromAsset(context.getAssets(), "Lato-Bold.ttf");
        LATO_MEDIUM = Typeface.createFromAsset(context.getAssets(), "Lato-Medium.ttf");
        LATO_REGULAR = Typeface.createFromAsset(context.getAssets(), "Lato-Regular.ttf");
        LATO_LIGHT = Typeface.createFromAsset(context.getAssets(), "Lato-Light.ttf");
        LATO_LIGHTITALIC = Typeface.createFromAsset(context.getAssets(), "Lato-LightItalic.ttf");
        LATO_THIN = Typeface.createFromAsset(context.getAssets(), "Lato-Thin.ttf");
        LATO_HAIRLINE = Typeface.createFromAsset(context.getAssets(), "Lato-Hairline.ttf");
        FONTAWESOME = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
    }
}
