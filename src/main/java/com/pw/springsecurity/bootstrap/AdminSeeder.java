package com.pw.springsecurity.bootstrap;

import com.pw.springsecurity.model.dtos.RegisterUserDto;
import com.pw.springsecurity.model.entities.Role;
import com.pw.springsecurity.model.entities.RoleEnum;
import com.pw.springsecurity.model.entities.User;
import com.pw.springsecurity.repository.RoleRepository;
import com.pw.springsecurity.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public AdminSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
    }

    private void createSuperAdministrator() {

        var email = "super.admin@email.com";
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN);
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        var userDto = RegisterUserDto.builder()
                .fullName("Super Admin")
                .email(email)
                .password("super_admin")
                .build();

        var user = User.builder()
                .fullName(userDto.getFullName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(optionalRole.get())
                .build();

        userRepository.save(user);
    }
}