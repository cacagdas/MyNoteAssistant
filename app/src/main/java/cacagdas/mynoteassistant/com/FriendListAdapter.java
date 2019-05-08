package cacagdas.mynoteassistant.com;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by cacagdas on 13.05.2018.
 */

public class FriendListAdapter extends ArrayAdapter<Friend> {

    List<Friend> friendList;
    LinearLayout layoutFriendListItem;
    TextView tvFriendId, tvFriendName;

    public FriendListAdapter(@NonNull Context context, @LayoutRes int resource, List<Friend> friendList) {
        super(context, resource, friendList);
        this.friendList = friendList;
    }

    public View getView (final int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        final Friend currentFriend = friendList.get(position);

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.friend_list_item, parent, false);
        }

        tvFriendName = (TextView) listItemView.findViewById(R.id.tvFriendName);
        tvFriendName.setText(currentFriend.getFriendName());

        tvFriendId = (TextView) listItemView.findViewById(R.id.tvFriendId);
        tvFriendId.setText(currentFriend.getFriendId());

        layoutFriendListItem = (LinearLayout) listItemView.findViewById(R.id.layoutFriendListItem);
        layoutFriendListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(tvFriendId.getText().toString(), tvFriendId.getText().toString());
                System.out.println("clip"+clip);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Copied to clipboard.", Toast.LENGTH_LONG).show();
            }
        });

        return listItemView;
    }
}
