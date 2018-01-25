package slg_aachen.de.shop_v01;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import slg_aachen.de.shop_v01.SquarePicker.PickerFragment;

import static slg_aachen.de.shop_v01.R.string.cancel;
import static slg_aachen.de.shop_v01.R.string.connect;


/*
*The wrapper class of all fragments. Used for everything else too.
*
* TODO: ImageActivity zoom ;  touch indication on HSVPicker & Image ; zoom! error ; checkbox no. 3; InfoActivity in Dialog ; Ads? ; StayOpen in Menu
*
*/

public class MainActivity extends AppCompatActivity{// implements AdapterView.OnItemClickListener {


    private Toolbar myToolbar;

    private TabLayout tabLayout;
    private BottomNavigationViewEx bottomNavigationView;
    private CustomViewPager viewPager;

    private Swatch swatches;
    private String ip, saveIP;
    private int actPos, actContrast;


    private boolean validIp, visibility;

    private ProgressBar mProgress;

    private List<Integer> checkedIndex;
    private int selectedCheckBoxes;

    private SmartFragmentStatePagerAdapter smartFragmentStatePagerAdapter;

  /*  @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("TEST", "ONITEMCLICK");
        CheckBox s = (CheckBox) view.findViewById(R.id.checkBox);
        if (!s.isChecked()) {
            s.setChecked(true);
            checkedIndex.append(position);
        } else {
            s.setChecked(false);
            for (checkedIndex.toFirst(); checkedIndex.hasAccess(); checkedIndex.next()) {
                if (position == checkedIndex.getContent()) {
                    checkedIndex.remove();
                }
            }
        }
        selectedCheckBoxes = countChecked();
        invalidateOptionsMenu();
    }

    private int countChecked() {
        int i = 0;
        for (checkedIndex.toFirst(); checkedIndex.hasAccess(); checkedIndex.next()) {
            i++;
            if (i > 2)
                return 3;
        }
        return i;
    }
*/
  //  public void clearCheckedList() {
      //  checkedIndex = new List<>();
    //}


    //inner class to load and display fragment efficently

    private static class CustomPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static int NUM_ITEMS = 4;

