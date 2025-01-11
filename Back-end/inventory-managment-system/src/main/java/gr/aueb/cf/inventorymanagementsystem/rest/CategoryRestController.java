package gr.aueb.cf.inventorymanagementsystem.rest;

import gr.aueb.cf.inventorymanagementsystem.core.exceptions.*;
import gr.aueb.cf.inventorymanagementsystem.core.filters.CategoryFilters;
import gr.aueb.cf.inventorymanagementsystem.dto.*;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryRestController.class);
    private final CategoryService categoryService;

    /**
     * Handles paginated retrieval of categories.
     *
     * <p>This method retrieves a paginated list of categories based on the page number and size provided
     * in the request parameters. It returns a paginated result as a {@link Page} containing
     * {@link CategoryReadOnlyDTO} objects.</p>
     *
     * @param page the page number to retrieve (default is 0)
     * @param size the number of categories per page (default is 5)
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link CategoryReadOnlyDTO}
     */
    @GetMapping("/categories")
    @Operation(
            summary = "Get paginated categories",
            description = "Retrieves a paginated list of categories based on page number and size."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
    })
    public ResponseEntity<Page<CategoryReadOnlyDTO>> getPaginatedCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        LOGGER.info("Starting getPaginatedCategories with page: {} and size: {}", page, size);

        try {
            Page<CategoryReadOnlyDTO> categoriesPage = categoryService.getPaginatedCategories(page, size);
            LOGGER.info("Successfully retrieved page {} of size {}", page, size);
            return new ResponseEntity<>(categoriesPage, HttpStatus.OK);

        } catch (IllegalArgumentException ex) {
            LOGGER.error("Invalid page or size parameters: page={}, size={}", page, size, ex);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (Exception ex) {
            LOGGER.error("An unexpected error occurred while fetching paginated categories: {}", ex.getMessage(), ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetches a category by its ID.
     *
     * <p>This method retrieves a single category by its unique identifier. If the category does
     * not exist, it throws an {@link AppObjectNotFoundException}.
     * </p>
     *
     * @param categoryId the unique identifier of the category
     * @return a {@link ResponseEntity} containing the {@link CategoryReadOnlyDTO}
     * @throws AppObjectNotFoundException if the category with the given ID is not found
     */
    @GetMapping("/categories/{categoryId}")
    @Operation(
            summary = "Get a category by ID",
            description = "Retrieves a single category by its unique identifier.",
            parameters = @Parameter(name = "categoryId", description = "The ID of the category to retrieve", required = true)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<CategoryReadOnlyDTO> getCategory(
            @PathVariable Long categoryId) throws AppObjectNotFoundException {

        LOGGER.info("Starting getCategory for ID: {}", categoryId);

        try {
            // Εύρεση της κατηγορίας
            CategoryReadOnlyDTO category = categoryService.getCategory(categoryId);
            LOGGER.info("Successfully fetched category with ID: {}", categoryId);
            return new ResponseEntity<>(category, HttpStatus.OK);

        } catch (AppObjectNotFoundException ex) {
            LOGGER.error("Category with ID {} not found: {}", categoryId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            LOGGER.error("An unexpected error occurred while fetching category with ID {}: {}", categoryId, ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates a category by its ID.
     *
     * <p>This method updates a category's details based on the provided ID and
     * update information. If the category does not exist, an {@link AppGenericException}
     * is thrown. If an unexpected error occurs, an {@link AppServerException} is thrown.</p>
     *
     * @param categoryId the ID of the category to be updated
     * @param categoryUpdateDTO the data transfer object containing updated category information
     * @return a {@link ResponseEntity} containing the updated {@link CategoryReadOnlyDTO}
     * @throws AppGenericException if the category with the given ID is not found
     * @throws AppServerException if an unexpected error occurs during the update
     */
    @PutMapping("/categories/{categoryId}")
    @Operation(
            summary = "Update a category by ID",
            description = "Updates the details of a category by its ID. The updated category is returned as a DTO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<CategoryReadOnlyDTO> updateCategory(
            @PathVariable @Parameter(description = "The ID of the category to update", required = true) Long categoryId,
            @RequestBody @Parameter(description = "The updated details of the category", required = true) CategoryUpdateDTO categoryUpdateDTO
    ) throws AppGenericException, AppServerException {

        LOGGER.info("Starting updateCategory for ID: {}", categoryId);

        try {
            // Ορισμός του ID της κατηγορίας στο DTO
            categoryUpdateDTO.setId(categoryId);
            LOGGER.debug("Updating category with ID: {} and details: {}", categoryId, categoryUpdateDTO);

            // Κλήση της μεθόδου updateCategory στο Service
            CategoryReadOnlyDTO updatedCategory = categoryService.updateCategory(categoryUpdateDTO);
            LOGGER.info("Successfully updated category with ID: {}", categoryId);

            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);

        } catch (AppGenericException ex) {
            LOGGER.error("Category with ID {} not found: {}", categoryId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            LOGGER.error("An unexpected error occurred while updating category with ID {}: {}", categoryId, ex.getMessage());
            throw new AppServerException("Error updating category", ex.getMessage());
        }
    }

    /**
     * Deletes a category by its ID.
     *
     * <p>This method deletes a category from the database based on its ID. If the category does
     * not exist or if it cannot be deleted due to associations with other entities, an exception is thrown.</p>
     *
     * @param categoryId the ID of the category to be deleted
     * @return a {@link ResponseEntity} containing the details of the deleted {@link CategoryReadOnlyDTO}
     * @throws AppObjectNotFoundException if the category with the given ID is not found
     * @throws AppGenericException if the category cannot be deleted due to associations with other entities
     * @throws AppServerException for unexpected errors during the deletion
     */
    @DeleteMapping("/categories/{categoryId}")
    @Operation(
            summary = "Delete a category by ID",
            description = "Deletes a category by its ID. If the category cannot be deleted due to associations, an exception is returned."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "400", description = "Category cannot be deleted due to associations"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<CategoryReadOnlyDTO> deleteCategory(
            @PathVariable @Parameter(description = "The ID of the category to delete", required = true) Long categoryId
    ) throws AppObjectNotFoundException, AppServerException, AppGenericException {

        LOGGER.info("Starting deleteCategory for ID: {}", categoryId);

        try {
            // Κλήση της μεθόδου deleteCategory στο Service
            LOGGER.debug("Attempting to delete category with ID: {}", categoryId);
            CategoryReadOnlyDTO deletedCategory = categoryService.deleteCategory(categoryId);
            LOGGER.info("Successfully deleted category with ID: {}", categoryId);

            return new ResponseEntity<>(deletedCategory, HttpStatus.OK);

        } catch (AppObjectNotFoundException ex) {
            LOGGER.error("Category with ID {} not found: {}", categoryId, ex.getMessage());
            throw ex;

        } catch (AppGenericException ex) {
            LOGGER.error("Category with ID {} cannot be deleted due to associations: {}", categoryId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            LOGGER.error("An unexpected error occurred while deleting category with ID {}: {}", categoryId, ex.getMessage());
            throw new AppServerException("Error deleting category", ex.getMessage());
        }
    }

    /**
     * Fetches all categories.
     *
     * <p>This method retrieves all categories from the database and returns them as a list of
     * {@link CategoryReadOnlyDTO} objects.</p>
     *
     * @return a {@link ResponseEntity} containing a list of all {@link CategoryReadOnlyDTO}
     */
    @GetMapping("/categories/getAll")
    @Operation(
            summary = "Get all categories",
            description = "Retrieves a list of all categories from the database."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<List<CategoryReadOnlyDTO>> getAllCategories() {

        LOGGER.info("Starting getAllCategories");

        try {
            // Ανάκτηση όλων των κατηγοριών
            LOGGER.debug("Fetching all categories from the database");
            List<CategoryReadOnlyDTO> allCategories = categoryService.getAllCategories();

            LOGGER.info("Successfully fetched {} categories", allCategories.size());
            return ResponseEntity.ok(allCategories);

        } catch (Exception ex) {
            LOGGER.error("An unexpected error occurred while fetching all categories: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Fetches a filtered and paginated list of categories.
     *
     * <p>This method retrieves categories based on the provided filters and pagination parameters.
     * If no filters are provided, it fetches all categories. The results are returned as a paginated
     * {@link Page} of {@link CategoryReadOnlyDTO}.</p>
     *
     * @param filters the filters to apply to the category search (optional)
     * @param page the page number to retrieve (default is 0)
     * @param size the number of categories per page (default is 5)
     * @param principal the currently authenticated user
     * @return a {@link ResponseEntity} containing a paginated list of {@link CategoryReadOnlyDTO}
     * @throws AppObjectNotFoundException if no categories match the filters
     * @throws AppObjectNotAuthorizedException if the user is not authorized to access the categories
     */
    @PostMapping("/categories/all")
    @Operation(
            summary = "Get filtered and paginated categories",
            description = "Fetches a paginated list of categories based on the provided filters and pagination parameters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories fetched successfully"),
            @ApiResponse(responseCode = "404", description = "No categories found matching the filters"),
            @ApiResponse(responseCode = "403", description = "User not authorized to access categories"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<Page<CategoryReadOnlyDTO>> getCategories(@Nullable @RequestBody CategoryFilters filters,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "5") int size,
                                                                  Principal principal)
            throws AppObjectNotFoundException, AppObjectNotAuthorizedException {
        try {
            if (filters == null) filters = CategoryFilters.builder().build();
            Page<CategoryReadOnlyDTO> paginatedCategories = categoryService.getCategoriesFiltered(filters, page, size);
            return ResponseEntity.ok(paginatedCategories);
        } catch (Exception e) {
            LOGGER.error("ERROR: Could not get categories.", e);
            throw e;
        }
    }


    /**
     * Saves a new category.
     *
     * @param categoryInsertDTO the DTO containing the details of the category to be created.
     * @return a {@link CategoryReadOnlyDTO} representing the saved category.
     */
    @Operation(
            summary = "Create a new category",
            description = "Creates a new category in the repository. Throws an exception if the category already exists."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryReadOnlyDTO.class))),
            @ApiResponse(responseCode = "400", description = "Category already exists",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping(value = "/categories/save")
    public ResponseEntity<CategoryReadOnlyDTO> saveCategory(
            @Valid @RequestBody CategoryInsertDTO categoryInsertDTO,
            BindingResult bindingResult)throws ValidationException, AppObjectAlreadyExists{
        // Check if there are validation errors
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        CategoryReadOnlyDTO categoryReadOnlyDTO = categoryService.saveCategory(categoryInsertDTO);
        return new ResponseEntity<>(categoryReadOnlyDTO, HttpStatus.OK);

    }


}
