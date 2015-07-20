package reli.reliapp.co.il.reli.dataStructures;

import android.location.Location;

import com.facebook.login.widget.ProfilePictureView;
import java.util.Date;

public class ReliUser {

    /* ========================================================================== */

    private ReliUserType userType;
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private ProfilePictureView facebookPicture;
    private Date lastLoginDate;
    private Location location;

    /* ========================================================================== */

    public ReliUser(ReliUserType userType, String id, String firstName, String middleName, String lastName, String fullName, Location location) {
        this.userType = userType;
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.location = location;
        this.facebookPicture = facebookPicture;
        this.lastLoginDate = new Date();
    }

    /* ========================================================================== */

    public String getFirstName() {
        return firstName;
    }

    /* ========================================================================== */

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /* ========================================================================== */

    public String getLastName() {
        return lastName;
    }

    /* ========================================================================== */

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /* ========================================================================== */

    public ReliUserType getUserType() {
        return userType;
    }

    /* ========================================================================== */

    public void setUserType(ReliUserType userType) {
        this.userType = userType;
    }

    /* ========================================================================== */

    public ProfilePictureView getPicture() {
        ReliUserType userType = getUserType();
        if (userType == ReliUserType.ANONYMOUS_USER) {
            // TODO - change
            return null;
        }
        else if (userType == ReliUserType.FACEBOOK_USER) {
            return facebookPicture;
        }

        return null;
    }

    /* ========================================================================== */

    public void setFacebookPicture(ProfilePictureView facebookPicture) {
        this.facebookPicture = facebookPicture;
    }

    /* ========================================================================== */

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    /* ========================================================================== */

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }


    /* ========================================================================== */

    public String getMiddleName() {
        return middleName;
    }

    /* ========================================================================== */

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /* ========================================================================== */

    public String getFullName() {
        return fullName;
    }

    /* ========================================================================== */

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /* ========================================================================== */

    public String getId() {
        return id;
    }

    /* ========================================================================== */

    public void setId(String id) {
        this.id = id;
    }

    /* ========================================================================== */

    public Location getLocation() {
        return location;
    }

    /* ========================================================================== */

    public void setLocation(Location location) {
        this.location = location;
    }
}
