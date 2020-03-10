package com.test.restaurant.controller;

import com.test.restaurant.entity.Ingredient;
import com.test.restaurant.service.IngredientService;
import com.test.restaurant.service.RecipeService;
import com.test.restaurant.service.dto.IngredientDTO;
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
@RequestMapping("/ingredients")
public class IngredientController {
    private final Logger log = LoggerFactory.getLogger(IngredientController.class);

    private final IngredientService ingredientService;
    private final RecipeService recipeService;
    private final ModelMapper modelMapper;

    public IngredientController(IngredientService ingredientService, RecipeService recipeService, ModelMapper modelMapper) {
        this.ingredientService = ingredientService;
        this.recipeService = recipeService;
        this.modelMapper = modelMapper;
    }

    /**
     * {@code GET /ingredients} : Get a list of all ingredients.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body with the list of {@link IngredientDTO},
     */
    @GetMapping("")
    public ResponseEntity<List<IngredientDTO>> getAllIngredients() {
        log.debug("REST request to get all Ingredients");
        final List<Ingredient> ingredients = ingredientService.findAll();
        final List<IngredientDTO> ingredientDTOS =  ingredients.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(ingredientDTOS, HttpStatus.OK);
    }

    /**
     * {@code GET /ingredients/{id}} : Get an ingredient by id.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body with the found {@link IngredientDTO},
     */
    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getIngredient(@PathVariable(name = "id") Long id) {
        log.debug("REST request to get Ingredient by Id");
        final Ingredient foundIngredient = ingredientService.findById(id);
        if (foundIngredient == null) {
            log.error("Ingredient with id: " + id + " is not found");
            return ResponseEntity.notFound().build();
        }
        final IngredientDTO found = convertToDto(foundIngredient);
        return new ResponseEntity<>(found, HttpStatus.OK);
    }

    /**
     * {@code POST /ingredients} : Create a new ingredient.
     * @param ingredientDTO
     * @return the {@link ResponseEntity} with status {@code 200(OK)} and with body with the created Ingredient as {@link IngredientDTO}
     */
    @PostMapping("")
    public ResponseEntity<IngredientDTO> createIngredient(@RequestBody @Valid IngredientDTO ingredientDTO) {
        log.debug("REST request to create Ingredient");
        final Ingredient ingredient = convertToEntity(ingredientDTO);
        final IngredientDTO saved = convertToDto(ingredientService.save(ingredient));
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }


    /**
     * {@code PUT /ingredients/{id}} : Update an existing Ingredient.
     * @param id of the Ingredient to update
     * @param ingredientDTO
     * @return the {@link ResponseEntity} with status {@code 200(OK)} and with body with the updated Ingredient as {@link IngredientDTO}
     */
    @PutMapping("/{id}")
    public ResponseEntity<IngredientDTO> updateIngredient(@PathVariable(name = "id") Long id, @RequestBody @Valid IngredientDTO ingredientDTO) {
        log.debug("REST request to update Ingredient");
        if (!isFound(id)) {
            log.debug("Ingredient with id: " + id + " is not found");
            return ResponseEntity.notFound().build();
        }
        ingredientDTO.setId(id);
        final Ingredient ingredient = convertToEntity(ingredientDTO);
        final IngredientDTO updated = convertToDto(ingredientService.save(ingredient));
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    /**
     * {@code DELETE /ingredients/{id}} : Delete an existing Ingredient by id.
     * @param id
     * @return the {@link ResponseEntity} with status {@code 200(OK)} and empty body
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        log.debug("REST request to delete Ingredient by id");
        if (!isFound(id)) {
            log.debug("Ingredient with id: " + id + " is not found");
            return ResponseEntity.notFound().build();
        }
        ingredientService.delete(id);
        return ResponseEntity.ok().build();
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////

    private IngredientDTO convertToDto(Ingredient ingredient) {
        return modelMapper.map(ingredient, IngredientDTO.class);
    }

    private Ingredient convertToEntity(IngredientDTO ingredientDTO) {
        Ingredient ingredient = modelMapper.map(ingredientDTO, Ingredient.class);
        if (ingredientDTO.getRecipe() == null) {
            ingredient.setRecipe(null);
        } else if (ingredientDTO.getId() != null) {
            ingredient.setRecipe(recipeService.findById(ingredientDTO.getRecipe().getId()));
        }
        return ingredient;
    }

    private boolean isFound(Long id) {
        return ingredientService.findById(id) != null;
    }

}
