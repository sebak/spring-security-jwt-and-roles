-- to insert through data.sql activate property spring.jpa.defer-datasource-initialization=true

-- to avoid to insert many time we do it only when the value not exist in db
INSERT INTO roles(name, description)
SELECT 'USER', 'USER role can access his information'
WHERE
NOT EXISTS (
SELECT name FROM roles WHERE name = 'USER'
);

INSERT INTO roles(name, description)
SELECT 'ADMIN', 'ADMIN role can do everything the User role does and access the users’ list'
WHERE
NOT EXISTS (
SELECT name FROM roles WHERE name = 'ADMIN'
);


INSERT INTO roles(name, description)
SELECT 'SUPER_ADMIN', 'SUPER_ADMIN role can do everything the Admin role does and create an admin user; shortly, he can do everything'
WHERE
NOT EXISTS (
SELECT name FROM roles WHERE name = 'SUPER_ADMIN'
);