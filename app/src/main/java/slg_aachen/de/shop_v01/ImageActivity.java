package slg_aachen.de.shop_v01;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment no. 4
 * @author Mirko
 * This is used to display a zoomable image
 * The image is opened either from the internal gallery or directly taken with the camera
 * It is then used to get the vibrant colors of the image or get a specific color value at one point
 */

public class ImageActivity extends Fragment {

    private static final int PICK_PHOTO = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    PhotoView photoView;
    boolean pickerOn;
    FloatingActionButton selectImage, picker, selectAll, delete, takePhoto;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) { //inflating view
        return inflater.inflate(R.layout.image_activity, container, false);

    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        photoView = (PhotoView) view.findViewById(R.id.photoView);
        pickerOn = false;
        selectImage = (FloatingActionButton) view.findViewById(R.id.floatingActionButton); //creating multiple FAB...
        picker = (FloatingActionButton) view.findViewById(R.id.floatingActionButton2);
        selectAll = (FloatingActionButton) view.findViewById(R.id.floatingActionButton3);
        delete = (FloatingActionButton) view.findViewById(R.id.floatingActionButton4);
        takePhoto = (FloatingActionButton) view.findViewById(R.id.floatingActionButton5);

        selectImage.setCompatElevation(4);
        takePhoto.setCompatElevation(4);

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //... and setting their listeners
                pickImage();
            }
        });
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(false);
                setPicker(false);
                photoView.setImageDrawable(null);
                pickerOn = false;
            }
        });
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Drawable drawable = photoView.getDrawable();

                if (drawable instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    if (bitmapDrawable.getBitmap() != null) {
                        createPaletteAsync(bitmapDrawable.getBitmap());  //creating the palette from the image
                        photoView.getAttacher().getDisplayRect();
                    }

                }
            }
        });
        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //depending on the state of the picker, enable or disable zoom
                Matrix matrix = new Matrix();
                photoView.getAttacher().getSuppMatrix(matrix);
                if (pickerOn) {
                    pickerOn = false;
                    photoView.setZoomable(true);
                    photoView.getAttacher().setDisplayMatrix(matrix);
                    setPicker(false);
                    // photoView.setImageBitmap(filteredBitmap);

                    ///float scale  = photoView.getScale();
                    //photoView.setZoomable(false);
                    // photoView.setScale(scale);
                } else {
                    pickerOn = true;
                    photoView.setZoomable(false);
                    photoView.getAttacher().setDisplayMatrix(matrix);
                    // Drawable d = view.getResources().getDrawable(, );
                    setPicker(true);
                    //    photoView.getAttacher().setOnClickListener(this);
                }
            }
        });
        photoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //get pixel with touch event
                if (pickerOn) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            getColor(event.getX(), event.getY());
                            return photoView.getAttacher().onTouch(v, event);

                        case MotionEvent.ACTION_DOWN:
                            Log.e("ACTIONS - IMAGE", "DOWN");
                            getColor(event.getX(), event.getY());
                            return true;
                    }
                }
                return photoView.getAttacher().onTouch(v, event);
            }
        });
    }

    private void setPicker(boolean on) {  //setting the states (on/off) of the eyedropper tool
        if (on) {
            Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_eyedropper_white_24dp);
            if (d != null) {
                picker.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                picker.setRippleColor(Color.parseColor("#42FFFFFF"));
                picker.setImageDrawable(d);
            }
        } else {
            Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_eyedropper_24dp);
            if (d != null) {
                picker.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                picker.setRippleColor(Color.parseColor("#42000000"));
                picker.setImageDrawable(d);
            }
        }
    }

    public void createPaletteAsync(Bitmap bitmap) {  //get the palette of five colors with android's 'Palette' class
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                Swatch extractedColors;
                java.util.List<Palette.Swatch> s = p.getSwatches();

                int def = Color.BLACK;
                int[] palette = new int[6];
                palette[0] = 0;
                palette[1] = p.getVibrantColor(def);
                //if(c1 == Color.BLACK)
                //     c1 = p.getLightMutedColor(def);
                palette[2] = p.getLightVibrantColor(def);
                palette[3] = p.getDarkVibrantColor(def);
                palette[4] = p.getMutedColor(def);
                palette[5] = p.getDarkMutedColor(def);
                for (int i = 1; i < 6; i++) {
                    if (palette[i] == def) {
                        for (Palette.Swatch a : s) {
                            if (a.getRgb() != def) {
                                palette[i] = a.getRgb();
                            }
                        }
                    }
                }
                //extractedColors = new Swatch(new int[]{c1, c2, c3, c4, c5});
                extractedColors = new Swatch(palette);
                updateMainSwatch(extractedColors);
            }
        });
    }

    private void getColor(float x, float y) { //get color at position on bitmap and setting it as actual color
        Bitmap image = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
        float width = image.getWidth();
        float height = image.getHeight();
        float[] points = new float[]{x, y};

        Matrix m = new Matrix();
        photoView.getAttacher().getImageMatrix().invert(m);
        m.mapPoints(points);

        if (points[0] < 0) {
            points[0] = 2;
        } else if (points[0] > width) {
            points[0] = width - 2;
        }
        if (points[1] < 0) {
            points[1] = 2;
        } else if (points[1] > height) {
            points[1] = height - 2;
        }
        int pixel = image.getPixel(Math.round(points[0]), Math.round(points[1]));
        ((MainActivity) getActivity()).updateColor(pixel);


    }


    private void updateMainSwatch(Swatch s) {
        ((MainActivity) getActivity()).setAllSwatches(s);
    }

    private void pickImage() {  //start intent to pick image from gallery, code 0
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO);
    }

    private void takePhoto() { //start intent to take an image with internal camera, code 1
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { //handle end of intent started just above
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO && resultCode == RESULT_OK) {
            if (data == null) {
                Log.e("ERROR", "IMAGEACTIVITY DATA NULL");
                return;
            }

            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());

               Bitmap d=  BitmapFactory.decodeStream(inputStream);
                photoView.setImageBitmap(d);
                setVisibility(true);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                photoView.setImageBitmap(imageBitmap);
                setVisibility(true);
            }
        }
    }

    public void setVisibility(boolean loaded) {
        if (loaded) {
            selectImage.setVisibility(View.GONE);
            picker.setVisibility(View.VISIBLE);
            selectAll.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        } else {
            selectImage.setVisibility(View.VISIBLE);
            picker.setVisibility(View.GONE);
            selectAll.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }
    }


}
