package com.example.bluetoothchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE = 3;
    private BluetoothChatService mChatService = null;
    public  static BluetoothAdapter BtAdapter;

    private static final int SELECT_IMAGE = 11;
    private static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 2;

    private static String mFileName = null;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissiontoRecordAccepted = false;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private static final String LOG_TAG = "AudioRecordTest";

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private StringBuffer mOutStringBuffer;
    private ListView mConversationView;
    private EditText mEditText;
    private ImageButton mButtonSend;
    private TextView connectionStatus;
    ChatMessageAdapter chatMessageAdapter;
    String fileName = null;
    Bitmap imageBitmap;
    private static final int CAMERA_REQUEST = 1888;


    private ImageView fullscreen;

    private boolean isGroupChat = false;

    private ArrayList<getDeviceInfo> users;

    private final static String TAG = "ChatActivity";
    private final static int MAX_IMAGE_SIZE = 200000;

    private HashMap<String, String> macToUser = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       BtAdapter=BluetoothAdapter.getDefaultAdapter();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_make_discoverable:
                ensureDiscoverable();
                return true;

            case R.id.menu_search_devices:
                Intent bluetoothIntent = new Intent(getApplicationContext(),
                        ShowDevices.class);
                startActivityForResult(bluetoothIntent, REQUEST_CONNECT_DEVICE);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void ensureDiscoverable()
    {
        Toast.makeText(getApplicationContext(),"Start Discovering",Toast.LENGTH_LONG).show();
        if (BtAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String macAddress = data.getExtras()
                            .getString(ShowDevices.EXTRA_DEVICE_ADDRESS);
                    connectDevice(macAddress);
                }
                break;
        }
    }
    public void connectDevice(String macAddress)
    {
        BluetoothDevice device =BtAdapter.getRemoteDevice(macAddress);
        String mConnectedDeviceAddress = macAddress;
        Toast.makeText(getApplicationContext(),"Connect Request",Toast.LENGTH_LONG).show();
        //mChatService.connect(device);

    }


}
