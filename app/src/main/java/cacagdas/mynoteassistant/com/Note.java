package cacagdas.mynoteassistant.com;

import android.graphics.Point;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by cacagdas on 15.02.2018.
 */

public class Note {

    private String noteHeader;
    private String noteContent;
    private LatLng noteLocation;
    private long noteDistance;
    private Date noteDate;
    private boolean isNotified;
    private String key;
    private boolean isInBounds;
    private long counter;

    public Note() { }

    public Note(String noteHeader, String noteContent, LatLng noteLocation, long noteDistance,
                boolean isNotified, boolean isInBounds, long counter) {
        this.noteHeader = noteHeader;
        this.noteContent = noteContent;
        this.noteLocation = noteLocation;
        this.noteDistance = noteDistance;
        this.isNotified = isNotified;
        this.isInBounds = isInBounds;
        this.counter = counter;
    }

    public Note(String noteHeader, String noteContent, LatLng noteLocation, long noteDistance,
                boolean isNotified, boolean isInBounds, long counter, String key) {
        this.noteHeader = noteHeader;
        this.noteContent = noteContent;
        this.noteLocation = noteLocation;
        this.noteDistance = noteDistance;
        this.isNotified = isNotified;
        this.isInBounds = isInBounds;
        this.counter = counter;
        this.key = key;
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

    public boolean isInBounds() {
        return isInBounds;
    }

    public long getNoteDistance() {
        return noteDistance;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getKey() {
        return key;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("noteHeader", noteHeader);
        result.put("noteContent", noteContent);
        result.put("noteLocation", noteLocation);
        result.put("noteDistance", noteDistance);
        result.put("isNotified", isNotified);
        result.put("isInBounds", isInBounds);
        result.put("counter", counter);

        return result;
    }

}
