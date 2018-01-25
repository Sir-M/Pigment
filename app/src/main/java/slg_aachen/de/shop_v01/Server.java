package slg_aachen.de.shop_v01;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Not really a server. Also badly named.
 * This is connecting to the desktop component, which is hosting a simple socket server
 * Then sending a color in Hexadecimal String format.
 *
 * Async Task
 */
class Server extends AsyncTask<String, Void, Void> {


    protected Void doInBackground(String... params) {


        String t = params[0];
        String c = params[1];
        try {
            Log.i("ServerAsync", "connect...");
            Socket sock = new Socket(t, 8080);
            Log.i("ServerAsync", "connected!");

            DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
            outToServer.writeBytes(c + '\n');

            Log.i("ServerAsync", "message send!");
            sock.close();

        } catch (IOException e) {
            Log.e("ERROR EXP SERVER", e.getMessage());
        }
        return null;
    }

}