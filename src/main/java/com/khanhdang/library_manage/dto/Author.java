package com.khanhdang.library_manage.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table ( name = "author")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults ( level = AccessLevel.PRIVATE)
public class Author {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column ( name = "id_author")
    Long id;
    @Column ( name = "author_name")
    String authorName;
}
