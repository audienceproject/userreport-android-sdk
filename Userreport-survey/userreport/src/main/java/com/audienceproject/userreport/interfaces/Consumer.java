package com.audienceproject.userreport.interfaces;

public interface Consumer<T> {

    void consume(T response);
}
