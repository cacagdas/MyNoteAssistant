package cacagdas.mynoteassistant.com;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by cacagdas on 15.02.2018.
 */

public class NoteListAdapter extends ArrayAdapter<Note> {

    List<Note> noteList;

    public NoteListAdapter(@NonNull Context context, @LayoutRes int resource, List<Note> noteList) {
        super(context, resource, noteList);
        this.noteList = noteList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        Note currentNote = noteList.get(position);

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.note_list_item, parent, false);
        }

        TextView tvNoteHeader = (TextView) listItemView.findViewById(R.id.tvNoteHeader);
        tvNoteHeader.setText(currentNote.getNoteHeader());

        TextView tvNoteContent = (TextView) listItemView.findViewById(R.id.tvNoteContent);
        tvNoteContent.setText(currentNote.getNoteContent());


        return listItemView;
    }
}
