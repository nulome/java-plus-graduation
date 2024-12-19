package ru.service;

import ru.dto.category.CategoryDto;
import ru.dto.category.NewCategoryDto;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    CategoryDto create(NewCategoryDto categoryDto);

    void delete(Long catId);

    CategoryDto change(Long catId, NewCategoryDto categoryDto);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);

    Optional<CategoryDto> getCategorOptional(Long catId);
}
