package com.interview.controller;


import com.interview.model.GenericFilter;
import com.interview.model.GenericServiceResponse;
import com.interview.model.User;
import com.interview.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    public ResponseEntity<?> add( @RequestBody User user) throws Exception {
        GenericServiceResponse genericServiceResponse = userService.add(user);
        return new ResponseEntity<>(genericServiceResponse, genericServiceResponse.getHttpStatus());
    }

    @GetMapping
    public ResponseEntity<GenericServiceResponse> getList(
            @RequestParam(name = "isPageable", defaultValue = "false", required = false) boolean isPageable,
            @RequestParam(name = "currentPage", defaultValue = "1", required = false) Short currentPage,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Short pageSize,
            @RequestParam(name = "ascending", defaultValue = "false", required = false) boolean ascending,
            @RequestParam(name = "orderBy", defaultValue = "false", required = false) String orderBy,
            @RequestParam(name = "active", defaultValue = "false", required = false) boolean active,
            @RequestParam(name = "cpName", defaultValue = "", required = false) String name,
            @RequestParam(name = "cpEmail", defaultValue = "", required = false) String email,
            @RequestParam(name = "cpContact", defaultValue = "", required = false) String contact) {
        GenericFilter filter = GenericFilter.builder()
                .isPageable(isPageable)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .ascending(ascending)
                .orderBy(orderBy)
                .user(User.builder()
                        .orderBy(orderBy)
                        .ascending(ascending)
                        .active(active)
                        .name(name)
                        .email(email)
                        .build()).build();
        GenericServiceResponse genericServiceResponse = userService.getList(filter);
        return  new ResponseEntity<>(genericServiceResponse,genericServiceResponse.getHttpStatus());
    }

}
