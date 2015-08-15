package reli.reliapp.co.il.reli.tags;

import java.util.ArrayList;

import reli.reliapp.co.il.reli.dataStructures.ReliTag;

public class TagList {

    ArrayList<ReliTag> tagsList = new ArrayList<>();

    public TagList() {

        String[] tagNames = {"Sports", "Infi"};

        for (int i = 0; i < tagNames.length; i++) {
            tagsList.add(new ReliTag(tagNames[i]));
        }

    }
}