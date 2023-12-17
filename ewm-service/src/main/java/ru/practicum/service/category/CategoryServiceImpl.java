package ru.practicum.service.category;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.event.EventRepository;
import ru.practicum.service.exceptions.ConflictException;
import ru.practicum.service.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;


    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        validateCategoryName(categoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    public void deleteCategory(Long catId) {
        validateCategoryByID(catId);
        if (eventRepository.countByCategoryId(catId) != 0) {
            throw new ConflictException("К данной категории привязаны собития.");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        validateCategoryByID(catId);
        categoryDto.setId(catId);
        validateCategoryName(categoryDto);

        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return CategoryMapper.toCategoryDto(validateCategoryByID(catId));
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }


    private Category validateCategoryByID(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Категория с id = %s не найдена!", catId),
                Category.class.getName()));
    }

    private void validateCategoryName(CategoryDto categoryDto) {
        List<Category> categories = categoryRepository.findByName(categoryDto.getName());
        if (!categories.isEmpty() && !Objects.equals(categories.get(0).getId(), categoryDto.getId())) {
            throw new ConflictException(String.format("Категория с именем = %s уже существует!", categoryDto.getName()));
        }
    }
}
