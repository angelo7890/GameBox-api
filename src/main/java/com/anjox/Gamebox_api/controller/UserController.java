package com.anjox.Gamebox_api.controller;
import com.anjox.Gamebox_api.dto.ResponsePaginationUserDto;
import com.anjox.Gamebox_api.dto.ResponseUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @PostMapping("/register")
    public ResponseEntity<?> createUser (){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(){
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUserDto> getUserById(@PathVariable("id") Long id){
        return null;
    }

    @GetMapping("/allUser")
    public ResponseEntity<ResponsePaginationUserDto> getAllUser(@RequestParam int page, @RequestParam int size){
        return null;
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
        return null;
    }
}
