package ru.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dto.category.CategoryDto;
import ru.service.CategoryService;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feign/categories")
public class FeignCategoryController {

    private final CategoryService service;

    @GetMapping("/{catId}")
    public Optional<CategoryDto> getCategorOptional(@PathVariable Long catId) {
        log.info("Запрос feign на получение категории Category = {}", catId);
        return service.getCategorOptional(catId);
    }

}
