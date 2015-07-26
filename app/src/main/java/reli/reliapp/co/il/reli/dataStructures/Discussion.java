package reli.reliapp.co.il.reli.dataStructures;

import android.graphics.Bitmap;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.Date;

@ParseClassName("Discussions")
public class Discussion extends AbstractDiscussion {

    private ArrayList<Message> messagesList;

    public Discussion() {
        super();
    }

    public Discussion(String discussionName, ParseGeoPoint location, int radius,
                      Bitmap discussionLogo, Date creationDate, Date expirationDate,
                      String ownerParseID) {
        super(discussionName, location, radius, discussionLogo, creationDate, expirationDate, ownerParseID);
    }
}

