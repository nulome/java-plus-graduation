package ru.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.dto.category.CategoryDto;
import ru.dto.category.NewCategoryDto;
import ru.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category toCategory(NewCategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);
}
