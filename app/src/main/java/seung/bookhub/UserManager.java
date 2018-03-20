package seung.bookhub;

import android.util.Log;

public final class UserManager {
    private static User current;

    public User getCurrentUser() {
        return current;
    }

    public void setCurrentUser(User user) {
        Log.d("UM", "Current User Set: " + user.getUsername());
        this.current = user;
    }

}
