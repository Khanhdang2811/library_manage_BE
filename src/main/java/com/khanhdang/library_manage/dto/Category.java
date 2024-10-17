package com.khanhdang.library_manage.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.naming.Name;

@Entity
@Table (name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults ( level = AccessLevel.PRIVATE)
public class Category {
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column (name = "id_category")
    Long id;
    @Column (name = "category_name",nullable = false)
    String categoryName;
}
