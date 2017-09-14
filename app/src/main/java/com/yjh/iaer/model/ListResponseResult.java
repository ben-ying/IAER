package com.yjh.iaer.model;

import com.google.gson.annotations.SerializedName;

public class ListResponseResult<T> {
    @SerializedName("count")
    private int count;
    @SerializedName("next")
    private String next;
    @SerializedName("previous")
    private String previous;
    @SerializedName("results")
    private T results;

    public ListResponseResult(ListResponseResult<T> result) {
        this.results = result.getResults();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }
}
