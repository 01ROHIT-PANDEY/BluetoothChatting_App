package com.example.bluetoothchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlendMode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class BluetoothChatService {

    private static final UUID MY_UUID = UUID.fromString("188c5bda-d1b6-464a-8074-c5deaad3fa36");
    BluetoothAdapter BtAdapter;
    private Handler handler;
    private static final String NAME = "BluetoothChat";
    private int mState;
    private int mNewState;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTION_FAILED=4;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final int MESSAGE_READ_IMAGE = 6;
    public static final int MESSAGE_READ_AUDIO = 7;
    public static final int MESSAGE_READ_TEXT = 8;

    public static final int MESSAGE_WRITE_IMAGE = 9;
    public static final int MESSAGE_WRITE_AUDIO = 10;
    public static final int MESSAGE_WRITE_TEXT = 11;
    private String DEVICE_NAME;
    private String DEVICE_ADDRESS;




}
