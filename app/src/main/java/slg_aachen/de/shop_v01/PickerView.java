package slg_aachen.de.shop_v01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * This class is a view used to display the background of the color picker.
 * Two gradients.
 * One black to white, second current hue to transparent.
 * Reacts to user input using the HSVPicker Fragment, see there for additional information
 */

public class PickerView extends View {
    private RectF rekt;
    private Shader valShader;
    private int theHue;
    private float actX, actY;
    private ImageView i;


    public PickerView(Context context) {
        super(context);
    }

    public PickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSatVal(canvas);
    }


 //   private void justBlack(Canvas c) {
      //  c.drawColor(Color.WHITE);
    //}

    private void drawSatVal(Canvas canvas) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (valShader == null) {
            valShader = new LinearGradient(getRekt().left, getRekt().top, getRekt().left, getRekt().bottom,
                    0xffffffff, 0xff000000, Shader.TileMode.CLAMP);
        }

        ComposeShader shader = new ComposeShader(new LinearGradient(getRekt().left, getRekt().top, getRekt().right, getRekt().top,
                0xffffffff, theHue, Shader.TileMode.CLAMP), valShader, PorterDuff.Mode.MULTIPLY);

        Paint p = new Paint();

        p.setShader(shader);
        canvas.drawRect(rekt, p);



    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        actX = 0;
        actY = 0;
        float newW;
        rekt = new RectF();
        if (h < w) {
            newW = (w - h) / 2;
            Log.e("OFFSET", String.valueOf(newW));
            rekt.left = newW;
            rekt.right = w - newW;
            rekt.top = getPaddingTop();
            rekt.bottom = h;
        } else {
            rekt.left = getPaddingLeft();
            rekt.right = h - getPaddingRight();
            rekt.top = getPaddingTop();
            rekt.bottom = h - getPaddingBottom();
        }
    }


    private void init() {
        theHue = Color.HSVToColor(new float[]{0, 1f, 1f});
    }


    public RectF getRekt() {
        return rekt;
    }


    public float[] getSatVal() {
        float[] a = new float[2];
        a[0] = actX / getRekt().width();
        a[1] = (getHeight() - actY) / getRekt().height();
        //
        return a;
    }

    public void setSatVal(float sat, float val) {
        actX = sat * getRekt().width();
        actY = getHeight() - val * getRekt().height();
    }

    public void setHue(int h) {
        theHue = h;
    }

    public void setXY(float x, float y) {
        actX = x;
        actY = y;
    }
}
