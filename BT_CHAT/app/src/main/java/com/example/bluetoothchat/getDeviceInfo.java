package com.example.bluetoothchat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class getDeviceInfo  implements Parcelable {
    String name;
    String macAddress;

    getDeviceInfo(String name, String macAddress) {
        this.name = name;
        this.macAddress = macAddress;
    }

    private getDeviceInfo(Parcel in) {
        name = in.readString();
        macAddress = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(macAddress);
    }

    public static final Parcelable.Creator<getDeviceInfo> CREATOR = new Parcelable.Creator<getDeviceInfo>() {
        public getDeviceInfo createFromParcel(Parcel in) {
            return new getDeviceInfo(in);
        }

        public getDeviceInfo[] newArray(int size) {
            return new getDeviceInfo[size];

        }
    };

    static List<getDeviceInfo> getUsersInfo(String users) {
        String usersSplitUp[] = users.split("[\\r?\\n]+");

        List<getDeviceInfo> usersInfo = new ArrayList<>();

        for (int i = 0; i < usersSplitUp.length; i+= 2) {
            String name = usersSplitUp[i];
            String userMacAddress = usersSplitUp[i+1];
            usersInfo.add(new getDeviceInfo(name, userMacAddress));
        }

        return usersInfo;
    }

    public String getName(){
        return name;
    }
    public String getMacAddress(){
        return macAddress;
    }
}
