package slg_aachen.de.shop_v01;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

/**
 * Not supporting List<> or java lists.
 * Just badly named and is loading Swatches
 *
 * PLEASE SEE THE VIEWHOLDER PATTERN
 *
 * https://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
 *
 */

class ListAdapter extends ArrayAdapter<Swatch> {//implements Checkable{

    private Context con;
    private Swatch[] swatches;
    private ViewHolder r;


    ListAdapter(@NonNull Context context, @LayoutRes int resource, Swatch[] s) {
        super(context, resource, s);
        con = context;
        swatches = s;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            r = new ViewHolder();
            row = inflater.inflate(R.layout.view_list_row_swatches, parent, false);
            r.v1 = row.findViewById(R.id.ic1);
            r.v2 = row.findViewById(R.id.ic2);
            r.v3 = row.findViewById(R.id.ic3);
            r.v4 = row.findViewById(R.id.ic4);
            r.v5 = row.findViewById(R.id.ic5);
            r.box = row.findViewById(R.id.checkBox);
            row.setTag(r);
        } else {
            r = (ViewHolder) row.getTag();
        }
        Swatch swatch = swatches[position];

        int c0 = swatch.getSwatch(0);
        r.v1.setColorFilter(c0, PorterDuff.Mode.SRC_ATOP);

        int c1 = swatch.getSwatch(1);

        r.v2.setColorFilter(c1, PorterDuff.Mode.SRC_ATOP);

        int c2 = swatch.getSwatch(2);
        r.v3.setColorFilter(c2, PorterDuff.Mode.SRC_ATOP);

        int c3 = swatch.getSwatch(3);

        r.v4.setColorFilter(c3, PorterDuff.Mode.SRC_ATOP);

        int c4 = swatch.getSwatch(4);
        r.v5.setColorFilter(c4, PorterDuff.Mode.SRC_ATOP);

       /*r.box.setOnClickListener(new View.OnClickListener() {
           @Override
          public void onClick(View view) {
               Log.e("FINALLY","A CLICK");

           }
       }); */

        return row;
    }

    public void setCheckBox(boolean b) {
        r.box.setChecked(b);
    }

    private static class ViewHolder {
        ImageView v1;
        ImageView v2;
        ImageView v3;
        ImageView v4;
        ImageView v5;
        CheckBox box;
    }
}





