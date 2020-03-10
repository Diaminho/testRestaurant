package com.test.restaurant.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class IngredientDTO {
    private Long id;

    @NotBlank
    @Size(min = 1, max = 40)
    private String name;

    private RecipeDTO recipe;


    public IngredientDTO() {
    }


    public RecipeDTO getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeDTO recipe) {
        this.recipe = recipe;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
