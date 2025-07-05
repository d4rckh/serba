package com.serba.entity;


import org.hibernate.annotations.ColumnDefault;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "user_library_access")
@Data
@Serdeable
public class UserLibraryAccessEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "library_id", nullable = false)
    private LibraryEntity library;

    @Column(name = "view_library", nullable = false)
    @ColumnDefault("false")
    private boolean viewLibrary;

    public boolean isViewLibrary() {
        return viewLibrary || (this.getUser() != null && this.getUser().getSuperUser().equals(true));
    }
}
