package reli.reliapp.co.il.reli.dataStructures;

public enum MessageStatus {
    STATUS_SENDING (0, "Sending..."),
    STATUS_SENT (1, "Sent"),
    STATUS_FAILED (2, "Failed");

    /* ========================================================================== */

    private final int status;
    private final String statusDescription;

    /* ========================================================================== */

    MessageStatus (int status, String statusDescription) {
        this.status = status;
        this.statusDescription = statusDescription;
    }

    /* ========================================================================== */

    public String statusDescription() {
        return this.statusDescription;
    }
}
