package reli.reliapp.co.il.reli.dataStructures;

import reli.reliapp.co.il.reli.utils.Const;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("ReliTag")
public class ReliTag extends ParseObject {
    private String tagName;
    private String parseID;

    public ReliTag() {
        // Default Constructor is a must
    }

    public ReliTag(String tagName) {
        setTagName(tagName);
//        saveEventually();
    }

    public ReliTag(String tagName, String parseID) {
        setTagName(tagName);
        setTagParseID(parseID);
//        saveEventually();
    }

    public String getTagName() {
        return getString(Const.COL_TAG_NAME);
    }

    public void setTagName(String tagName) {
        put(Const.COL_TAG_NAME, tagName);
        this.tagName = tagName;
    }

    public void setTagParseID(String parseID) {
        this.parseID = parseID;
    }

    public String getTagParseID() {
        return this.parseID;
    }


    @Override
    public boolean equals(Object o) {
        return ((o instanceof ReliTag) &&
                (((ReliTag)o).getTagName().equals(this.getTagName())));

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static ParseQuery<ReliTag> getReliTagQuery() {
        return ParseQuery.getQuery(ReliTag.class);
    }
}