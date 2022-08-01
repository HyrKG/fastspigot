package cn.hyrkg.fastspigot.spigot.utils;

import java.util.ArrayList;

public class PageHelper<T> {

    private ArrayList<T> lists = new ArrayList<>();
    private int pageMax = 1, pageNow = 1;
    private int pageAmount = 1;

    public PageHelper(int amountPerPage) {
        pageAmount = amountPerPage;
    }

    public PageHelper<T> setContext(ArrayList<T> contexts) {
        lists = contexts;
        updatePageInfo();
        return this;
    }

    public PageHelper<T> updatePageInfo() {
        pageMax = lists.size() / pageAmount + (lists.size() % pageAmount == 0 ? 0 : 1);
        if (pageMax == 0)
            pageMax = 1;

        if (pageNow > pageMax)
            pageNow = pageMax;
        if (pageNow < 1)
            pageNow = 1;
        return this;
    }

    public PageHelper<T> addPage(int amount) {
        pageNow += amount;
        if (pageNow > pageMax)
            pageNow = 1;
        if (pageNow < 1)
            pageNow = pageMax;
        return updatePageInfo();
    }

    public int getPageMax() {
        return pageMax;
    }

    public int getPageNow() {
        return pageNow;
    }

    public String getPageInfo() {
        return pageNow + "/" + pageMax;
    }

    public int getAmountNowPage() {
        if (pageNow == pageMax && lists.size() == pageAmount)
            return pageAmount;
        return (pageNow == pageMax ? (lists.size() % pageAmount) : pageAmount);
    }

    public T getItem(int index) {
        return lists.get((pageNow - 1) * pageAmount + index);
    }

}
