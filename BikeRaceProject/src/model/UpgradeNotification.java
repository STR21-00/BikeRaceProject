package model;

// upgradeNotification, from the DP diagram: -message, +update(), +sendNotification().
public class UpgradeNotification implements Observer {

    private String message;
    private boolean pending = false; // added this, true when a notification is waiting to be shown

    @Override
    public void update(String message) {
        this.message = message;
        sendNotification();
    }

    // The model can't print or call the view, so "sending" just marks the notification as ready.
    // The controller picks it up with takeNotification() and the view displays it.
    public void sendNotification() {
        pending = true;
    }

    // gives back the waiting message (or null if there isn't one) and clears it so it only shows once
    public String takeNotification() {
        if (!pending) {
            return null;
        }
        pending = false;
        return message;
    }
}
