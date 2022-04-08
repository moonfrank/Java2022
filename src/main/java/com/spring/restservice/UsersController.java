package com.spring.restservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.spring.entities.User;
import com.spring.services.EncryptionService;
import com.spring.utils.enums.RoleEnum;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UsersController {

    private final AtomicLong counter = new AtomicLong();
    private ArrayList<User> users = new ArrayList<User>();

    @GetMapping("/users")
    public List<User> getAll(@RequestParam(value = "role", required = false) RoleEnum role) {
        if (role == null) {
            return users;
        } else {
            return users.stream().filter(user -> user.getRole().equals(role)).toList();
        }

    }

    @GetMapping("/users/{id}")
    public User getOne(@PathVariable(value = "id") long id) {
        try {
            return users.get((int) id - 1);
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found", exc);
        }
    }

    @PostMapping("/users")
    public int post(@RequestBody PostRequest req) {
        try {
            String[] hashAndSalt = EncryptionService.hashPassword(req.password);
            users.add(new User(counter.incrementAndGet(), req.role, req.username, req.email, hashAndSalt[0],
                    hashAndSalt[1], req.firstName, req.lastName));
            return users.size();
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request", exc);
        }
    }

    @PatchMapping("/users/{id}")
    public boolean patch(@PathVariable(value = "id") long id, @RequestBody PostRequest req) {
        try {
            users.get((int) id - 1).setUsername(req.username);
            return true;
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Name Not Found", exc);
        }
    }
}

class PostRequest {
    public RoleEnum role;
    public String username;
    public String email;
    public String password;
    public String firstName;
    public String lastName;
}

class PatchRequest {
    public String username;
}
