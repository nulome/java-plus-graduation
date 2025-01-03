package ru.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dto.category.CategoryDto;
import ru.service.CategoryService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Tag(name = "Public: Категории", description = "Публичный API для работы с категориями")
public class PublicCategoriesController {
    private final CategoryService service;

    @GetMapping
    @Operation(summary = "получение категорий")
    public List<CategoryDto> getCategories(
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение категорий from = {}, size = {}", from, size);
        return service.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    @Operation(summary = "Получение информации о категории по идентификатору")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("Получение категории id = {}", catId);
        return service.getCategoryById(catId);
    }
}
