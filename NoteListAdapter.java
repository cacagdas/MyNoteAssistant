package cacagdas.mynoteassistant.com;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import static cacagdas.mynoteassistant.com.MainActivity.noteListAdapter;

/**
 * Created by cacagdas on 15.02.2018.
 */

public class NoteListAdapter extends ArrayAdapter<Note> {

    List<Note> noteList;
    ViewHolder viewHolder;
    //ImageButton ibCompleted;


    public NoteListAdapter(@NonNull Context context, @LayoutRes int resource, List<Note> noteList) {
        super(context, resource, noteList);
        this.noteList = noteList;
        viewHolder = new ViewHolder();
    }

    public class ViewHolder {
        ImageButton ibCompleted;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        Note currentNote = noteList.get(position);

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.note_list_item, parent, false);
        }

        TextView tvNoteHeader = (TextView) listItemView.findViewById(R.id.tvNoteHeader);
        tvNoteHeader.setText(currentNote.getNoteHeader());

        TextView tvNoteContent = (TextView) listItemView.findViewById(R.id.tvNoteContent);
        tvNoteContent.setText(currentNote.getNoteContent());

        viewHolder.ibCompleted = (ImageButton) listItemView.findViewById(R.id.ibCompleted);

        viewHolder.ibCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteList.get(position);
                noteList.remove(position);
                noteListAdapter.notifyDataSetChanged();
            }
        });


        return listItemView;
    }



}
