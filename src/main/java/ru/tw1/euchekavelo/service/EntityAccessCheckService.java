package ru.tw1.euchekavelo.service;

public interface EntityAccessCheckService<T> {

    void checkEntityAccess(T entity);
}
