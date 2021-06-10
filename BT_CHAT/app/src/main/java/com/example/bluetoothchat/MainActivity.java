package com.example.bluetoothchat;

import androidx.annotation.NonNull;
import java.util.Calendar;
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
import static com.example.bluetoothchat.BluetoothChatService.MESSAGE_READ_TEXT;
import static com.example.bluetoothchat.BluetoothChatService.MESSAGE_WRITE_TEXT;
import static com.example.bluetoothchat.MessageInstance.DATA_TEXT;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE = 3;
    private BluetoothChatService mChatService;
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


    private ArrayList<getDeviceInfo> users;

    private final static String TAG = "ChatActivity";
    private final static int MAX_IMAGE_SIZE = 200000;

    private HashMap<String, String> macToUser = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       BtAdapter=BluetoothAdapter.getDefaultAdapter();
       mEditText=findViewById(R.id.edit_text_text_message);
       mButtonSend=findViewById(R.id.btn_send);
       connectionStatus=findViewById(R.id.connection_status);
       mConversationView=findViewById(R.id.message_history);
       mConversationView.setAdapter(chatMessageAdapter);
       chatMessageAdapter= new ChatMessageAdapter(MainActivity.this, R.layout.chat_message);
       fullscreen = (ImageView) findViewById(R.id.fullscreen_image);
       if(mChatService==null)
       {
           setupChat();
       }

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
          mChatService.connect(device);
    }

    public void PhotoMessage(View view) {
        //permissionCheck();
    }

    public void CameraPhoto(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    static final SimpleDateFormat sdf=new SimpleDateFormat("y-MM-dd:HH:mm:ss");
    String prevSendTime=null;
    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            connectionStatus.setText(getResources().getString(R.string.connected));
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            connectionStatus.setText("Connecting");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        /*case BluetoothChatService.STATE_CONNECTION_FAILED:
                             connectionStatus.setText("Connecting");
                             break;*/
                        case BluetoothChatService.STATE_NONE:
                            connectionStatus.setText(getResources().getString(R.string.disconnected));
                            break;
                    }
                    break;
                case MESSAGE_WRITE_TEXT:
                    MessageInstance textWriteInstance = (MessageInstance) msg.obj;
                    byte[] writeBuf = (byte[]) textWriteInstance.getData();
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Calendar calendar = Calendar.getInstance();
                    String txtWriteTime = sdf.format(calendar.getTime());

                    // This is stored in milliseconds for time checking
                    String time = textWriteInstance.getTime();

                    if (prevSendTime == null) {
                        prevSendTime = time;
                    } else if (prevSendTime.equals(time)) {
                        Log.d(TAG, "Time equal, msg not repeated");
                        break;
                    }
                    prevSendTime = time;


                    String writeDisplayMessage = "Me: " + writeMessage + "\n" + "(" + txtWriteTime + ")";

                    chatMessageAdapter.add(new MessageInstance(true, writeDisplayMessage));
                    chatMessageAdapter.notifyDataSetChanged();
                    break;
                case MESSAGE_READ_TEXT:
                    MessageInstance msgTextData = (MessageInstance) msg.obj;
                    byte[] readBuf = (byte[]) msgTextData.getData();

                    Calendar cal = Calendar.getInstance();
                    String readTime = sdf.format(cal.getTime());

                    String message = new String(readBuf);
                    String connectedMacAddress = msgTextData.getMacAddress();



                    String displayMessage = msgTextData.getUserName() + ": " + message + "\n" + "(" + readTime + ")";

                    chatMessageAdapter.add(new MessageInstance(false, displayMessage));
                    chatMessageAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Text was read from " + msgTextData.getUserName() + ": " + msgTextData.getMacAddress());
                    break;

            }

        }

    };

    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();

                    sendMessage(message);


            }
            return true;
        }
    };
    private void setupChat() {
        // Initialize the compose field with a listener for the return key
        mEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget

                String message = mEditText.getText().toString();

                    sendMessage(message);

            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections

            Log.d(TAG, "setting up single chat");
            mChatService = new BluetoothChatService(handler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), R.string.not_connected,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            System.out.println("Message Length = " + message.length());

            Calendar calendar = Calendar.getInstance();
            String timeSent = sdf.format(calendar.getTime());
            mChatService.write(message.getBytes(), DATA_TEXT, timeSent);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mEditText.setText(mOutStringBuffer);
        }
    }


}
