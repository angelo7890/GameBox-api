package com.anjox.Gamebox_api.controller;
import com.anjox.Gamebox_api.dto.*;
import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.exeption.error.ResponseError;
import com.anjox.Gamebox_api.service.JwtService;
import com.anjox.Gamebox_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public UserController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser (@RequestBody @Valid RequestRegisterUserDto requestRegisterUserDto) {
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.createUser(requestRegisterUserDto , usernameFromToken);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid RequestLoginDto dto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
            var auth = authenticationManager.authenticate(usernamePassword);
            if (auth.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                ResponseJwtTokensDto response = jwtService.getJwtUserToken(userDetails);
                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseError("Credenciais inválidas", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseError("Ocorreu um erro", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseJwtTokensDto> refreshTokenJwt(){
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseJwtTokensDto dto = userService.refreshTokenAccessFromRefreshToken(usernameFromToken);
        return ResponseEntity.ok().body(dto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResponseUserDto> getUserById(@PathVariable("id") Long id){
       ResponseUserDto dto = userService.findById(id);
       return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/findAll")
    public ResponseEntity<ResponsePaginationUserDto> getAllUser(@RequestParam int page, @RequestParam int size){
        ResponsePaginationUserDto dto = userService.findAll(page, size);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
