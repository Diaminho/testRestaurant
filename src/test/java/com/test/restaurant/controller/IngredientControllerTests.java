package com.test.restaurant.controller;

import com.test.restaurant.RestaurantApplication;
import com.test.restaurant.entity.Ingredient;
import com.test.restaurant.entity.Recipe;
import com.test.restaurant.repository.IngredientRepository;
import com.test.restaurant.repository.RecipeRepository;
import com.test.restaurant.service.IngredientService;
import com.test.restaurant.service.RecipeService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
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

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = RestaurantApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class IngredientControllerTests {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;


    private MockMvc restCategoryMockMvc;

    private Ingredient ingredient;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final IngredientController ingredientController = new IngredientController(ingredientService, recipeService, modelMapper);
        this.restCategoryMockMvc = MockMvcBuilders.standaloneSetup(ingredientController)
                .setMessageConverters(jacksonMessageConverter)
                .build();
    }

    @BeforeAll
    public void initTest() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("REC1");
        recipe.setDescription("Desc");
        recipeRepository.save(recipe);
        Recipe recipe2 = new Recipe();
        recipe2.setId(2L);
        recipe2.setName("REC2");
        recipe2.setDescription("Desc2");
        recipeRepository.save(recipe2);

        ingredient = createIngredientEntity(recipe);
        ingredientRepository.saveAndFlush(ingredient);
    }

    public static Ingredient createIngredientEntity(Recipe recipe) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("INGR");
        ingredient.setId(1L);
        ingredient.setRecipe(recipe);
        return ingredient;
    }


    @Test
    @Transactional
    public void getAllIngredients() throws Exception {
        // Get all the ingredients
        restCategoryMockMvc.perform(get("/ingredients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(ingredient.getId().intValue())));
    }

    @Test
    @Transactional
    public void getIngredientById() throws Exception {
        // Get ingredient by id
        restCategoryMockMvc.perform(get("/ingredients/{id}", ingredient.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(ingredient.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingIngredientById() throws Exception {
        // Get non existing ingredient by id
        restCategoryMockMvc.perform(get("/ingredients/{id}", 2))
                .andExpect(status().isNotFound());
    }


    @Test
    @Transactional
    public void deleteIngredientById() throws Exception {
        // Delete existing ingredient by id
        restCategoryMockMvc.perform(delete("/ingredients/{id}", ingredient.getId()))
                .andExpect(status().isOk());
        assertThat(ingredientRepository.findAll()).hasSize(0);
    }

    @Test
    @Transactional
    public void deleteNonExistingIngredientById() throws Exception {
        // Delete non existing ingredient by id
        restCategoryMockMvc.perform(delete("/ingredients/2"))
                .andExpect(status().isNotFound());
        assertThat(ingredientRepository.findAll()).hasSize(1);
    }

    @Test
    @Transactional
    public void updateIngredientById() throws Exception {
        final String json = "{\"name\": \"NEW\", \"recipe\": {\"id\": 2}}";
        // Update existing ingredient by id
        restCategoryMockMvc.perform(put("/ingredients/{id}", ingredient.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(ingredient.getId().intValue()))
                .andExpect(jsonPath("name").value("NEW"))
                .andExpect(jsonPath("recipe.id").value(2));

        assertThat(ingredientRepository.findAll()).hasSize(1);
    }

    @Test
    @Transactional
    public void updateNonExistingIngredientById() throws Exception {
        final String json = "{\"name\": \"NEW\"}";
        // Update non existing ingredient by id
        restCategoryMockMvc.perform(put("/ingredients/2")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());

        assertThat(ingredientRepository.findAll()).hasSize(1);
    }

    @Test
    @Transactional
    public void createIngredient() throws Exception {
        final String json = "{\"name\": \"NEW\"}";
        // Create ingredient
        restCategoryMockMvc.perform(post("/ingredients")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(Matchers.not(ingredient.getId().intValue())))
                .andExpect(jsonPath("name").value("NEW"));

        assertThat(ingredientRepository.findAll()).hasSize(2);
    }
}
