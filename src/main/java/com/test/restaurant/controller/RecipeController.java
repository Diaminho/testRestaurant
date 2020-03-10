package com.test.restaurant.controller;

import com.test.restaurant.entity.Recipe;
import com.test.restaurant.service.RecipeService;
import com.test.restaurant.service.dto.RecipeDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final Logger log = LoggerFactory.getLogger(RecipeController.class);

    private final RecipeService recipeService;
    private final ModelMapper modelMapper;

    public RecipeController(RecipeService recipeService, ModelMapper modelMapper) {
        this.recipeService = recipeService;
        this.modelMapper = modelMapper;
    }

    /**
     * {@code GET /recipes : Get a list of all recipes.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body with the list of {@link RecipeDTO},
     */
    @GetMapping("")
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        log.debug("REST request to get all recipes");
        final List<Recipe> recipes = recipeService.findAll();
        final List<RecipeDTO> recipeDTOS =  recipes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(recipeDTOS, HttpStatus.OK);
    }

    /**
     * {@code GET /recipes/{id}} : Get a recipe by id.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body with the found {@link RecipeDTO},
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable(name = "id") Long id) {
        log.debug("REST request to get Recipe by Id");
        final Recipe foundRecipe = recipeService.findById(id);
        if (foundRecipe == null) {
            log.error("Recipe with id: " + id + " is not found");
            return ResponseEntity.notFound().build();
        }
        final RecipeDTO found = convertToDto(foundRecipe);
        return new ResponseEntity<>(found, HttpStatus.OK);
    }

    /**
     * {@code POST /recipes} : Create a new Recipe.
     * @param recipeDTO
     * @return the {@link ResponseEntity} with status {@code 200(OK)} and with body with the created Recipe as {@link RecipeDTO}
     */
    @PostMapping("")
    public ResponseEntity<RecipeDTO> create(@RequestBody @Valid RecipeDTO recipeDTO) {
        log.debug("REST request to create Recipe");
        final Recipe recipe = convertToEntity(recipeDTO);
        final RecipeDTO saved = convertToDto(recipeService.save(recipe));
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }


    /**
     * {@code PUT /recipes/{id}} : Update an existing Recipe.
     * @param id of the Recipe to update
     * @param recipeDTO
     * @return the {@link ResponseEntity} with status {@code 200(OK)} and with body with the updated Recipe as {@link RecipeDTO}
     */
    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> update(@PathVariable(name = "id") Long id, @RequestBody @Valid RecipeDTO recipeDTO) {
        log.debug("REST request to update Recipe");
        if (!isFound(id)) {
            log.error("Recipe with id: " + id + " is not found");
            return ResponseEntity.notFound().build();
        }
        recipeDTO.setId(id);
        final Recipe recipe = convertToEntity(recipeDTO);
        final RecipeDTO updated = convertToDto(recipeService.save(recipe));
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    /**
     * {@code DELETE /recipes/{id}} : Delete an existing Recipe by id.
     * @param id
     * @return the {@link ResponseEntity} with status {@code 200(OK)} and empty body
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        log.debug("REST request to delete Recipe by id");
        if (!isFound(id)) {
            log.debug("Recipe with id: " + id + " is not found");
            return ResponseEntity.notFound().build();
        }
        recipeService.delete(id);
        return ResponseEntity.ok().build();
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////

    private RecipeDTO convertToDto(Recipe recipe) {
        return modelMapper.map(recipe, RecipeDTO.class);
    }

    private Recipe convertToEntity(RecipeDTO recipeDTO) {
        return modelMapper.map(recipeDTO, Recipe.class);
    }

    private boolean isFound(Long id) {
        return recipeService.findById(id) != null;
    }
}
