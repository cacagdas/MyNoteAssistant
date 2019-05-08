package cacagdas.mynoteassistant.com;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cacagdas on 13.05.2018.
 */

public class Friend {

    private String friendName, friendId;

    public Friend (String friendName, String friendId) {
        this.friendName = friendName;
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getFriendId() {
        return friendId;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("friendName", friendName);
        result.put("friendId", friendId);
        return result;
    }
}
