package angeelya.inPic.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "album")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Boolean security;
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    private User user;
    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(joinColumns = @JoinColumn(name = "album_id") ,
            inverseJoinColumns = @JoinColumn(name = "image_id"))
    private List<Image> images;
}
