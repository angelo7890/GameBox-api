package com.anjox.Gamebox_api.service;


import com.anjox.Gamebox_api.dto.RequestRegisterUserDto;
import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

    public UserEntity findById (Long  id){
        return userRepository.findById(id).orElse(null);
    }

    public List<UserEntity> findAll(){
        return userRepository.findAll();
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
