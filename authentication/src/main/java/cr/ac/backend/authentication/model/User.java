package cr.ac.backend.authentication.model;

import lombok.*;


import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString


public class User implements Serializable {
    @Getter

    private Long id;

    private String userName;


    private String password;

    private String email;

    private Rol role;


    private boolean enabled;


    private boolean accountNonExpired;

    private boolean credentialsNonExpired;

    private boolean accountNonLocked;


    public Collection<String> getAuthorities() {
        return List.of(role.name());
    }


    public String getPassword() {
        return password;
    }


    public String getUsername() {
        return userName;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                ", accountNonExpired=" + accountNonExpired +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return enabled == user.enabled && accountNonExpired == user.accountNonExpired && credentialsNonExpired == user.credentialsNonExpired && accountNonLocked == user.accountNonLocked && Objects.equals(id, user.id) && Objects.equals(userName, user.userName) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, password, email, role, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked);
    }

    public enum Rol {
         ADMIN, TRAINER, CLIENT
    }
}
