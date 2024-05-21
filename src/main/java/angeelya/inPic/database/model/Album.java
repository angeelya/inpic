package angeelya.inPic.database.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "album")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Boolean security;
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false)
    private User user;
    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(joinColumns = @JoinColumn(name = "album_id") ,
            inverseJoinColumns = @JoinColumn(name = "image_id"))
    private List<Image> images;
}
