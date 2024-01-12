package com.example.userservice.services;

import com.example.userservice.dtos.UserDto;
import com.example.userservice.models.Session;
import com.example.userservice.models.SessionStatus;
import com.example.userservice.models.User;
import com.example.userservice.repositories.SessionRepository;
import com.example.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;

import static  java.util.random.RandomGenerator.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private SecretKey key;
    @Autowired
    public AuthService(UserRepository userRepository,SessionRepository sessionRepository,BCryptPasswordEncoder bCryptPasswordEncoder,SecretKey key){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.key = key;
    }
    public ResponseEntity<UserDto> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        //check if user exists
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        //validate password
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        /*
            String token = RandomStringUtils.randomAlphanumeric(30);
            Session session = new Session();
            session.setToken(token);
            session.setUser(user);
            session.setSessionStatus(SessionStatus.ACTIVE);
            sessionRepository.save(session);
         */
        Map<String,Object> content = new HashMap<>();
        content.put("userId",user.getId());
        content.put("email",user.getEmail());
        content.put("roles",user.getRoles());


        MacAlgorithm algo = Jwts.SIG.HS256;
        String token = Jwts.builder().claims(content).signWith(key,algo).compact();


        UserDto userDto = UserDto.from(user);

        MultiValueMapAdapter<String,String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:"+token);
        ResponseEntity<UserDto> responseEntity = new ResponseEntity<>(userDto, headers, HttpStatus.OK);




        return responseEntity;
    }
    public void logout(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return ;
        }

        Session session = sessionOptional.get();

        session.setSessionStatus(SessionStatus.ENDED);

        sessionRepository.save(session);
    }
    public User signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        return userRepository.save(user);
    }
    public SessionStatus validate(String token, Long userId) {
//        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
//
//        if (sessionOptional.isEmpty()) {
//            return null;
//        }
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        //Session session = sessionOptional.get();

        return SessionStatus.ACTIVE;
    }
}
