package slg_aachen.de.shop_v01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

/*
*   Small Activity to get information about the connection protocol and process
*   also displaying information about the developer
 */

public class Info2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(toolbar != null) {
            setSupportActionBar(toolbar);getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

    }
}
