package com.anjox.Gamebox_api.annotation;

import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;


@Aspect
@Component
public class JustImageAspect{

    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png");

    @Around("@annotation(com.anjox.Gamebox_api.annotation.JustImage)")
    public Object validateImageFile(ProceedingJoinPoint joinPoint) throws Throwable {

        MultipartFile multipartFile = (MultipartFile) joinPoint.getArgs()[0];

        if(multipartFile!=null && !IMAGE_TYPES.contains(multipartFile.getContentType()) ){
            throw new MessageErrorExeption("Apenas arquivos jpeg,jpg e png são suportados.", HttpStatus.BAD_REQUEST);
        }
        return joinPoint.proceed();
    }
}
