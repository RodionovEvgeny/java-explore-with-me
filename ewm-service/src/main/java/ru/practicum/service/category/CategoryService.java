package ru.practicum.service.category;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    CategoryDto getCategoryById(Long catId);

    List<CategoryDto> getAllCategories(Integer from, Integer size);
}
