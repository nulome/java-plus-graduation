package ru.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dto.category.CategoryDto;
import ru.dto.category.NewCategoryDto;
import ru.exception.ConflictException;
import ru.exception.NotFoundException;
import ru.feign.EventClient;
import ru.mapper.CategoryMapper;
import ru.model.Category;
import ru.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;
    private final EventClient eventClient;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto categoryDto) {
        Category category = mapper.toCategory(categoryDto);
        return mapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        existCategoryToEvent(catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto change(Long catId, NewCategoryDto categoryDto) {
        Category category = checkCategoryInDataBase(catId);
        category.setName(categoryDto.getName());
        return mapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        List<Category> categories = categoryRepository.findAll(PageRequest.of(from, size)).getContent();

        return categories.stream()
                .map(mapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long catId) {
        return mapper.toCategoryDto(checkCategoryInDataBase(catId));
    }

    @Override
    public Optional<CategoryDto> getCategorOptional(Long catId) {
        Optional<Category> category = categoryRepository.findById(catId);
        return category.map(mapper::toCategoryDto);
    }

    private Category checkCategoryInDataBase(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + id + " не найдена"));
    }

    private void existCategoryToEvent(Long id) {
        if (eventClient.existsByCategoryFromEvent(id)) {
            throw new ConflictException("Удаление категории с привязанными событиями не доступно");
        }
    }

}
