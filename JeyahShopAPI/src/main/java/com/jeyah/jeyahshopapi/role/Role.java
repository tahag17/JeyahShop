package com.jeyah.jeyahshopapi.role;

import com.jeyah.jeyahshopapi.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@Entity
//@EntityListeners(AuditingEntityListener.class)
public class Role {
    @Id
    @GeneratedValue
    private String id;
    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
//    @JsonIgnore
    private List<User> users;


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
}
