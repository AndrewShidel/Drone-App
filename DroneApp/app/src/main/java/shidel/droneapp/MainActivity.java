package shidel.droneapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import java.io.IOException;

import shidel.droneapp.USBInterface.MaestroSSC;

public class MainActivity extends Activity {
    private static MaestroSSC maestroSSC = null;
    private static Button armEscButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        armEscButton = (Button) findViewById(R.id.armESCsButton);
        SeekBar bar = (SeekBar) findViewById(R.id.seekBar);
        maestroSSC = new MaestroSSC(this, bar);
    }
    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                maestroSSC.setDevice(usbManager, device);
                armEscButton.setEnabled(true);
                armEscButton.setText("START!");
                armEscButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (maestroSSC != null) {
                            start();
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
    private void start() {
        final Motors motors = new Motors(maestroSSC);
        motors.armEscs(new Motors.MotorTaskCallback() {
            @Override
            public void finished() {
            try {
                new MainLoop(MainActivity.this).start(motors);
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        });
    }
}
