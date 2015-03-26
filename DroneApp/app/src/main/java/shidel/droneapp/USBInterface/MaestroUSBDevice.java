package shidel.droneapp.USBInterface;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class MaestroUSBDevice {
    private static final String TAG = "MaestroUSBDevice";
    private UsbDevice device;
    private UsbDeviceConnection controlConnection;

    // Pololu Protocol Commands
    public static final int CMD_SET_POSITION = 0x85;
    public static final int CMD_SET_SPEED = 0x87;
    public static final int CMD_SET_ACCELERATION = 0x89;
    public static final int CMD_SET_PWM = 0x8A;
    public static final int CMD_GET_POSITION = 0x90;
    public static final int CMD_GET_MOVING_STATE = 0x93;
    public static final int CMD_GET_ERRORS = 0xA1;
    public static final int CMD_GO_HOME = 0xA2;

    public MaestroUSBDevice() {
    }

    public void sendCommand(int command, int channel, int value) {
        Log.d(TAG, "sendCommand(" + command + ", " + channel + ", " + value + ")");

        if (device == null) {
            Log.e(TAG, "sendCommand(): device is NULL!");
            return;
        }

        controlConnection.controlTransfer(0x40, command, value, channel, null, 0, 5000);
    }

    public int readInt() {
        if (device == null) {
            Log.e(TAG, "readInt(): device is NULL!");
            return 0;
        }

        // TODO Add code to read from device

        return 0;
    }
    UsbEndpoint endpoint;
    public void setDevice(UsbManager usbManager, UsbDevice usbDevice) {
        device = usbDevice;

        if (device != null) {
            usbDeviceDump(usbDevice);

            UsbInterface controlInterface = device.getInterface(1);

            endpoint = controlInterface.getEndpoint(0);

            UsbDeviceConnection connection = usbManager.openDevice(device);

            if (connection != null && connection.claimInterface(controlInterface, true)) {
                controlConnection = connection;
            } else {
                Log.e(TAG, "open connection FAIL");
                controlConnection = null;
            }
        }
    }

    public void usbDeviceDump(UsbDevice usbDevice) {
        if (usbDevice == null) {
            Log.e(TAG, "usbDevice is NULL!");
            return;
        }

        int numInterfaces = usbDevice.getInterfaceCount();

        Log.d(TAG, "Device found: VendorId=" + usbDevice.getVendorId() + " ProductId=" + usbDevice.getProductId());

        for (int i = 0; i < numInterfaces; i++) {
            UsbInterface usbInterface = usbDevice.getInterface(i);
            int numEndpoints = usbInterface.getEndpointCount();
            for (int j = 0; j < numEndpoints; j++) {
                UsbEndpoint usbEndpoint = usbInterface.getEndpoint(j);
            }
        }
    }
}
