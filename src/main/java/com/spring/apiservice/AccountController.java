package com.spring.apiservice;

import java.util.stream.StreamSupport;

import com.spring.data.entities.User;
import com.spring.data.repositories.UserRepository;
import com.spring.services.EncryptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserRepository _userRepository;
    
    @PostMapping("/login")
    public @ResponseBody LoginResponse login(@RequestBody LoginRequest req) {
        try {
            User user = StreamSupport.stream(_userRepository.findAll().spliterator(), false).filter(u -> u.getUsername().equals(req.username)
                            && EncryptionService.hashPassword(req.password, u.getSalt())
                                    .equals(u.getHashedPassword()))
                    .findFirst().get();
            if (user != null) {
                return new LoginResponse();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
            }
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found", exc);
        }
    }

}

class LoginRequest {
    public String username;
    public String password;
}

class LoginResponse {
    public boolean success;

    LoginResponse() {
        this.success = true;
    }
}
