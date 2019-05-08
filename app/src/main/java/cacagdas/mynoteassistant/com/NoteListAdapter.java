package cacagdas.mynoteassistant.com;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cacagdas.mynoteassistant.com.CompletedListActivity.isActive;
import static cacagdas.mynoteassistant.com.MainActivity.completedListAdapter;
import static cacagdas.mynoteassistant.com.MainActivity.noteListAdapter;
import static cacagdas.mynoteassistant.com.NoteAddActivity.completedNoteList;
import static cacagdas.mynoteassistant.com.NoteAddActivity.currentFirebaseUser;
import static cacagdas.mynoteassistant.com.NoteAddActivity.noteList;

/**
 * Created by cacagdas on 15.02.2018.
 */

public class NoteListAdapter extends ArrayAdapter<Note> {

    List<Note> noteList;
    ViewHolder viewHolder;
    //ImageButton ibCompleted;
    public static DatabaseReference mDatabase;


    public NoteListAdapter(@NonNull Context context, @LayoutRes int resource, List<Note> noteList) {
        super(context, resource, noteList);
        this.noteList = noteList;
        viewHolder = new ViewHolder();
    }

    public static class ViewHolder {
        RelativeLayout layoutCompleted, layoutDelete, layoutSend;
        LinearLayout layoutListViewItem;
        SwipeLayout swipeLayout;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        final Note currentNote = noteList.get(position);

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.note_list_item, parent, false);
        }

        TextView tvNoteHeader = (TextView) listItemView.findViewById(R.id.tvNoteHeader);
        tvNoteHeader.setText(currentNote.getNoteHeader());

        TextView tvNoteContent = (TextView) listItemView.findViewById(R.id.tvNoteContent);
        tvNoteContent.setText(currentNote.getNoteContent());

        viewHolder.layoutCompleted = (RelativeLayout) listItemView.findViewById(R.id.layoutCompleted);
        if (isActive) {
            viewHolder.layoutCompleted.setVisibility(View.GONE);
        } else {
            viewHolder.layoutCompleted.setVisibility(View.VISIBLE);
        }
        viewHolder.layoutCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteList.get(position);


                final String userId = currentFirebaseUser.getUid();
                mDatabase = FirebaseDatabase.getInstance().getReference();
                String key = mDatabase.child("users").child(userId).child("completedNotes").push().getKey();
                Map<String, Object> postValues = currentNote.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/users/" + userId + "/completedNotes/" + key, postValues);
                mDatabase.updateChildren(childUpdates);

                DatabaseReference nRef = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(userId).child("notes");
                nRef.child(currentNote.getKey()).removeValue();

                //completedNoteList.add(currentNote);
                noteList.remove(position);
                completedListAdapter.notifyDataSetChanged();
                noteListAdapter.notifyDataSetChanged();
            }
        });

        viewHolder.layoutDelete = (RelativeLayout) listItemView.findViewById(R.id.layoutDelete);
        viewHolder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActive) {
                    DatabaseReference nRef = FirebaseDatabase.getInstance().getReference().child("users")
                            .child(currentFirebaseUser.getUid()).child("completedNotes");
                    nRef.child(completedNoteList.get(position).getKey()).removeValue();
                    completedNoteList.remove(position);
                    completedListAdapter.notifyDataSetChanged();
                } else if (!isActive) {
                    DatabaseReference nRef = FirebaseDatabase.getInstance().getReference().child("users")
                            .child(currentFirebaseUser.getUid()).child("notes");
                    nRef.child(currentNote.getKey()).removeValue();
                    noteList.remove(position);
                    noteListAdapter.notifyDataSetChanged();
                }

            }
        });


        viewHolder.layoutListViewItem = (LinearLayout) listItemView.findViewById(R.id.layoutListViewItem);
        viewHolder.layoutListViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strHeader = noteList.get(position).getNoteHeader();
                String strContent = noteList.get(position).getNoteContent();
                Intent i = new Intent(getContext(), NoteAddActivity.class);
                i.putExtra("strHeader", strHeader );
                i.putExtra("strContent", strContent);

                //startActivity(i);

                // TODO: FOR UPDATE THE NOTE OPEN NOTE ADD
                System.out.println("layoutListViewItem is clicked.");
            }
        });


        viewHolder.swipeLayout = (SwipeLayout) listItemView.findViewById(R.id.swipeLayout);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                layout.open();

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });

        return listItemView;
    }
}
