package ru.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.dto.category.CategoryDto;

import java.util.Optional;


@FeignClient(name = "categories-server", fallback = CategoryClientFallback.class)
public interface CategoryClient {

    @GetMapping("/feign/categories/{catId}")
    Optional<CategoryDto> getCategory(@PathVariable Long catId);

}
