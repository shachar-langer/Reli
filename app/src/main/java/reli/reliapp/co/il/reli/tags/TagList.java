package reli.reliapp.co.il.reli.tags;

import java.util.ArrayList;

import reli.reliapp.co.il.reli.dataStructures.Tag;

public class TagList {

    ArrayList<Tag> tagsList = new ArrayList<>();

    public TagList() {

        String[] tagNames = {"Sports", "Infi"};

        for (int i = 0; i < tagNames.length; i++) {
            tagsList.add(new Tag(tagNames[i]));
        }

    }
}