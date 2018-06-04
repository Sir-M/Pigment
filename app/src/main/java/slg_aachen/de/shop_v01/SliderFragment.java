package slg_aachen.de.shop_v01;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.R.string.cancel;
import static android.R.string.ok;
import static android.graphics.Color.parseColor;

/*
*   Fragment no. 1
*   This Activity is used to choose colors in the RGB system
*   Using three sliders
*   Also featuring a button. WOW!
 */
public class SliderFragment extends Fragment {
    private int p;
    private SeekBar seekBarR, seekBarG, seekBarB;
    private TextView textViewR, textViewG, textViewB;
    private Button buttonHEX;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_slider_picker, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        seekBarR = (SeekBar) view.findViewById(R.id.seekBar);
        seekBarG = (SeekBar) view.findViewById(R.id.seekBar2);
        seekBarB = (SeekBar) view.findViewById(R.id.seekBar3);

        textViewR = (TextView) view.findViewById(R.id.textView);
        textViewG = (TextView) view.findViewById(R.id.textView2);
        textViewB = (TextView) view.findViewById(R.id.textView3);


        buttonHEX = (Button) view.findViewById(R.id.button);
        buttonHEX.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();

            }

        });


        //Seekbars

        seekBarR.getProgressDrawable().setColorFilter(parseColor("#F44336"), PorterDuff.Mode.SRC_IN);
        seekBarR.getThumb().setColorFilter(parseColor("#F44336"), PorterDuff.Mode.SRC_IN);

        seekBarG.getProgressDrawable().setColorFilter(parseColor("#4CAF50"), PorterDuff.Mode.SRC_IN);
        seekBarG.getThumb().setColorFilter(parseColor("#4CAF50"), PorterDuff.Mode.SRC_IN);

        seekBarB.getProgressDrawable().setColorFilter(parseColor("#2196F3"), PorterDuff.Mode.SRC_IN);
        seekBarB.getThumb().setColorFilter(parseColor("#2196F3"), PorterDuff.Mode.SRC_IN);


        p = 66;

        seekBarR.setProgress(p);
        seekBarB.setProgress(p);
        seekBarG.setProgress(p);

        textViewR.setText(String.valueOf(p));
        textViewG.setText(String.valueOf(p));
        textViewB.setText(String.valueOf(p));

        //Seekbar listeners. WOW. SUCH AMAZEMENT.

        seekBarR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBarG, int progress, boolean fromUser) {
                textViewR.setText(String.valueOf(progress));
                if (fromUser)
                    sendRGBtoUpdate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBarG) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBarG) {
                sendRGBtoServer();
            }
        });


        seekBarG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBarG, int progress, boolean fromUser) {
                textViewG.setText(String.valueOf(progress));
                if (fromUser)
                    sendRGBtoUpdate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBarG) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBarG) {
                sendRGBtoServer();
            }
        });


        seekBarB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBarB, int progress, boolean fromUser) {
                textViewB.setText(String.valueOf(progress));
                if (fromUser)
                    sendRGBtoUpdate();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBarB) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBarB) {
                sendRGBtoServer();

            }
        });


        //small buttons to de- or increase the seekbar value by one

        Button dec1 = (Button) view.findViewById(R.id.dec1);
        Button dec2 = (Button) view.findViewById(R.id.dec2);
        Button dec3 = (Button) view.findViewById(R.id.dec3);

        Button inc1 = (Button) view.findViewById(R.id.inc1);
        Button inc2 = (Button) view.findViewById(R.id.inc2);
        Button inc3 = (Button) view.findViewById(R.id.inc3);

        dec1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarR.getProgress() > 0) {
                    seekBarR.setProgress(seekBarR.getProgress() - 1);
                    sendRGBtoUpdate();
                    sendRGBtoServer();
                }
            }
        });
        dec2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarG.getProgress() > 0) {
                    seekBarG.setProgress(seekBarG.getProgress() - 1);
                    sendRGBtoUpdate();
                    sendRGBtoServer();
                }
            }
        });
        dec3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarB.getProgress() > 0) {
                    seekBarB.setProgress(seekBarB.getProgress() - 1);
                    sendRGBtoUpdate();
                    sendRGBtoServer();
                }
            }
        });
        inc1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarG.getProgress() < 255) {
                    seekBarG.setProgress(seekBarR.getProgress() + 1);
                    sendRGBtoUpdate();
                    sendRGBtoServer();
                }
            }
        });
        inc2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarG.getProgress() < 255) {
                    seekBarG.setProgress(seekBarG.getProgress() + 1);
                    sendRGBtoUpdate();
                    sendRGBtoServer();
                }
            }
        });
        inc3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarB.getProgress() < 255) {
                    seekBarB.setProgress(seekBarB.getProgress() + 1);
                    sendRGBtoUpdate();
                    sendRGBtoServer();
                }
            }
        });


    }

    private void sendRGBtoUpdate() {
        int c = makeColor();
        ((MainActivity) getActivity()).updateColor(c);
        String strColor = String.format("#%06X", 0xFFFFFF & c);
        buttonHEX.setText(strColor);
    }


    private void sendRGBtoServer() {
        ((MainActivity) getActivity()).sendHex(makeColor());
    }


    public void setColor(int c) {
        seekBarR.setProgress(Color.red(c));
        seekBarB.setProgress(Color.blue(c));
        seekBarG.setProgress(Color.green(c));
        String strColor = String.format("#%06X", 0xFFFFFF & c);
        buttonHEX.setText(strColor);
    }

    private int makeColor() { //combining the seekbar vlaues to get a rgb color
        int r = seekBarR.getProgress();
        int g = seekBarG.getProgress();
        int b = seekBarB.getProgress();
        return Color.rgb(r, g, b);
    }

    private void showDialog() { //showing a dialog to change the Hexadecial value and/or copy it to the users clipboard
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        View v = layoutInflaterAndroid.inflate(R.layout.dialog_hex_input, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final EditText input = (EditText) v.findViewById(R.id.inputLayout);

        input.setText(buttonHEX.getText());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(v);
        Button copy = (Button) v.findViewById(R.id.copy);
        copy.setOnClickListener(new OnClickListener() {
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

