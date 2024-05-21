package angeelya.inPic.database.model;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
@Entity
@Table(name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "login",unique = true)
    private String login;
    private String password;
    private String description;
    @Column(name = "email",unique = true)
    private String email;
    private String name;
    @Enumerated(EnumType.STRING)
    private Role role;
    @JsonIdentityReference(alwaysAsId = true)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserImage userImage;
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Like> likes;
    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Action> actions;
    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Album> albums;
    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<CategoryRecommendation> categoryRecommendations;
    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Comment> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Friend> subscription;
    @JsonIgnore
    @OneToMany(mappedBy = "subFriend",cascade = CascadeType.ALL)
    private List<Friend> subscribers;
    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Image> images;
    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Recommendation> recommendations;
    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<SavedImage> savedImages;
    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Search> searches;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return login;
    }

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
