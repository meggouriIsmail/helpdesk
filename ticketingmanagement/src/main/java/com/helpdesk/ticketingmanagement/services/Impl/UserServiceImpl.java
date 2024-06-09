package com.helpdesk.ticketingmanagement.services.Impl;

import com.helpdesk.ticketingmanagement.dto.UserDto;
import com.helpdesk.ticketingmanagement.dto.UserReqDto;
import com.helpdesk.ticketingmanagement.dto.UserReqPasswordDto;
import com.helpdesk.ticketingmanagement.entities.Department;
import com.helpdesk.ticketingmanagement.entities.User;
import com.helpdesk.ticketingmanagement.repositories.DepartmentRepository;
import com.helpdesk.ticketingmanagement.repositories.RoleRepository;
import com.helpdesk.ticketingmanagement.repositories.UserRepository;
import com.helpdesk.ticketingmanagement.security.KeycloakRegistration;
import com.helpdesk.ticketingmanagement.services.UserService;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final DepartmentRepository departmentRepository;
    private final KeycloakRegistration keycloakAdminClientService;

    public UserServiceImpl(UserRepository userRepository, DepartmentRepository departmentRepository, KeycloakRegistration keycloakAdminClientService) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.keycloakAdminClientService = keycloakAdminClientService;
    }

    public void saveOrUpdateUser(UserDto userDto) {
        keycloakAdminClientService.createUser(userDto);
        Optional<User> optional = userRepository.findByUsername(userDto.getUsername());
        Optional<Department> department = departmentRepository.findDepartmentByName(userDto.getDepartmentDto().getName());

        User user;
        if (optional.isEmpty()) {
            user = new User();
            user.setUsername(userDto.getUsername());
        } else { user = optional.get(); }
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        int randomNumber = 10000000 + RandomUtils.nextInt(90000000);
        String reference = "RU-" + randomNumber;
        user.setReferenceUser(reference);
        user.setEnabled(true);
        department.ifPresent(user::setDepartment);

        userRepository.save(user);
    }

    public User getLoggedInUser() {
        String username = getUsernameFromAuthentication();
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public void desactivateUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.setEnabled(false);
            userRepository.save(user);
        }
    }

    @Override
    public User updateUser(Long userId, UserReqDto userReqDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Department> department = departmentRepository.findDepartmentByName(userReqDto.getDepartmentDto().getName());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.setFirstName(userReqDto.getFirstName());
            user.setLastName(userReqDto.getLastName());
            user.setPhoneNumber(userReqDto.getPhoneNumber());
            user.setPost(userReqDto.getPost());
            user.setAboutMe(userReqDto.getAboutMe());
            user.setLocation(userReqDto.getLocation());
            department.ifPresent(user::setDepartment);
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public void updatePassword(UserReqPasswordDto password) {
        String id = getIdFromAuthentication();
        keycloakAdminClientService.updatePassword(password.getPassword(), id);
    }

    private String getUsernameFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            username = jwt.getClaimAsString("preferred_username");
        }

        return username;
    }
    private String getIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = null;

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            id = jwt.getSubject();
        }

        return id;
    }
}


