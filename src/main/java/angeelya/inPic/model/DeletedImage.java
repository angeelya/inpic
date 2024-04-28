package angeelya.inPic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deleted_image")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeletedImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String path;
    private String cause;
    @Column(name = "img_name")
    private String imgName;
}
