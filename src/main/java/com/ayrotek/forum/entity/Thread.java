package com.ayrotek.forum.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
/*import lombok.Getter;
import lombok.Setter;*/
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.ayrotek.forum.entity.Post;



@Entity
@Data
@Table(name = "threads")

public class Thread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modelSlug;

    private String created_at;

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }


    @OneToMany(mappedBy = "thread", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
}
