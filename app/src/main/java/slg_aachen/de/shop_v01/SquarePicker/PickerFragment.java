package slg_aachen.de.shop_v01.SquarePicker;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import slg_aachen.de.shop_v01.MainActivity;
import slg_aachen.de.shop_v01.R;

import static android.R.string.cancel;
import static android.R.string.ok;

/*
* Fragment no. 2
* This Activity is used to display a color picker
* made in SatValView with shaders
*
* HSV - Picker
*
* Also featuring a button. WOW!
* */
public class PickerFragment extends android.support.v4.app.Fragment {


    private Button buttonHSVPicker;
    private SeekBar seekBar;
    private ImageView i;
    private PickerView d;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_square_picker, container, false);
        super.onCreate(savedInstanceState);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar4);
        i = (ImageView) view.findViewById(R.id.pointerPicker);

        d = (PickerView) view.findViewById(R.id.satValView);
        i.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ViewTreeObserver vto = seekBar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() { //when layout has finished building, get width and fill the seekbar with a nice gradient from red to red, all hues
                int width = seekBar.getWidth();
                Rect test = seekBar.getProgressDrawable().getBounds();

                Log.e("SEEKBAR", String.valueOf(width)); //return display width
                LinearGradient l = new LinearGradient(0.f, 0.f, test.width(), 0f, hueArray(), null, Shader.TileMode.CLAMP); //filling seekbar with gradient, all hues
                ShapeDrawable shape = new ShapeDrawable(new RectShape());
                shape.getPaint().setShader(l);


                seekBar.setProgressDrawable((Drawable) shape);
                ViewTreeObserver obs = seekBar.getViewTreeObserver();


                obs.removeOnGlobalLayoutListener(this);

            }


        });
        ViewTreeObserver vto2 = d.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() { //when layout has finished building, get Rect of PickerView set the pointer on POS
                int left = d.getPaddingLeft();
                int top = d.getPaddingTop();
                int right = d.getPaddingRight();
                int bottom = d.getPaddingBottom();

                Log.e("PICKERVIEW", String.valueOf(left)+" "+String.valueOf(top)+" "+String.valueOf(right)+" "+String.valueOf(bottom)); //return left padding of "d"

                ViewTreeObserver obs2 = d.getViewTreeObserver();
                //i.setPadding(left, top, right, bottom);
             //   i.setPadding(15, 15, 15, 15);
                i.setX(left);
                i.setY(top);
                i.setVisibility(View.VISIBLE);

                obs2.removeOnGlobalLayoutListener(this);

            }


        });

        seekBar.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        updateSatVal(seekBar.getProgress());


        buttonHSVPicker = (Button) view.findViewById(R.id.button);
        buttonHSVPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();

            }

        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSatVal(progress);
                if (fromUser) {
                    sendRGBtoUpdate();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendRGBtoServer();
            }});


        d.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //touch on the SatValView


                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        movePointer(event.getX(), event.getY(), true);
                        setSatValCoords(event.getX(), event.getY());
                        sendRGBtoUpdate();
                        Log.e("ACTIONS - HSV","DOWN");
                       return true;

                    case MotionEvent.ACTION_MOVE:
                        movePointer(event.getX(), event.getY(), true);
                        setSatValCoords(event.getX(), event.getY());
                        sendRGBtoUpdate();
                        return false;

                    case MotionEvent.ACTION_UP:
                        setSatValCoords(event.getX(), event.getY());
                        sendRGBtoUpdate();
                        sendRGBtoServer();
                        return false;


                    default:

                        return true;

                }
            }

        });
    }

    private void setSatValCoords(float eventX, float eventY){  //sending coords based on input to SatValView
        float x, y;
        if (eventX > d.getRekt().width()) {
            x = d.getRekt().width();
        } else {
            x = eventX;
        }
        if (eventY > d.getRekt().height()) {
            y = d.getRekt().height();
        } else {
            y = eventY;
        }
        d.setXY(x, y);
    }

    private int makeColor() { //making color from SatValView and seekbar value
        float[] satVal = d.getSatVal();
        //
        float[] f = new float[3];
        f[0] = getHue();
        f[1] = satVal[0];
        f[2] = satVal[1];
        return Color.HSVToColor(f);

    }


    private int[] hueArray() {

        int[] hue = new int[361];


        for (int i = 0; i < hue.length; i++) {
            hue[i] = Color.HSVToColor(new float[]{i, 1f, 1f});
        }
        return hue;
    }

    public void setColor(int c) { //setting the SatValView and the seekbar values
        float[] a = new float[3];
        Color.colorToHSV(c, a);
        seekBar.setProgress(Math.round(a[0]));
        d.setSatVal(a[1], a[2]);
        movePointer(a[1], a[2], false);
        String strColor = String.format("#%06X", 0xFFFFFF & c);
        buttonHSVPicker.setText(strColor);

    }

    private void movePointer(float x, float y, boolean withMargin){
       // if(withMargin){
            i.setX(x);
            i.setY(y);
   /*     }
        else{
            int leftPadding = d.getPaddingLeft();
            int topPadding = d.getPaddingTop();

            float sat = x * d.getWidth() + leftPadding;
            float val = d.getHeight() - (topPadding + y * d.getHeight());
            i.setX(sat);
            i.setY(val);
        }*/
    }

    private void sendRGBtoServer() {
        ((MainActivity) getActivity()).sendHex(makeColor());
    }


    private void sendRGBtoUpdate() { //send color to be displayed on button and toolbar
        int c = makeColor();
        ((MainActivity) getActivity()).updateColor(c);
        String strColor = String.format("#%06X", 0xFFFFFF & c);
        buttonHSVPicker.setText(strColor);
    }


    private void updateSatVal(int progress) {
        d.setHue(Color.HSVToColor(new float[]{progress, 1f, 1f}));
        d.invalidate();
    }

    private int getHue() {
        return seekBar.getProgress();
    }


    private void showDialog() { //inflating same dialog as in RGBSlider
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        View v = layoutInflaterAndroid.inflate(R.layout.dialog_hex_input, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final EditText input = (EditText) v.findViewById(R.id.inputLayout);

        input.setText((CharSequence) buttonHSVPicker.getText());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(v);

        Button copy = (Button) v.findViewById(R.id.copy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("HEX-Value", String.valueOf(input.getText()));
                clipboard.setPrimaryClip(clip);
            }
        });

        builder.setPositiveButton(getString(ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO udate hex
                try {
                    int actC = Color.parseColor(String.valueOf(input.getText()));
                    setColor(actC);
                    sendRGBtoUpdate();
                    sendRGBtoServer();
                } catch (IllegalArgumentException e) {
                    Log.e("ERROR", e.getMessage());
                }
            }
        });

        builder.setNegativeButton(getString(cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
