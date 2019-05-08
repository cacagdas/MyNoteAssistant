package cacagdas.mynoteassistant.com;

import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by cacagdas on 15.02.2018.
 */

public class Note {

    private String noteHeader;
    private String noteContent;
    private LatLng noteLocation;
    private Date noteDate;
    private boolean isNotified;

    public Note(String noteHeader, String noteContent, LatLng noteLocation, boolean isNotified) {
        this.noteHeader = noteHeader;
        this.noteContent = noteContent;
        this.noteLocation = noteLocation;
        this.isNotified = isNotified;
    }

    public Note(String noteHeader, String noteContent, Date noteDate) {
        this.noteHeader = noteHeader;
        this.noteContent = noteContent;
        this.noteDate = noteDate;
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

    public LatLng getNoteLocation() {
        return noteLocation;
    }

    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }

}
