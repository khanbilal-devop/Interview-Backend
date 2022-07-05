package com.interview.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.interview.dto.UserDto;
import com.interview.model.GenericFilter;
import com.interview.model.GenericServiceResponse;
import com.interview.model.User;
import com.interview.repository.UserRepository;
import com.interview.security.JwtTokenUtil;
import com.interview.specification.UserSpecification;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.interview.util.CommonUtil.*;

@Log4j2
@Service
@AllArgsConstructor
public class UserServiceImpl {

    UserRepository userRepository;
    JwtTokenUtil jwtTokenUtil;
    PasswordEncoder passwordEncoder;

    @Transactional
    public GenericServiceResponse add(User user) {
        GenericServiceResponse response = new GenericServiceResponse();
        try {
            boolean mailDuplicate = userRepository.findByEmailAndActiveIsTrue(user.getEmail()).isPresent();
            if (!mailDuplicate) {
                if (!user.getPassword().equals(user.getConfirmPassword()))
                    throw new Exception("Password doesn't match");
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setConfirmPassword(passwordEncoder.encode(user.getConfirmPassword()));
               user = userRepository.save(user);
               generatingSuccessResponse(response,converToDto(user),"Succssfully added");
            } else {
                generatingCustomResponse(response,user,"Email Id Already Exist",HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            log.info("Exception in ClientServiceImpl.add " + e.getMessage());
            e.printStackTrace();
            generatingErrorResponse(response,converToDto(user),e.getMessage(),"Error while adding Client");
        }
        return response;
    }

    public GenericServiceResponse getList(GenericFilter filter) {
        GenericServiceResponse response = new GenericServiceResponse();
        UserSpecification userSpecification = new UserSpecification(filter.getUser());
        List<User> users;
        try {
            if (filter.isPageable()) {
                Pageable page = PageRequest.of(filter.getCurrentPage() - 1, filter.getPageSize());
                Page<User> pageableContent =
                        userRepository.findAll(userSpecification, page);
                users = pageableContent.getContent();
                response.setTotalPages(pageableContent.getTotalPages());
                response.setTotalElements(pageableContent.getTotalElements());
            } else {
                users = userRepository.findAll(userSpecification);
            }
            List<UserDto> userDtos = users.isEmpty() ? Collections.emptyList() : convertToDtoList(users);
            response.setMessage(userDtos.isEmpty() ? "No such record found" : "Successfully fetched list");
            response.setHttpStatus(userDtos.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
            response.setData(userDtos);
        } catch (Exception e) {
            log.info("Exception in ClientServiceImpl.getList " + e.getMessage());
            e.printStackTrace();
            generatingErrorResponse(response,Collections.EMPTY_LIST,e.getMessage(),"Error while fetching for list");
        }
        return response;
    }

    public GenericServiceResponse loadUserByUsername(String email) {
        GenericServiceResponse response = new GenericServiceResponse();
        Optional<User> user = Optional.empty();
        try {
           user = userRepository.findByEmailAndActiveIsTrue(email);
            if (!user.isPresent()) {
                generatingCustomResponse(response,null,"Invalid credentials",HttpStatus.UNAUTHORIZED);
            } else{
                generateToken(user.get(), response);
            }
        } catch (Exception e) {
            log.info("Exception Occurs in UserServiceImpl.loadUserByUsername" + e.getMessage());
            e.printStackTrace();
            generatingErrorResponse(response,user,e.getMessage(),"Exeption while validating user");

        }
        return response;
    }

    private void generateToken(User user, GenericServiceResponse genericServiceResponse) throws JsonProcessingException {
        UserDto userDto = converToDto(user);
        String token = jwtTokenUtil.generateToken(userDto);
        genericServiceResponse.setData(token);
        genericServiceResponse.setTempData(user);
        genericServiceResponse.setHttpStatus(HttpStatus.OK);
    }

    private UserDto converToDto(User user){
        return  UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .active(user.isActive())
                .build();
    }

    private  List<UserDto> convertToDtoList(List<User> users){
        return users.stream().map(this::converToDto).collect(Collectors.toList());
    }
}
