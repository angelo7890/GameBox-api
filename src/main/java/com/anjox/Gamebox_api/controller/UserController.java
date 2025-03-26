package com.anjox.Gamebox_api.controller;
import com.anjox.Gamebox_api.dto.*;
import com.anjox.Gamebox_api.security.UserPrincipal;
import com.anjox.Gamebox_api.security.service.AuthService;
import com.anjox.Gamebox_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser (@RequestBody @Valid RequestRegisterUserDto requestRegisterUserDto) {
        Authentication userPrincipal = SecurityContextHolder.getContext().getAuthentication();
        userService.createUser(requestRegisterUserDto , userPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseLoginDto> login(@RequestBody @Valid RequestLoginDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.authenticate(dto));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseJwtTokensDto> refreshTokenJwt(){
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseJwtTokensDto dto = authService.refreshTokenAccessFromRefreshToken(usernameFromToken);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping("/refresh-password")
    public ResponseEntity<?> refreshPassword(@RequestBody @Valid RequestRefreshPasswordDto requestRefreshPasswordDto) {
        userService.refreshPassword(requestRefreshPasswordDto.email());
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUserDto> getUserById(@PathVariable("userId") Long idUser){
        ResponseUserDto dto = userService.findById(idUser);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/findAll")
    public ResponseEntity<ResponsePaginationUserDto> getAllUser(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        ResponsePaginationUserDto dto = userService.findAll(page, size);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/verify")
    public String verifyUserAccount (@Param("code") String code){
        if(userService.activateAccount(code)) {
            return "deu certo";
        }
        //aqui pode fazer um redirecionamento para a pagina de login
        return "token nao encontrado ou ja foi ativado";
    }


    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId, @RequestBody RequestUpdateUserDto dto){
        userService.updateUserById(userId , dto );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId){
        userService.deleteById(userId);
        return ResponseEntity.ok().build();
    }
}
