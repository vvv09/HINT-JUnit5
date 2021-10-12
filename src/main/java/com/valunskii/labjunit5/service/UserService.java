package com.valunskii.labjunit5.service;

import com.valunskii.labjunit5.dto.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final List<User> users = new ArrayList<>();

    public List<User> getAll() {
        return users;
    }

    public void add(User user) {
        users.add(user);
    }
}
