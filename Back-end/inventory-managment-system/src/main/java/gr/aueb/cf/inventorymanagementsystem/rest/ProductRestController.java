package gr.aueb.cf.inventorymanagementsystem.rest;

import gr.aueb.cf.inventorymanagementsystem.core.exceptions.*;
import gr.aueb.cf.inventorymanagementsystem.core.filters.ProductFilters;
import gr.aueb.cf.inventorymanagementsystem.dto.*;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
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
public class ProductRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestController.class);
    private final ProductService productService;
    private final Mapper mapper;

    @GetMapping("/products")
    @Operation(
            summary = "Get paginated products",
            description = "Fetches a paginated list of products with optional page number and page size parameters."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class) // Page<ProductReadOnlyDTO> schema
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<Page<ProductReadOnlyDTO>> getPaginatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<ProductReadOnlyDTO> productsPage = productService.getPaginatedProducts(page, size);
        return new ResponseEntity<>(productsPage, HttpStatus.OK);
    }

    @PostMapping("/products/all")
    @Operation(
            summary = "Get filtered paginated products",
            description = "Fetches a paginated list of products based on optional filters and pagination parameters."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class) // Page<ProductReadOnlyDTO> schema
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Products not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content
            )
    })
    public ResponseEntity<Page<ProductReadOnlyDTO>> getProducts(@Nullable @RequestBody ProductFilters filters,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "5") int size,
                                                                Principal principal)
            throws AppObjectNotFoundException, AppObjectNotAuthorizedException {
        try {
            if (filters == null) filters = ProductFilters.builder().build();
            Page<ProductReadOnlyDTO> paginatedProducts = productService.getProductsFiltered(filters, page, size);
            return ResponseEntity.ok(paginatedProducts);
        } catch (Exception e) {
            LOGGER.error("ERROR: Could not get products.", e);
            throw e;
        }
    }

    @GetMapping("/products/getAll")
    @Operation(
            summary = "Get all products",
            description = "Fetches a complete list of all products in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<List<ProductReadOnlyDTO>> getAllProducts() {
        List<ProductReadOnlyDTO> allProducts = productService.getAllProducts();
        return ResponseEntity.ok(allProducts);
    }

    @DeleteMapping("/products/{productId}")
    @Operation(
            summary = "Delete a product by ID",
            description = "Deletes an existing product based on its unique ID. Returns the details of the deleted product."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<ProductReadOnlyDTO> deleteProduct(@PathVariable Long productId) throws AppObjectNotFoundException {
        ProductReadOnlyDTO deletedProduct = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }

    @PutMapping("/products/{productId}")
    @Operation(
            summary = "Update a product by ID",
            description = "Updates the details of an existing product using the provided product ID and updated data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<ProductReadOnlyDTO> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateDTO productUpdateDTO
    ) throws AppGenericException, AppServerException {
        productUpdateDTO.setId(productId);

        ProductReadOnlyDTO updatedProduct = productService.updateProduct(productUpdateDTO);

        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @GetMapping("/products/{productId}")
    @Operation(
            summary = "Get a product by ID",
            description = "Fetches the details of a specific product using its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<ProductReadOnlyDTO> getProduct(
            @PathVariable Long productId) throws AppObjectNotFoundException {

        ProductReadOnlyDTO product = productService.getProduct(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping(value = "/products/save")
    @Operation(
            summary = "Save a new product",
            description = "Creates and saves a new product in the system. Validates the provided product details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product saved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation errors occurred",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Product already exists",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Related object not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<ProductReadOnlyDTO> saveProduct(
        @Valid @RequestBody ProductInsertDTO productInsertDTO,
        BindingResult bindingResult)throws ValidationException, AppObjectAlreadyExists, AppObjectNotFoundException, AppGenericException{
        // Check if there are validation errors
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        ProductReadOnlyDTO productReadOnlyDTO = productService.saveProduct(productInsertDTO);
        return new ResponseEntity<>(productReadOnlyDTO, HttpStatus.OK);

    }
    

}
