package com.example.ekthacare.services;

import com.example.ekthacare.entity.User;
import java.util.List;

public class UserUploadResult {
    private List<User> validUsers;
    private List<User> duplicateUsers;

    public UserUploadResult(List<User> validUsers, List<User> duplicateUsers) {
        this.validUsers = validUsers;
        this.duplicateUsers = duplicateUsers;
    }

    public List<User> getValidUsers() {
        return validUsers;
    }

    public List<User> getDuplicateUsers() {
        return duplicateUsers;
    }
}
