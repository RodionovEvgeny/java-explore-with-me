package ru.practicum.service.category;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.exceptions.ConflictException;
import ru.practicum.service.exceptions.EntityNotFoundException;

@Service
@AllArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        validateCategoryName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    public void deleteCategory(Long catId) {
        validateCategoryByID(catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        validateCategoryByID(catId);
        validateCategoryName(categoryDto.getName());
        categoryDto.setId(catId);
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    public Category getCategoryById(Long catId) {
        return validateCategoryByID(catId);
    }


    private Category validateCategoryByID(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Категория с id = %s не найдена!", catId),
                Category.class.getName()));
    }

    private void validateCategoryName(String name) {
        if (!categoryRepository.findByName(name).isEmpty()) {
            throw new ConflictException(String.format("Категория с именем = %s уже существует!", name));
        }
    }
}
