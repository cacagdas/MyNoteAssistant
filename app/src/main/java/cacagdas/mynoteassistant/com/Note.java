package cacagdas.mynoteassistant.com;

import android.graphics.Point;

/**
 * Created by cacagdas on 15.02.2018.
 */

public class Note {

    private String noteHeader;
    private String noteContent;
    private Point noteLocation;

    public Note(String noteHeader, String noteContent, Point noteLocation) {
        this.noteHeader = noteHeader;
        this.noteContent = noteContent;
        this.noteLocation = noteLocation;

    }

    public Note(String noteHeader, String noteContent) {
        this.noteHeader = noteHeader;
        this.noteContent = noteContent;
    }

    public String getNoteHeader() {
        return noteHeader;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public Point getNoteLocation() {
        return noteLocation;
    }
}
