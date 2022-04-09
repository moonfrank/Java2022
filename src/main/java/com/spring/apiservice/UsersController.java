package com.spring.apiservice;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import com.spring.data.entities.User;
import com.spring.data.repositories.UserRepository;
import com.spring.services.EncryptionService;
import com.spring.utils.enums.RoleEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserRepository _userRepository;

    @GetMapping()
    public @ResponseBody List<User> getAll(@RequestParam Map<String, String> req) {
        if (req.containsKey("role")) {
            try {
                if (Integer.parseInt(req.get("role")) < 1 || Integer.parseInt(req.get("role")) > 4) {
                    throw new IllegalArgumentException();
                }
                return StreamSupport.stream(_userRepository.findAll().spliterator(), false)
                        .filter(user -> user.getRole() == RoleEnum
                                .valueOf(Integer.parseInt(req.get("role")))
                                .get())
                        .toList();
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
            }
        } else {
            return StreamSupport.stream(_userRepository.findAll().spliterator(), false).toList();
        }

    }

    @GetMapping("/{id}")
    public @ResponseBody User getOne(@PathVariable(value = "id") long id) {
        try {
            return _userRepository.findById((long) id).get();
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found", exc);
        }
    }

    @PostMapping()
    public @ResponseBody long post(@RequestBody PostRequest req) {
        try {
            var hashAndSalt = EncryptionService.hashPassword(req.password);
            User user = new User(RoleEnum.valueOf(req.role).get(), req.username, req.email,
            hashAndSalt.hash,
            hashAndSalt.salt, req.firstName, req.lastName);
            _userRepository.save(user);
            return _userRepository.count();
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request", exc);
        }
    }

    @PatchMapping("/{id}")
    public @ResponseBody boolean patch(@PathVariable(value = "id") long id, @RequestBody PatchRequest req) {
        try {
            var hashedPasswordAndSalt = EncryptionService.hashPassword(req.getPassword().get());
            _userRepository.findById((long) id).get().setHashedPassword(hashedPasswordAndSalt.hash);
            _userRepository.findById((long) id).get().setSalt(hashedPasswordAndSalt.salt);
            _userRepository.save(_userRepository.findById((long) id).get());
            return true;
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found", exc);
        }
    }
}

class PostRequest {
    public int role;
    public String username;
    public String email;
    public String password;
    public String firstName;
    public String lastName;
}

class PatchRequest {
    private String password;

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }
}
