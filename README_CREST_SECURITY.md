# CREST Security Documentation

## Overview

CREST (Conditions REST) implements a comprehensive, multi-layered security architecture designed for flexibility and robustness in managing particle physics conditions data. The security framework is built on Spring Security and supports multiple authentication mechanisms to accommodate different deployment scenarios, from development environments to production systems integrated with enterprise identity management.

## Table of Contents

1. [Security Architecture](#security-architecture)
2. [Authentication Mechanisms](#authentication-mechanisms)
3. [Authorization and Role-Based Access Control](#authorization-and-role-based-access-control)
4. [Security Profiles](#security-profiles)
5. [SSL/TLS Support](#ssltls-support)
6. [CORS and CSRF Protection](#cors-and-csrf-protection)
7. [Configuration](#configuration)
8. [Best Practices](#best-practices)
9. [Troubleshooting](#troubleshooting)

---

## Security Architecture

CREST's security architecture is based on the following principles:

- **Defense in Depth**: Multiple security layers protect the application
- **Flexible Authentication**: Support for Keycloak/OAuth2, database-backed
- **Role-Based Access Control (RBAC)**: Fine-grained authorization using predefined roles
- **Profile-Based Configuration**: Different security profiles for development, testing, and production
- **Industry Standards**: OAuth2/OpenID Connect, JWT tokens, BCrypt password hashing

### Core Components

- **SecurityConfig**: Keycloak/OAuth2 JWT-based authentication configuration
- **SecurityDbConfig**: Database-backed authentication with BCrypt password encoding
- **SecurityDefaultConfig**: Default security profiles (`none` and `weak` modes for testing)
- **CustomAuthenticationProvider**: Custom authentication logic for database mode
- **CustomUserDetailsService**: User and role loading from database
- **AuthorizationControl**: Method-level security annotation for endpoint protection

---

## Authentication Mechanisms

CREST supports 2 authentication mechanisms that can be activated via Spring profiles:

### 1. Keycloak/OAuth2 (Recommended for Production)

**Profile**: `kc` (Keycloak)

The Keycloak integration provides enterprise-grade authentication using OAuth2 and OpenID Connect protocols.

**Features**:
- JWT (JSON Web Token) based authentication
- Single Sign-On (SSO) capability
- Centralized identity management
- Role extraction from JWT claims (`realm_access` and `resource_access`)
- Token validation and signature verification

**How it works**:
1. Client obtains JWT token from Keycloak server
2. Token is included in HTTP request as `Authorization: Bearer <token>`
3. CREST validates the token signature and expiration
4. Roles are extracted from token claims
5. Access is granted based on role permissions

**Configuration** (application.properties):
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://keycloak.example.com/auth/realms/crest-realm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://keycloak.example.com/auth/realms/crest-realm/protocol/openid-connect/certs
```

**Role Mapping**:
- Realm roles: `realm_access.roles`
- Client roles: `resource_access.<client-id>.roles`

### 2. Database Authentication

**Profile**: `db` (Database)

Database-backed authentication stores user credentials in CREST's database with BCrypt-hashed passwords.

**Features**:
- BCrypt password hashing (industry-standard, salted hash)
- User and role management via database
- Simple configuration for standalone deployments
- No external dependencies

**How it works**:
1. User credentials stored in `CREST_USER` and `CREST_ROLES` tables
2. Client sends Basic Authentication or form-based login
3. CREST queries database for user credentials
4. Password verified using BCrypt comparison
5. Roles loaded from database and applied to security context

**User Roles**:
- `ADMIN`: Full administrative access
- `MANAGER`: Elevated permissions for data management
- Additional custom roles as needed

**Password Storage**:
Passwords are hashed using BCrypt with a strength factor of 12, providing strong protection against brute-force attacks.

---

## Authorization and Role-Based Access Control

CREST implements fine-grained authorization using role-based access control (RBAC).

### Roles

Three primary roles control access to CREST functionality:

| Role | Description | Permissions |
|------|-------------|-------------|
| `crest-admin` | Administrator | Full access to all endpoints, including administrative functions |
| `crest-expert` | Expert User | Write access to data, create/modify tags, IOVs, global tags |
| `crest-trigger` | Trigger System | Limited write access for automated systems (trigger data) |

Other roles are defined inherited from the `cool-proxy` application. In general a certain "system" will be associated to a group:
1. `atlas-db-conditions-lar` : all users in this egroup can write to tags starting with `LAR`
2. `atlas-db-conditions-tile` : all users in this egroup can write to tags starting with `TILE`
3. `atlas-db-conditions-mdt` : all users in this egroup can write to tags starting with `MDT`

We have groups for each detector (as was done in COOL for schemas). Service accounts can also belong to groups, and specific applications 
with the need of write access can get a `client-id` and a `client-secret` to get a token from Keycloak and use it to write to the database.

**Note**: Public read operations (GET requests) typically require no authentication, enabling open access to conditions data while protecting write operations.

### Method-Level Security

CREST uses the `@AuthorizationControl` annotation for declarative security:

```java
@AuthorizationControl(roles = {"crest-admin", "crest-expert"})
public ResponseEntity<?> createTag(...) {
    // Only accessible by admin and expert users
}
```

### Endpoint Protection

- **Public Endpoints**: Read operations (GET) are generally open
- **Protected Endpoints**: Write operations (POST, PUT, DELETE) require authentication
- **Admin Endpoints**: Administrative functions require `crest-admin` role
- **Expert Operations**: Data modification requires `crest-expert` or `crest-admin`

---

## Security Profiles

CREST supports multiple security profiles to accommodate different deployment scenarios:

### Production Profiles

#### `kc` (Keycloak)
- **Use**: Production deployments with enterprise SSO
- **Authentication**: OAuth2/JWT via Keycloak
- **Security Level**: `active`
- **Recommended for**: Multi-user production environments

#### `db` (Database)
- **Use**: Standalone production deployments
- **Authentication**: Database-backed with BCrypt
- **Security Level**: `active`
- **Recommended for**: Single-instance deployments without SSO

#### `ldap` (LDAP)
- **Use**: Enterprise deployments with existing LDAP
- **Authentication**: LDAP directory service
- **Security Level**: `active`
- **Recommended for**: Organizations with Active Directory or OpenLDAP

### Development/Testing Profiles

#### `none`
- **Use**: Development only, NO SECURITY
- **Authentication**: None - all requests permitted
- **Security Level**: `none`
- **WARNING**: Never use in production!

#### `weak`
- **Use**: Testing with minimal security
- **Authentication**: Permissive mode
- **Security Level**: `weak`
- **WARNING**: Development/testing only!

### Configuration Property

The security level is controlled by:
```properties
crest.server.security=active  # Options: none, weak, active
```

---

## SSL/TLS Support

CREST supports secure HTTPS communication using SSL/TLS certificates.

### Let's Encrypt Integration

For production deployments, CREST can be configured with Let's Encrypt certificates:

**Configuration**:
```properties
server.ssl.enabled=true
server.ssl.key-store=/path/to/keystore.p12
server.ssl.key-store-password=changeit
server.ssl.key-store-type=PKCS12
server.port=8443
```

### Certificate Management

CREST includes certificate tools in `certtools/` directory for:
- Certificate generation
- Certificate renewal
- Keystore management

### HTTP to HTTPS Redirection

When SSL is enabled, CREST can automatically redirect HTTP requests to HTTPS.

---

## CORS and CSRF Protection

### Cross-Origin Resource Sharing (CORS)

CREST includes a `CORSFilter` that manages cross-origin requests:

**Default CORS Policy**:
- Allowed origins: Configurable via `crest.cors.allowed-origins`
- Allowed methods: GET, POST, PUT, DELETE, OPTIONS
- Allowed headers: Authorization, Content-Type, X-Requested-With
- Credentials support: Configurable
- Max age: 3600 seconds

**Configuration**:
```properties
crest.cors.allowed-origins=https://crest-ui.example.com
crest.cors.allow-credentials=true
```

### Cross-Site Request Forgery (CSRF)

CSRF protection is configured based on security mode:
- **API Mode**: CSRF disabled for stateless REST API
- **Session Mode**: CSRF tokens required for form-based authentication

---

## Configuration

### Basic Security Configuration

**application.properties**:
```properties
# Security level: none, weak, active
crest.server.security=active

# Authentication type
crest.auth.type=keycloak  # Options: keycloak, database, ldap

# Active profiles
spring.profiles.active=kc
```

### Keycloak Configuration

**application.properties.kc**:
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://keycloak.example.com/auth/realms/crest-realm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://keycloak.example.com/auth/realms/crest-realm/protocol/openid-connect/certs

# Keycloak client configuration
keycloak.realm=crest-realm
keycloak.resource=crest-client
```

### Database Configuration

For database authentication, ensure users exist in the database:

```sql
-- Create user with BCrypt-hashed password
INSERT INTO CREST_USER (username, password, enabled) 
VALUES ('admin', '$2a$12$hashed_password', true);

-- Assign roles
INSERT INTO CREST_ROLES (username, role) 
VALUES ('admin', 'crest-admin');
```

### LDAP Configuration

**ldap.properties**:
```properties
ldap.url=ldap://ldap.example.com:389
ldap.base.dn=dc=example,dc=com
ldap.user.dn.pattern=uid={0},ou=people
ldap.group.search.base=ou=groups
ldap.group.role.attribute=cn
ldap.group.search.filter=(member={0})
```

---

## Best Practices

### 1. Use Strong Authentication in Production

- **Always** use `active` security level in production
- Prefer Keycloak/OAuth2 for multi-user environments
- Use strong passwords (minimum 12 characters, mixed case, numbers, symbols)

### 2. Protect Sensitive Configuration

- Store credentials in environment variables or secure vaults
- Never commit passwords or secrets to version control
- Use Spring Boot's encrypted properties for sensitive data

### 3. Enable SSL/TLS

- Use HTTPS in production environments
- Obtain certificates from trusted CAs (e.g., Let's Encrypt)
- Enforce TLS 1.2 or higher
- Disable weak cipher suites

### 4. Implement Least Privilege

- Grant users minimum necessary permissions
- Use `crest-expert` role for data managers, not `crest-admin`
- Use `crest-trigger` for automated systems
- Regularly audit user roles and permissions

### 5. Monitor and Log Security Events

- Enable security event logging
- Monitor failed authentication attempts
- Log authorization decisions
- Set up alerts for suspicious activity

### 6. Keep Dependencies Updated

- Regularly update Spring Security and related libraries
- Monitor security advisories
- Apply patches promptly
- Use `./gradlew dependencyUpdates` to check for updates

### 7. Secure Token Management

- Set appropriate token expiration times
- Implement token refresh mechanisms
- Invalidate tokens on logout
- Use secure token storage on client side

### 8. Network Security

- Use firewalls to restrict access
- Implement rate limiting
- Use reverse proxies (nginx, Apache) for additional security
- Enable DDoS protection

---

## Troubleshooting

### Common Issues

#### 1. Authentication Fails with Keycloak

**Symptom**: 401 Unauthorized with valid JWT token

**Solutions**:
- Verify `issuer-uri` matches Keycloak realm
- Check token expiration
- Ensure roles are in `realm_access.roles` or `resource_access.<client>.roles`
- Verify JWT signature with public key

#### 2. CORS Errors

**Symptom**: Browser blocks requests with CORS policy error

**Solutions**:
- Add client origin to `crest.cors.allowed-origins`
- Ensure `OPTIONS` preflight requests are allowed
- Check CORS headers in response
- Verify credentials setting matches client

#### 3. Database Authentication Not Working

**Symptom**: Valid credentials rejected

**Solutions**:
- Verify BCrypt password hash is correct
- Check user enabled flag in database
- Ensure role names match exactly (case-sensitive)
- Verify `spring.profiles.active=db` is set

#### 4. SSL Certificate Issues

**Symptom**: Certificate validation errors

**Solutions**:
- Verify certificate validity period
- Check certificate chain completeness
- Ensure hostname matches certificate CN/SAN
- Update truststore with CA certificates

### Debug Logging

Enable debug logging for security troubleshooting:

```properties
logging.level.org.springframework.security=DEBUG
logging.level.hep.crest.server.security=DEBUG
```

---

## Security Contact

For security vulnerabilities or concerns, please contact the CREST development team.

**Important**: Do not disclose security vulnerabilities in public issue trackers.

---

## References

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [OAuth2 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [JWT RFC 7519](https://tools.ietf.org/html/rfc7519)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [OWASP Security Guidelines](https://owasp.org/)

---

**Last Updated**: July 6, 2026  
**Version**: 1.0.0

