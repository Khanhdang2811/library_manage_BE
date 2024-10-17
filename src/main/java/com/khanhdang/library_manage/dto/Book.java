package com.khanhdang.library_manage.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table (name = "book")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class Book {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column ( name = "id_book")
    Long id;
    @ManyToOne
    @JoinColumn ( name ="id_author")
    Author author;
    @ManyToOne
    @JoinColumn (name = "id_category")
    Category category;
    @Column ( name = "title",nullable = false)
    String title;
    @Column ( name = "description",nullable = false)
    String description;
    @Column ( name = "published_year",nullable = false)
    Long publishedYear;
    @Column ( name = "quantity",nullable = false)
    Long quantity;
    @Column ( name = "image_url")
    String imageUrl;
}
