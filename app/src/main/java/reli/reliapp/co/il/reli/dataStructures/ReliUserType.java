package reli.reliapp.co.il.reli.dataStructures;

public enum ReliUserType {
    ANONYMOUS_USER (0),
    FACEBOOK_USER (1);

    private final int userTypeCode;

    ReliUserType (int userTypeCode) {
        this.userTypeCode = userTypeCode;
    }

    int getUserTypeCode() {
        return this.userTypeCode;
    }

}
