package ru.practicum.service.category;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    Category getCategoryById(Long catId);
}
