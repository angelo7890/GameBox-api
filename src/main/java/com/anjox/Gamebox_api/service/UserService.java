package com.anjox.Gamebox_api.service;


import com.anjox.Gamebox_api.dto.RequestRegisterUserDto;
import com.anjox.Gamebox_api.dto.ResponseGameDto;
import com.anjox.Gamebox_api.dto.ResponsePaginationUserDto;
import com.anjox.Gamebox_api.dto.ResponseUserDto;
import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.enums.UserEnum;
import com.anjox.Gamebox_api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity createUser (RequestRegisterUserDto dto){
        VerifyUsernameOrEmailIfExists(dto.name(), dto.email());
        validationPassword(dto.password());
        UserEntity user = new UserEntity(
                dto.name(),
                dto.email(),
                dto.password(),
                dto.type()
        );
         return userRepository.save(user);
    }

    public ResponseUserDto findById (Long  id){
        UserEntity user = userRepository.findById(id).orElse(null);
        if(user == null){
           return new ResponseUserDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getType()
            );
        }
        throw new RuntimeException("usuario nao encontrado");
    }

    public ResponsePaginationUserDto findAll( int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> users = userRepository.findAll(pageable);
        List<ResponseUserDto> usersList = users.stream().map(
                u -> new ResponseUserDto(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getType()
                )
        ).collect(Collectors.toList());
        return new ResponsePaginationUserDto(
                usersList,
                users.getTotalPages(),
                users.getTotalElements(),
                users.getSize(),
                users.getNumber()
        );
    }
    public void deleteById (Long id){
        userRepository.deleteById(id);
    }

    private void VerifyUsernameOrEmailIfExists (String username , String email){
        if(userRepository.findByUsername(username) != null || userRepository.findByEmail(email) != null){
            throw new RuntimeException("email ou usuario ja existe");
        }
    }

    private void validationPassword(String password){
        List<String> list = new ArrayList<String>();
        if(!(password!=null && password.length()>6)) {
            list.add("a senha deve ter no minimo 6 caracteres");
        }
        if(!Pattern.matches(".*[A-Z].*", password)) {
            list.add("a senha deve conter letras maiusculas");
        }
        if(!Pattern.matches(".*[a-z].*", password)) {
            list.add("a senha deve conter letras minusculas");
        }
        if(!Pattern.matches(".*[0-9].*", password)) {
            list.add("a senha deve conter numeros");
        }
        if(!Pattern.matches(".*[!@#$%?+-].*", password)) {
            list.add("a senha deve conter caracteres especiais: !@#$%?+- ");
        }
        if(!list.isEmpty()) {
            throw new RuntimeException("password incorreto"+list);
        }
    }
}
