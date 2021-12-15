package com.example.bills;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bills.models.Group;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//For Miscellaneous Functions
public class Miscellaneous {
    public boolean checkTextFields(String[] fields) {
        for(int i = 0; i < fields.length; i++) {
            if(fields[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public String replaceWhiteSpace(String word) {
        String newString = word.replaceAll(" ", "%");
        return newString;
    }

    public String replacePercentage(String word) {
        String newString = word.replaceAll("%", " ");
        return newString;
    }

    //Add curr user to group
    public void addCurrUserToGroup(String groupId, String myPhone) {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference groupRef = userDatabase.child("Group").child(groupId).child("Participants");
        ArrayList<String> currPhones = new ArrayList<>();

        //Check for other Participants and append to the list
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()) {
                    String mPhone = ds.getValue(String.class);
                    currPhones.add(mPhone);
                }
                currPhones.add(myPhone);
                groupRef.setValue(currPhones);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.getMessage());
            }
        };

        groupRef.addListenerForSingleValueEvent(valueEventListener);
    }

    //Add currGroup to user's info
    public void addCurrGroupToUser(String currUserPhone, String groupId) {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference groupRef = userDatabase.child("User").child(currUserPhone).child("Groups");
        ArrayList<String> currGroupId = new ArrayList<>();

        //Check for other group ids and add them to arraylist
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()) {
                    String mGroupId = ds.getValue(String.class);
                    currGroupId.add(mGroupId);
                }
                currGroupId.add(groupId);
                groupRef.setValue(currGroupId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.getMessage());
            }
        };

        groupRef.addListenerForSingleValueEvent(valueEventListener);
    }

}
