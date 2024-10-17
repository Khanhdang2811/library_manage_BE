package com.khanhdang.library_manage.mapper;

import com.khanhdang.library_manage.dto.Author;
import com.khanhdang.library_manage.dto.Book;
import com.khanhdang.library_manage.dto.Category;
import com.khanhdang.library_manage.exception.BookException;
import com.khanhdang.library_manage.repository.AuthorRepository;
import com.khanhdang.library_manage.repository.CategoryRepository;
import com.khanhdang.library_manage.request.Book.BookCreationRequest;
import com.khanhdang.library_manage.response.BookResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {AuthorRepository.class, CategoryRepository.class})
public abstract class BookMapper {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Mapping(target = "authorName", expression = "java(book.getAuthor().getAuthorName())")
    @Mapping(target = "categoryName", expression = "java(book.getCategory().getCategoryName())")
    public abstract BookResponseDTO bookToBookResponseDTO(Book book);

    @Mapping(source = "id_author", target = "author",qualifiedByName = "mapAuthorFromId")
    @Mapping(source = "id_category", target = "category",qualifiedByName = "mapCategoryFromId")
    @Mapping(source = "publishedYear", target = "publishedYear")
    @Mapping(source = "image_url", target = "imageUrl")
    public abstract Book bookCreationRqToBook(BookCreationRequest request);

    @Named("mapAuthorFromId")
    protected Author mapAuthorFromId(Long id_author) {
        if (id_author == null) {
            return null;
        }
        return authorRepository.findById(id_author)
                .orElseThrow(() -> new BookException("Author not found"));
    }

    @Named("mapCategoryFromId")
    protected Category mapCategoryFromId(Long id_category) {
        if (id_category == null) {
            return null;
        }
        return categoryRepository.findById(id_category)
                .orElseThrow(() -> new BookException("Category not found"));
    }
}
