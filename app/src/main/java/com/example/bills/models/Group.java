package com.example.bills.models;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

public class Group {
    private String groupId;
    private String adminId;
    private String groupName;
    private ArrayList<String> participants = new ArrayList<>();

    public Group() {

    }

    public Group(String groupId, String adminId, String groupName) {
        this.groupId = groupId;
        this.adminId = adminId;
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public void setParticipants(JSONArray participantsJSON) throws JSONException {
        for(int i = 0; i < participantsJSON.length(); i++) {
            this.participants.add(participantsJSON.get(i).toString());
        }
    }

    public void setParticipantsForInitial(String number) {
        this.participants.add(number);
    }

    @Override
    public String toString() {
        return "{groupId='" + groupId + '\'' +
                ", adminId='" + adminId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", participants=" + Arrays.toString(participants.toArray()) +
                '}';
    }
}
