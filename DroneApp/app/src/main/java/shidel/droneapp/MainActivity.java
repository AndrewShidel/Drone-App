package shidel.droneapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;

import shidel.droneapp.USBInterface.MaestroSSC;


public class MainActivity extends Activity {
    private MaestroSSC maestroSSC;

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
        maestroSSC = new MaestroSSC();
    }
    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
//		Log.d(TAG, "onResume(" + intent + ")");
        String action = intent.getAction();

        if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                maestroSSC.setDevice(usbManager, device);

                Motors motors = new Motors(maestroSSC);
                motors.armEscs(new Motors.MotorTaskCallback() {
                    @Override
                    public void finished() {
                        try {
                            new MainLoop(MainActivity.this).start(new Controller());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                maestroSSC.setDevice(null, null);
            } else {
                Log.d("debug", "Unexpected Action=" + action.toString());
            }
        }
    }
}
