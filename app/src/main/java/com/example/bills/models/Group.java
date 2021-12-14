package com.example.bills.models;


import java.util.Arrays;

public class Group {
    private String groupId;
    private String adminId;
    private String groupName;
    private String[] participants;

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

    public String[] getParticipants() {
        return participants;
    }

    public void setParticipants(String[] participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "{groupId='" + groupId + '\'' +
                ", adminId='" + adminId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", participants=" + Arrays.toString(participants) +
                '}';
    }
}
