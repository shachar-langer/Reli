package reli.reliapp.co.il.reli.dataStructures;

import reli.reliapp.co.il.reli.utils.Const;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("ReliTag")
public class ReliTag extends ParseObject {


    private String tagName;

    // Default Constructor
    public ReliTag() {
    }

    public ReliTag(String tagName) {
        setTagName(tagName);
    }

    public String getTagName() {
        return getString(Const.COL_TAG_NAME);
    }

    public void setTagName(String tagName) {
        put(Const.COL_TAG_NAME, tagName);
    }
}