package slg_aachen.de.shop_v01;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
    private ListView swatchListView;
    private ListAdapter listAdapter;


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


    /**
     * all database actions have been moved to async taks in case the request may take to long; avoid causing ANR
     */
    private class Getter extends AsyncTask<Void, Void, Swatch[]> { //this async is getting the palettes to fill the ListView; this one is commented

        @Override
        protected Swatch[] doInBackground(Void... params) {  //executed apart from UI-Thread
            Swatch[] sw;    //making the final result set
            dbHelper = new DBHelper(getContext());
            db = dbHelper.getReadableDatabase();     //get DBHelper and a database from which we can read
            Cursor c = db.rawQuery("SELECT rowid, c1, c2, c3, c4, c5 FROM palette", null); //executing SQL command on SQLite DB
            // Log.e("ERROR", DatabaseUtils.dumpCursorToString(c)); //This could be used to see the output of the database, very nice for debugging
            if (c != null && c.moveToFirst()) { //iterating Cursor object
                sw = new Swatch[c.getCount()]; //instancing final result

                for (int i = 0; i < c.getCount(); i++) {  //iterating through Cursor rows

                    int[] swatches = new int[c.getColumnCount()];  //each row is one palette, each color of a palette is a column


                    for (int a = 0; a < c.getColumnCount(); a++) { //iterating through all columns of one row to get all colors
                        if (c.getString(a) == null)
                            Log.e("PALETTE GETTER", "STRING null");  //bug fixed. string was null. weird.

                        else {
                            swatches[a] = Integer.parseInt(c.getString(a));  //load result into swatch
                        }
                    }
                    c.moveToNext();

                    sw[i] = new Swatch(swatches); //make swatch and load it into array of swatches, the result set

                }
                c.close(); //finish.
                actSwatches = sw;
                return sw; //finished.

            }

            return null;
        }

        @Override
        protected void onPostExecute(Swatch[] swatches) {
            if (swatches != null) {
                View view = getView();
                listAdapter = new ListAdapter(getContext(), R.layout.view_list_row_swatches, swatches);
                if (view != null) {
                    swatchListView = view.findViewById(R.id.listView);
                    swatchListView.setAdapter(listAdapter);
                    swatchListView.setOnItemClickListener((AdapterView.OnItemClickListener) getActivity());
                    listChanged();

                }
            }
            super.onPostExecute(swatches);
        }
    }

    public void insertPalette(Swatch s) { //insert swatch, starting Inserter
        Inserter inserter = new Inserter();
        inserter.execute(s);

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
            super.onPostExecute(aVoid);
        }
    }

    public void deletePalette(List<Integer> pos) { //delete multiple swatches, starting Deleter
        List<Integer> number = new List<>();
        for (pos.toFirst(); pos.hasAccess(); pos.next()) {
            number.append(actSwatches[pos.getContent()].getId());
        }
        Deleter deleter = new Deleter();
        deleter.execute(number);


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
            super.onPostExecute(aVoid);
        }
    }

    /*public void TEXTZWECKE() {  //JUST FOR TESTING PURPOSES. CLEARS DB
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM palette");

    }*/

    public Swatch getSwatchAt(int pos) {
        return listAdapter.getItem(pos);
    }

    public void listChanged() {
        if (swatchListView != null) {
            swatchListView.clearChoices();
            listAdapter.notifyDataSetChanged();
            //  ((MainActivity) getActivity()).clearCheckedList();
        }
    }

    public void scrollToLast() { //see method name
        if (swatchListView != null) {

            swatchListView.setSelection(listAdapter.getCount() - 1);
        }
    }

    public void actionTransfer() {
        Log.e("D", "actionTRANSFER");
    }

    public void actionDelete() {
        Log.e("D", "actionDEL");
    }

    public void actionAdd() {
        Log.e("D", "actioADD");
    }
}

