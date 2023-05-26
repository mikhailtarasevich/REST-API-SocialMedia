package com.mikhail.tarasevich.socialmedia.util;

public class PageableService {

    public static final int ITEMS_PER_PAGE_DEFAULT = 10;

    public static int checkItemsPerPage(int itemsPerPage) {
        if (itemsPerPage <= 0) return ITEMS_PER_PAGE_DEFAULT;
        else return itemsPerPage;
    }

    public static int getOffset(int itemsPerPage, int page) {
        if (page <= 0) return 0;
        else return itemsPerPage * (page - 1);
    }

}
