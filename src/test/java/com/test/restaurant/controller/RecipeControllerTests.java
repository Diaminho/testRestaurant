package com.test.restaurant.controller;

import com.test.restaurant.RestaurantApplication;
import com.test.restaurant.entity.Recipe;
import com.test.restaurant.repository.RecipeRepository;
import com.test.restaurant.service.RecipeService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = RestaurantApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RecipeControllerTests {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;


    private MockMvc restCategoryMockMvc;

    private Recipe recipe;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RecipeController recipeController = new RecipeController(recipeService, modelMapper);
        this.restCategoryMockMvc = MockMvcBuilders.standaloneSetup(recipeController)
                .setMessageConverters(jacksonMessageConverter)
                .build();
    }

    @BeforeAll
    public void initTest() {
        recipe = createEntity();
        recipeRepository.saveAndFlush(recipe);
    }

    public static Recipe createEntity() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Name");
        recipe.setDescription("Descr");
        return recipe;
    }


    @Test
    @Transactional
    public void getAllRecipes() throws Exception {
        // Get all the recipes
        restCategoryMockMvc.perform(get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId().intValue())));
    }

    @Test
    @Transactional
    public void getRecipeById() throws Exception {
        // Get recipe by id
        restCategoryMockMvc.perform(get("/recipes/{id}", recipe.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingRecipeById() throws Exception {
        // Get non existing recipe by id
        restCategoryMockMvc.perform(get("/recipes/{id}", 2))
                .andExpect(status().isNotFound());
    }


    @Test
    @Transactional
    public void deleteRecipeById() throws Exception {
        // Delete existing recipe by id
        restCategoryMockMvc.perform(delete("/recipes/{id}", recipe.getId()))
                .andExpect(status().isOk());
        assertThat(recipeRepository.findAll()).hasSize(0);
    }

    @Test
    @Transactional
    public void deleteNonExistingRecipeById() throws Exception {
        // Delete non existing recipe by id
        restCategoryMockMvc.perform(delete("/recipes/2"))
                .andExpect(status().isNotFound());
        assertThat(recipeRepository.findAll()).hasSize(1);
    }

    @Test
    @Transactional
    public void updateRecipeById() throws Exception {
        final String json = "{\"name\": \"NEW\", \"description\": \"NEW DESCR\"}";
        // Update existing recipe by id
        restCategoryMockMvc.perform(put("/recipes/{id}", recipe.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(recipe.getId().intValue()))
                .andExpect(jsonPath("name").value("NEW"))
                .andExpect(jsonPath("description").value("NEW DESCR"));

        assertThat(recipeRepository.findAll()).hasSize(1);
    }

    @Test
    @Transactional
    public void updateNonExistingRecipeById() throws Exception {
        final String json = "{\"name\": \"NEW\", \"description\": \"NEW DESCR\"}";
        // Update non existing recipe by id
        restCategoryMockMvc.perform(put("/recipes/2")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());

        assertThat(recipeRepository.findAll()).hasSize(1);
    }

    @Test
    @Transactional
    public void createRecipe() throws Exception {
        final String json = "{\"name\": \"NEW\", \"description\": \"NEW DESCR\"}";
        // Create recipe
        restCategoryMockMvc.perform(post("/recipes")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(Matchers.not(recipe.getId().intValue())))
                .andExpect(jsonPath("name").value("NEW"))
                .andExpect(jsonPath("description").value("NEW DESCR"));

        assertThat(recipeRepository.findAll()).hasSize(2);
    }
}