        private CustomPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SliderFragment(); //loads the fragments at different places; c.f. the single fragments for more information
                case 1:
                    return new PickerFragment();
                case 2:
                    return new PaletteFragment();
                case 3:
                    return new ImageActivity();
                default:
                    return null;
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        } //no tab title
    }


    private class CheckIP extends AsyncTask<String, Void, Boolean> { //check the IP before connecting to the server. Sending message and waiting on return 'OK' from server.
        //TODO : ADD LINK TO DOWNLOAD THE SERVER PART IN THE APP AND IN HERE TOO

        @Override
        protected void onPreExecute() {
            mProgress.setVisibility(View.VISIBLE);
        }


        @Override
        protected Boolean doInBackground(String... params) {
            boolean t = isReachable(params[0]);

            if (t) {
                ip = params[0];
                validIp = true;
                saveIP = ip;
            }

            return t;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.e("CHECKIP", "FINISHED");

            mProgress.setVisibility(View.GONE);
            if (validIp) {
                invalidateOptionsMenu();
            }
        }


        private boolean isReachable(String a) {
            try {
                Log.i("CheckIP", "connect...");
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress(a, 8080), 7000); //try to connect to socket with timeout = 7s

                Log.i("CheckIP", "connected!");

                DataInputStream inputStream = new DataInputStream(sock.getInputStream());

                DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
                outToServer.writeBytes("CONNECT" + '\n');

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String s = reader.readLine();
                if (s.startsWith("OK")) {
                    return true;
                }

                reader.close();
                outToServer.close();
                sock.close();
                Log.e("CheckIP", "Error");
                return false;

            } catch (SocketTimeoutException e) { //catching timeout
                Log.e("CheckIP ERROR TIMEOUT", e.getMessage());
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.timeout, Toast.LENGTH_LONG).show();
                    }
                });

                return false;
            } catch (IOException e) {
                Log.e("CheckIP ERROR", e.getMessage());
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.an_error, Toast.LENGTH_LONG).show();
                    }
                });


                return false;

            }

        }
    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smartFragmentStatePagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());

        ip = "";
        validIp = false;
        visibility = false;
        swatches = new Swatch(0);

        checkedIndex = new List<>();


        mProgress = (ProgressBar) findViewById(R.id.progressBar1); //indeterminate progress bar shown upon start of async tasks

        actContrast = Color.WHITE;
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottom);
        bottomNavigationView.enableAnimation(false);        //customize the bottom navigation bar
        bottomNavigationView.enableShiftingMode(false);
        actPos = bottomNavigationView.getCurrentItem();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem d) {

                int a = bottomNavigationView.getMenuItemPosition(d);
                actPos = a;
                int actCol = swatches.getSwatch(a);
                setAllSetters(actCol);
                updateColor(actCol);
                sendHex(actCol);
                return true;
            }
        });


        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(smartFragmentStatePagerAdapter);
        viewPager.setPagingEnabled(true);
        viewPager.setOffscreenPageLimit(3);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLayout.setScrollPosition(tab.getPosition(), 0f, false);
                if (tab.equals(tabLayout.getTabAt(2))) {

                    visibility = true;
                    invalidateOptionsMenu();
                } else {

                    visibility = false;
                    invalidateOptionsMenu();

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        invalidateOptionsMenu();
        setTabIcons(true); //white
        setPaging(false);


    }


    public void updateColor(int c) {    //updates the colors of the top toolbar, currently used in: RGBSliderActivity, ImageActivity, HSVPicker
        myToolbar.setBackgroundColor(c);

        int contrast = myYIQ(c);
        actContrast = contrast;
        invalidateOptionsMenu();
        myToolbar.setTitleTextColor(contrast);
        setStatusBarColor(c);
        tabLayout.setBackgroundColor(c);
        tabLayout.setSelectedTabIndicatorColor(contrast);

        ActivityManager.TaskDescription taskDescription = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            taskDescription = new ActivityManager.TaskDescription(getString(R.string.app_name), BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_pigmentv3), c);
            this.setTaskDescription(taskDescription);
        }


        if (contrast == Color.WHITE) {
            setTabIcons(true); //color is white
        } else {
            setTabIcons(false); //color is not white (black)
        }
        swatches.updateSwatch(bottomNavigationView.getIconAt(actPos), c, actPos);


    }

    public void sendHex(int c) {      //create new server from Server.class and launch it with the IP and the HEX-Code
        setAllSetters(c);
        if (validIp) {
            String strColor = String.format("#%06X", 0xFFFFFF & c);
            Server s = new Server();
            s.execute(ip, strColor);
        }
    }


    private int myYIQ(int v) {     //Function to determine the contrast of a color and if black or white should be applied
        int q = ((Color.red(v)) * 299 + (Color.green(v)) * 587 + (Color.blue(v)) * 114) / 1000;
        if (q >= 128)
            return Color.BLACK;
        else
            return Color.WHITE;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {    //create the menu in the top toolbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbarmenu, menu);
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem del = menu.findItem(R.id.action_delete);
        MenuItem add = menu.findItem(R.id.action_add);
        MenuItem tra = menu.findItem(R.id.action_transfer);
        MenuItem con = menu.findItem(R.id.action_connect);

        if (validIp)
            con.setIcon(R.drawable.ic_cast_connected_black_24dp);
        else
            con.setIcon(R.drawable.ic_cast_black_24dp);


        if (visibility) {
            if (selectedCheckBoxes == 0) {
                add.setVisible(true);
                tra.setVisible(false);
                del.setVisible(false);
            } else if (selectedCheckBoxes == 1) {

                add.setVisible(true);
                tra.setVisible(true);
                del.setVisible(true);
            } else {
                tra.setVisible(false);
                add.setVisible(true);
                del.setVisible(true);
            }
        }

        Drawable d2 = add.getIcon();
        Drawable d3 = tra.getIcon();
        Drawable d4 = del.getIcon();
        Drawable d1 = con.getIcon();

        if (d2 != null) {
            d2.setColorFilter(actContrast, PorterDuff.Mode.SRC_ATOP);
        }
        if (d3 != null) {
            d3.setColorFilter(actContrast, PorterDuff.Mode.SRC_ATOP);
        }
        if (d3 != null) {
            d3.setColorFilter(actContrast, PorterDuff.Mode.SRC_ATOP);
        }
        if (d4 != null) {
            d4.setColorFilter(actContrast, PorterDuff.Mode.SRC_ATOP);
        }
        if (d1 != null) {
            d1.setColorFilter(actContrast, PorterDuff.Mode.SRC_ATOP);
        }


        return true;
    }


    private void showDialog() {  //shows dialog and asks for IP of the desktop component
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View v = layoutInflaterAndroid.inflate(R.layout.dialog_ip_input_v2, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input1 = (EditText) v.findViewById(R.id.editTextIP1);
        final EditText input2 = (EditText) v.findViewById(R.id.editTextIP2);
        final EditText input3 = (EditText) v.findViewById(R.id.editTextIP3);
        final EditText input4 = (EditText) v.findViewById(R.id.editTextIP4);
        if (saveIP != null) {
            String[] splitted = saveIP.split("[.]");
            if (splitted.length == 4) {
                input1.setText(splitted[0]);
                input2.setText(splitted[1]);
                input3.setText(splitted[2]);
                input4.setText(splitted[3]);
            }
        }


        Button remove = (Button) v.findViewById(R.id.removeText);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input1.setText("");
                input2.setText("");
                input3.setText("");
                input4.setText("");

            }
        });


        builder.setView(v);

        builder.setPositiveButton(getString(connect), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = input1.getText() + "." + input2.getText() + "." + input3.getText() + "." + input4.getText();
                Log.e("IP?", s);

                CheckIP ch = new CheckIP();
                ch.execute(s);


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

    private void showDialogWifi(boolean wifiOn) {


        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);


        if (wifiOn)
            builder1.setMessage(R.string.wifi_warning_connection);
        else
            builder1.setMessage(R.string.wifi_warning_off);

        builder1.setPositiveButton(
                R.string.settings,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                    }
                });

        builder1.setNegativeButton(
                getString(cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Handle presses on the Toolbar items
        PaletteFragment paletteActivity = (PaletteFragment) smartFragmentStatePagerAdapter.getRegisteredFragment(2);


        switch (item.getItemId()) {
            case R.id.action_connect:
                if (validIp) {  //Check if user just wants to deactivate connection
                    validIp = false;
                    invalidateOptionsMenu();

                } else {
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

                    if (wifiManager != null) {
                        if (wifiManager.isWifiEnabled()) {   //Check if Wifi is on

                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                            if (wifiInfo != null) {  //Check if Info about Wifi is not null

                                if (wifiInfo.getNetworkId() != -1) {


                                    showDialog();

                                } else {
                                    showDialogWifi(true);
                                }

                            } else {
                                Log.e("MainActivity", "WifiInfo null");
                            }
                        } else {
                            showDialogWifi(false);
                        }
                    } else {
                        Log.e("MainActivity", "WifiManager null");
                    }
                }
                return true;
            case R.id.action_add:
                paletteActivity.actionAdd();
                /*
                paletteActivity.insertPalette(swatches);

                for (checkedIndex.toFirst(); checkedIndex.hasAccess(); checkedIndex.next()) {
                    checkedIndex.remove();
                }
                selectedCheckBoxes = 0;
                paletteActivity.listChanged();
                invalidateOptionsMenu();
                paletteActivity.scrollToLast();

*/
                return true;
            case R.id.action_delete:
                paletteActivity.actionDelete();

                /*
                if (checkedIndex != null && !checkedIndex.isEmpty()) {

                    paletteActivity.deletePalette(checkedIndex);
                    for (checkedIndex.toFirst(); checkedIndex.hasAccess(); checkedIndex.next()) {
                        checkedIndex.remove();
                    }
                    selectedCheckBoxes = 0;
                    paletteActivity.listChanged();
                    invalidateOptionsMenu();
                    paletteActivity.scrollToLast();

                }
*/
                return true;

            case R.id.action_transfer:
                paletteActivity.actionTransfer();
                /*checkedIndex.toFirst();
                selectedCheckBoxes = countChecked();
                if (selectedCheckBoxes == 1) {
                    checkedIndex.toFirst();
                    int pos = checkedIndex.getContent();
                    setAllSwatches(paletteActivity.getSwatchAt(pos));

                    paletteActivity.listChanged();
                    invalidateOptionsMenu();
                    paletteActivity.scrollToLast();

                }
                */
                return true;

            case R.id.action_info:
                Intent intent = new Intent(getApplicationContext(), Info2.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void setAllSwatches(Swatch pSwatch) {
        swatches = pSwatch;
        for (int i = 0; i < 5; i++) {
            bottomNavigationView.setCurrentItem(i);
            updateColor(pSwatch.getSwatch(i));
        }
        bottomNavigationView.setCurrentItem(0);
    }

    public void setAllSetters(int color) {  //setting colors in all fragments except PaletteActivity & ImageActivity

        SliderFragment s0 = (SliderFragment) smartFragmentStatePagerAdapter.getRegisteredFragment(0);
        PickerFragment s1 = (PickerFragment) smartFragmentStatePagerAdapter.getRegisteredFragment(1);
        if (s0 != null && s0.isAdded())
            s0.setColor(color);
        if (s1 != null && s1.isAdded())
            s1.setColor(color);

    }

    private static void setOverflowButtonColor(final Activity activity, final PorterDuffColorFilter colorFilter) { //TODO
        final String overflowDescription = activity.getString(R.string.overflow);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();

        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<View>();
                decorView.findViewsWithText(outViews, overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    return;
                }
                ImageView overflow = (ImageView) outViews.get(0);
                overflow.setColorFilter(colorFilter);
                //removeOnGlobalLayoutListener(decorView,this);
            }
        });
    }

    private void setStatusBarColor(int c) {  //Status Bar has a slightly darker tone than the Toolbar. Also setting the color of the task view. DEPENDING ON VERSION: above API 23, text is changing color

        //int color = c;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (actContrast == Color.BLACK) {
                    this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    this.getWindow().getDecorView().setSystemUiVisibility(0);
                }
            } else {
                float[] hsv = new float[3];
                Color.RGBToHSV(Color.red(c), Color.green(c), Color.blue(c), hsv);

                float v = hsv[2];

                if ((v - .35f) > 0) {
                    hsv[2] = hsv[2] - .35f;
                } else {
                    hsv[2] = 0;
                }
                c = Color.HSVToColor(hsv);
            }
            window.setStatusBarColor(c);
        }
    }


    //Setting the Tab Icons with a selector (opacity) and according to the above YIQ-Function to determine black or white icons
    private void setTabIcons(boolean white) {


        if (white) {
            TabLayout.Tab tabCall = tabLayout.getTabAt(0);
            if (tabCall != null)
                tabCall.setIcon(R.drawable.selector_white);
            TabLayout.Tab tabCall2 = tabLayout.getTabAt(1);
            if (tabCall2 != null)
                tabCall2.setIcon(R.drawable.selector2_white);
            TabLayout.Tab tabCall3 = tabLayout.getTabAt(2);
            if (tabCall3 != null)
                tabCall3.setIcon(R.drawable.selector3_white);
            TabLayout.Tab tabCall4 = tabLayout.getTabAt(3);
            if (tabCall4 != null)
                tabCall4.setIcon(R.drawable.selector4_white);

        } else {
            TabLayout.Tab tabCall = tabLayout.getTabAt(0);
            if (tabCall != null)
                tabCall.setIcon(R.drawable.selector);
            TabLayout.Tab tabCall2 = tabLayout.getTabAt(1);
            if (tabCall2 != null)
                tabCall2.setIcon(R.drawable.selector2);
            TabLayout.Tab tabCall3 = tabLayout.getTabAt(2);
            if (tabCall3 != null)
                tabCall3.setIcon(R.drawable.selector3);
            TabLayout.Tab tabCall4 = tabLayout.getTabAt(3);
            if (tabCall4 != null)
                tabCall4.setIcon(R.drawable.selector4);
        }

    }

    public void setPaging(boolean t) {
        viewPager.setPagingEnabled(t);
    }

}





