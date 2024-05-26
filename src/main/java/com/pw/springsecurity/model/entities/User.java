package com.pw.springsecurity.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Data // for getter and setter to have a beans
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true) // to include BaseEntity attributes in equal and hashcode
public class User extends BaseEntity implements UserDetails {


    @Id
    /**
     * for serial value we most create users_id_seq sequence in postgres: [CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 1 INCREMENT BY 1;]
     * if we don't create sequence the query will fail
     */
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, length = 100, nullable = false)
    @Email(message = "Email should be valid")
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private Role role;

    public User setRole(Role role) {
        this.role = role;

        return this;
    }


    // ************* UserDetails interface method to override *********************

    /**
     * The method “getAuthorities()” returns the user’s roles list; it is helpful to manage permissions.
     * This allows us to access the user role from the authentication context.
     * For role-based authorization, Spring Security adds a default ROLE_ prefix to the value given. This is why we concatenate the role’s name with “ROLE_”.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getName().toString());

        return List.of(authority);
    }

    /*
    The method “getUsername()” returns the email address because it is unique information about the user.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Make sure the method isAccountNonExpired(), isAccountNonLocked(), isCredentialsNonExpired(), and isEnabled() returns “true”;
     * otherwise, the authentication will fail. You can customize the logic of these methods to fit your needs.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
