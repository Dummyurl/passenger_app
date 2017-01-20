package com.passengerapp.main.model;

/**
 * Created by adventis on 1/16/16.
 */
public class MenuItemData {
    public enum MenuItem {
        GET_A_QUOTE_OR_MAKE_RESERVATION,
        ACTIVE_TRIPS,
        HISTORICAL_TRIPS,
        WRITE_A_REVIEW,
        PREFERENCES,
        GET_EMAIL_RECEIPT,
        GO_TO_MAP
    }

    public MenuItemData(String title, MenuItem id) {
        setId(id);
        setTitle(title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MenuItem getId() {
        return id;
    }

    public void setId(MenuItem id) {
        this.id = id;
    }

    private String title;
    private MenuItem id;
}
