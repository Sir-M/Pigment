package slg_aachen.de.shop_v01;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.widget.ImageView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Data class which is handling the display of one palette.
 * A palette has five colors.
 */
class Swatch {
    private int[] swatches;
    private int id;


     Swatch(int a) { //swatch can be created by either giving just an id and creating a white or black palette...
        swatches = new int[5];

        id = a;
        for (int i = 0; i < swatches.length; i++) {
            if(a == -1)
                swatches[i] = Color.BLACK;
            else
                swatches[i] = Color.WHITE;
        }
    }

     Swatch(int[] d) { //or giving an array with an integrated id
        swatches = new int[5];
        id = d[0] ;

        for(int i =1;i<d.length;i++){
            swatches[i-1] = d[i];

        }
    }

     void updateSwatch(ImageView i, int color, int index) { //updating a swatch updates also his displayed color
        if (i != null) {
            //  drawable.mutate();
            i.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        if (index < swatches.length) {
            swatches[index] = color;
        }
    }

     int getSwatch(int i) { //get one swatch in a palette
        if (i < swatches.length) {
            return swatches[i];
        }
        else{
        return -1;
        }
    }
    public int getId(){
        return id;
    }

}
