package ru.tw1.euchekavelo.userservice.service;

public interface EntityAccessCheckService<T> {

    void checkEntityAccess(T entity);
}
