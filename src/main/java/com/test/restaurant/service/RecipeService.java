package com.test.restaurant.service;

import com.test.restaurant.entity.Recipe;
import com.test.restaurant.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository repository;

    public RecipeService(RecipeRepository repository) {
        this.repository = repository;
    }

    public List<Recipe> findAll() {
        return repository.findAll();
    }

    public Recipe findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Recipe save(Recipe recipe) {
        return repository.save(recipe);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

}
