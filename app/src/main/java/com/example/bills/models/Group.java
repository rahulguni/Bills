package com.example.bills.models;

public class Group {
    private String groupId;
    private String adminId;
    private String groupName;

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
}
