package cacagdas.mynoteassistant.com;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class FriendsActivity extends AppCompatActivity {

    EditText etFriendName, etFriendId;
    TextView tvUserId;
    Button btnAddFriend;
    String stFriendName, stFriendId;
    FriendListAdapter friendListAdapter;
    List<Friend> friendList = new ArrayList<>();
    ListView lvFriendList;

    DatabaseReference mDatabase;
    FirebaseUser currentFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentFirebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("friends");

        initViews();
        friendListAdapter = new FriendListAdapter(this, 0, friendList);
        lvFriendList.setAdapter(friendListAdapter);
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend();
            }
        });
        onFirebaseData();

        tvUserId.setText("My ID: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
        tvUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(tvUserId.getText().toString(), tvUserId.getText().toString());
                System.out.println(clip);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(FriendsActivity.this,"Copied to clipboard.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        stFriendName = etFriendName.getText().toString();
        stFriendId = etFriendId.getText().toString();

        if (TextUtils.isEmpty(stFriendName)) {
            etFriendName.setError("Required.");
            valid = false;
        } else {
            etFriendName.setError(null);
        }

        if (TextUtils.isEmpty(stFriendId)) {
            etFriendId.setError("Required.");
            valid = false;
        } else {
            etFriendId.setError(null);
        }

        return valid;
    }

    public void addFriend() {
        if (!validateForm()) {
            return;
        }

        stFriendName = etFriendName.getText().toString();
        stFriendId = etFriendId.getText().toString();

        Friend friend = new Friend(stFriendName, stFriendId);
        final String userId = currentFirebaseUser.getUid();
        String key = mDatabase.push().getKey();
        Map<String, Object> postValues = friend.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);
        mDatabase.updateChildren(childUpdates);

        etFriendName.setText("");
        etFriendId.setText("");
    }

    public void onFirebaseData() {
        System.out.println("email: "+currentFirebaseUser.getEmail());

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    HashMap<String, Object> hashMap = (HashMap<String, Object>) postSnapshot.getValue();

                    Friend friend = new Friend((String)hashMap.get("friendName"), (String)hashMap.get("friendId"));
                    friendList.add(friend);
                    System.out.println("friend: "+ friend.getFriendName());
                    System.out.println("friend: " + friend.toString());
                    friendListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initViews() {
        etFriendName = (EditText) findViewById(R.id.etFriendName);
        etFriendId = (EditText) findViewById(R.id.etFriendId);
        btnAddFriend = (Button) findViewById(R.id.btnAddFriend);
        tvUserId = (TextView) findViewById(R.id.tvUserId);
        lvFriendList = (ListView) findViewById(R.id.lvFriends);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
