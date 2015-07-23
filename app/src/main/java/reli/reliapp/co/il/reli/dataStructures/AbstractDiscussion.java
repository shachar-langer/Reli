package reli.reliapp.co.il.reli.dataStructures;

import android.graphics.Bitmap;

import com.parse.ParseGeoPoint;

import java.util.Date;

public abstract class AbstractDiscussion {

    private String discussionParseID;
    private String discussionName;
    private ParseGeoPoint location;
    private int radius;
    private Bitmap discussionLogo;
    private Date creationDate;
    private Date expirationDate;
    private String ownerParseID;

}
