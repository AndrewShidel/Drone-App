package shidel.droneapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Message", "test");
        //Position p = new Position(this);
        MainLoop loop = new MainLoop(MainActivity.this);

        try {
            loop.start(null);
        } catch(IOException e) {
            ((TextView)this.findViewById(R.id.debugX)).setText("Could not connect the server.");
        }
    }
}
