package slg_aachen.de.shop_v01;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;

/**
 * Fragment no. 3
 * This Activity is used to save previously made color compositions
 * c.f. the color shemes of Le Corbusier
 * <p>
 * http://www.lescouleurs.ch/
 * <p>
 * <p>
 * save, delete and load back palettes
 * showed in a listview with a checkbox
 */
public class PaletteFragment extends Fragment {
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Swatch[] actSwatches;
    private RecyclerView swatchRecyclerView;
    private SwatchAdapter swatchAdapter;
    private AdapterView.OnItemClickListener itemClick;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.activity_palette, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Getter g = new Getter();
        g.execute();
    }

    private void initListView(final Swatch[] swatches) {
        if (swatches != null) {
            View view = getView();
            swatchAdapter = new SwatchAdapter(getLayoutInflater(), swatches);
            swatchRecyclerView = view.findViewById(R.id.recylcerView);
            swatchRecyclerView.setAdapter(swatchAdapter);

            listChanged();
        }
    }

    public Swatch getSwatchAt(int pos) {
        return swatchAdapter.getItem(pos);
    }

    public void insertPalette(Swatch s) { //insert swatch, starting Inserter
        Inserter inserter = new Inserter();
        inserter.execute(s);

    }

    public void listChanged() {

        swatchAdapter.notifyDataSetChanged();
        ((MainActivity) getActivity()).setSelectedCheckBoxes(0);
    }

    public void deletePalette(List<Integer> pos) { //delete multiple swatches, starting Deleter
        List<Integer> number = new List<>();
        for (pos.toFirst(); pos.hasAccess(); pos.next()) {
            number.append(actSwatches[pos.getContent()].getId());
        }
        Deleter deleter = new Deleter();
        deleter.execute(number);


    }

    private void scrollToLast() { //see method name
        if (swatchRecyclerView != null) {
            swatchRecyclerView.scrollToPosition(swatchAdapter.getItemCount() - 1);
        }
    }

    /*public void TEXTZWECKE() {  //JUST FOR TESTING PURPOSES. CLEARS DB
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM palette");

    }*/

    public void actionTransfer() {
        Log.e("D", "actionTRANSFER");
        int pos = -1;
        for (int i = 0; i < swatchAdapter.getItemCount(); i++) {
            //CheckBox checkBox = swatchRecyclerView.getAdapter().getView(i, null, swatchRecyclerView).findViewById(R.id.checkBox);
            //= swatchRecyclerView.getChildAt(i).findViewById(R.id.checkBox);
            // if (checkBox != null && checkBox.isChecked()) {
            //   pos = i;
            //    checkBox.toggle();

            //          }
            if (pos != -1) {
                ((MainActivity) getActivity()).setAllSwatches(getSwatchAt(pos));
            }
            listChanged();
        }
    }

    public void actionDelete() {
        Log.e("D", "actionDEL");
        List<Integer> positions = new List<>();
        for (int i = 0; i < swatchAdapter.getItemCount(); i++) {
            CheckBox checkBox = swatchRecyclerView.getChildAt(i).findViewById(R.id.checkBox);
            if (checkBox != null && checkBox.isChecked()) {
                positions.append(i);
                checkBox.toggle();
            }
        }
        deletePalette(positions);

    }

    public void actionAdd(Swatch swatch) {
        Log.e("D", "actioADD");
        insertPalette(swatch);
    }

    /**
     * all database actions have been moved to async tasks in case the request may take to long; avoid causing ANR
     */
    private class Getter extends AsyncTask<Void, Void, Swatch[]> {              //this async is getting the palettes to fill the ListView; this one is commented

        @Override
        protected Swatch[] doInBackground(Void... params) {                     //executed apart from UI-Thread
            Swatch[] sw;                                                        //making the final result set
            dbHelper = new DBHelper(getContext());
            db = dbHelper.getReadableDatabase();                                //get DBHelper and a database from which we can read
            Cursor c = db.rawQuery("SELECT rowid, c1, c2, c3, c4, c5 FROM palette", null); //executing SQL command on SQLite DB
            // Log.e("ERROR", DatabaseUtils.dumpCursorToString(c));             //This could be used to see the output of the database, very nice for debugging
            if (c != null && c.moveToFirst()) {                                 //iterating Cursor object
                sw = new Swatch[c.getCount()];                                  //instancing final result

                for (int i = 0; i < c.getCount(); i++) {                        //iterating through Cursor rows

                    int[] swatches = new int[c.getColumnCount()];               //each row is one palette, each color of a palette is a column


                    for (int a = 0; a < c.getColumnCount(); a++) {              //iterating through all columns of one row to get all colors
                        if (c.getString(a) == null)
                            Log.e("PALETTE GETTER", "STRING null");   //bug fixed. string was null. weird.

                        else {
                            swatches[a] = Integer.parseInt(c.getString(a));     //load result into swatch
                        }
                    }
                    c.moveToNext();

                    sw[i] = new Swatch(swatches);                               //make swatch and load it into array of swatches, the result set

                }
                c.close();                                                      //finish.
                actSwatches = sw;
                return sw;                                                      //finished.

            }

            return null;
        }

        @Override
        protected void onPostExecute(Swatch[] swatches) {
            initListView(swatches);
            super.onPostExecute(swatches);
        }
    }

    private class Inserter extends AsyncTask<Swatch, Void, Void> { //async to insert a palette at the end of the ListView; for comments check the Getter

        @Override
        protected Void doInBackground(Swatch... params) {
            Swatch s = params[0];
            dbHelper = new DBHelper(getContext());
            db = dbHelper.getWritableDatabase();
            db.execSQL("INSERT INTO palette (c1, c2, c3, c4, c5) VALUES" + "(" + s.getSwatch(0) + ", " + s.getSwatch(1) + ", " + s.getSwatch(2) + ", " + s.getSwatch(3) + ", " + s.getSwatch(4) + ")");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Getter g = new Getter();
            g.execute();
            listChanged();
            scrollToLast();
            super.onPostExecute(aVoid);
        }
    }

    private class Deleter extends AsyncTask<List, Void, Void> {//Deletes palette at given index ;for comments check the Getter

        @Override
        protected Void doInBackground(List... params) {
            List number = params[0];
            dbHelper = new DBHelper(getContext());
            db = dbHelper.getWritableDatabase();
            for (number.toFirst(); number.hasAccess(); number.next()) {
                Object index = number.getContent();
                Cursor c = db.rawQuery("SELECT rowid, c1, c2, c3, c4, c5 FROM palette WHERE rowid=" + index, null);
                if (c.moveToFirst()) {
                    db.execSQL("DELETE FROM palette WHERE rowid=" + index);
                } else {
                    Log.e("Error", "delete : id not found");
                }
                c.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Getter g = new Getter();
            g.execute();
            listChanged();
            scrollToLast();
            super.onPostExecute(aVoid);
        }
    }
}