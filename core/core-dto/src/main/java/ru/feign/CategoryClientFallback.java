package ru.feign;

import ru.dto.category.CategoryDto;
import ru.exception.FeignException;

import java.util.Optional;

public class CategoryClientFallback implements CategoryClient {
    @Override
    public Optional<CategoryDto> getCategory(Long catId) {
        throw new FeignException("Ошибка запроса через Feign CategoryClient");
    }
}
