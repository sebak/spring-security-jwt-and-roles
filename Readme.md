# Spring Boot 3 Security With JWT and Roles

## This tutorial is based on code write from Spring Boot 3 Security With JWT

This tutorial is the suite of the following tutorial where we implemented a JWT authentication in Spring Boot 3 and
Spring Security 6.

## What we will do

We have a Web API that has public routes and restricted routes. The restricted routes require a valid JWT from a user in
the database.

Now that the user is authenticated, we want to go further by allowing access to some data only if the user has a
specific role.

Here are the following roles we have in our system:

* User: can access his information
* Administrator: can do everything the User role does and access the users’ list.
* Super Administrator: can do everything the Admin role does and create an admin user; shortly, he can do everything.
  Below are the list of protected routes with the roles required to access them.

Route: [GET] /users/me
Roles: User, Admin, Super Admin
Description: retrieve the authenticated user.

Route: [GET] /users
Roles: Admin, Super Admin
Description: retrieve the list of all users.

Route: [POST] /admins
Roles: Super Admin
Description: Create an administrator.

#### Prerequisites

- Start by running postgres docker image with environment variables and port mapping
  application.yml most have the same environment variable and port than docker db

```docker
docker run -d -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=springbootsecuritydb -e POSTGRES_USER=pw --name postgresdb -p 5433:5432 postgres
```

- To connect from host according to environment variable

```bash
psql -p $LOCAL_PORT -h 127.0.0.1 -d $YOUR_DB_NAME -U $YOUR_USERNAME -W
psql -p 5433 -h 127.0.0.1 -d springbootsecuritydb -U pw -W
```

- start spring boot application it will create needed table on db by schema.sql

### Let’s register or add a user with the following cURL request.

```curl
curl -XPOST -H "Content-type: application/json" -d '{
  "email": "jon@snow.com",
  "password": "123456",
  "fullName": "Jon Snow"
}' 'http://localhost:8085/auth/signup'
```

### 1- Create the role entity (RoleEnum.java and Role.java)

The role entity will represent the different roles needed in our system

### 2- Create the “RoleRepository.java” which represents the Data Access Layer for the Role entity.

### 3- Store the pre-defined roles in the database (in my case i have use script data.sql, but below there is another ways to do in spring boot)

We already know roles to persist in the system. So, before creating a user, we must ensure the role exists in the
database.

We will create a function executed at the application startup to create roles in the database if they don’t exist.

Spring Boot allows executing some actions on the application startup; we will use it here, so let’s create a package
called “bootstrap”, then create the file “RoleSeeder.java” and add the code below:

```java
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;


    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    private void loadRoles() {
        RoleEnum[] roleNames = new RoleEnum[]{RoleEnum.USER, RoleEnum.ADMIN, RoleEnum.SUPER_ADMIN};
        Map<RoleEnum, String> roleDescriptionMap = Map.of(
                RoleEnum.USER, "Default user role",
                RoleEnum.ADMIN, "Administrator role",
                RoleEnum.SUPER_ADMIN, "Super Administrator role"
        );

        Arrays.stream(roleNames).forEach((roleName) -> {
            Optional<Role> optionalRole = roleRepository.findByName(roleName);

            optionalRole.ifPresentOrElse(System.out::println, () -> {
                Role roleToCreate = new Role();

                roleToCreate.setName(roleName)
                        .setDescription(roleDescriptionMap.get(roleName));

                roleRepository.save(roleToCreate);
            });
        });
    }
}
```

Re-run the application and verify the roles have been created in the database.

### 4- Update the User entity to include the role in User.java

```java
@OneToOne(cascade = CascadeType.REMOVE)
@JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
private Role role;

public Role getRole(){
        return role;
        }

public User setRole(Role role){
        this.role=role;

        return this;
        }
```

### 4-1 Alter script schema.sql to add role_id in users and re run application

### 5- Set the role when creating a user

The user role is now required, so creating a user without one will throw an error. We must update signup() function in
the AuthenticationService.java;

### Let’s register or add a user with the following cURL request to add role_id in users table.

```curl
curl -XPOST -H "Content-type: application/json" -d '{
  "email": "jones@sill.com",
  "password": "98765",
  "fullName": "Jones Sill"
}' 'http://localhost:8085/auth/signup'
```

### 6- Access the user role in the authentication context

In the user entity class (User.java), the function getAuthorities() returns all the authorities associated with this
user; it was empty by default, but now we must update it to produce a list containing the user's role name.

### 7- Enable the method security of Spring Security

To restrict user access based on their roles, we must enable the feature in Spring security, allowing us to perform the
check without writing a custom logic.
You must add the annotation @EnableMethodSecurity on the security configuration file “SecurityConfiguration.java”.

```java

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    // The existing code configuration here...
}
```

Adding this annotation gives us annotations we can use at the controller level to perform role access control.

### 8- Protect the API routes for user and admin role

Open the file UserController.java and add the annotation for the following routes:

* “/users/me”: @PreAuthorize(“isAuthenticated()”)
* “/users”: @PreAuthorize(“hasAnyRole(‘ADMIN’, ‘SUPER_ADMIN’)”)
  Since the route “users/me” is accessible by all the roles, we can just check that the user is authenticated.

### 9- Protect the API route for the super admin role
The endpoint “/admins” creates a new admin in the system and is accessible only by a user having a super admin role.

Since the implementation doesn’t exist, let’s add it in the UserService.java

### 9-1 create a new file “AdminController.java”

### 10- create super admin
There is no endpoint to create a super admin, so we must create one at the application startup. In the package “bootstrap”, create a file “AdminSeeder.java”

###  11- call end point to get token of super admin created up 

![super-admin-token.png](src%2Fmain%2Fresources%2Fimages%2Fsuper-admin-token.png)

###  12- call user/me as super admin with is own token generated up 
![log-me-with-super-admin-token.png](src%2Fmain%2Fresources%2Fimages%2Flog-me-with-super-admin-token.png)

### 13- call admin end point with a super admin token
![adminEndPoint.png](src%2Fmain%2Fresources%2Fimages%2FadminEndPoint.png)

### 14- Wrap up
In this post, we saw how to implement a Role Based Access Control in a Spring Boot application, and here are the main steps to remember:

Create the role entity and data access layer.
Associate the user entity with a role.
Expose the user’s role in the authentication context.
Enable the method security Spring security.
Protect the API route using the method security route isAuthenticated(), hasRole() and hasAnyRole().