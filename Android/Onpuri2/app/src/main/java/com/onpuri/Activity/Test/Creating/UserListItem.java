package com.onpuri.Activity.Test.Creating;

/**
 * Created by kutemsys on 2016-09-17.
 */
public class UserListItem{
    public boolean isSelected;
    public String description;

    UserListItem(String description, boolean isSelected){
        this.description = description;
        this.isSelected=isSelected;
    }
}