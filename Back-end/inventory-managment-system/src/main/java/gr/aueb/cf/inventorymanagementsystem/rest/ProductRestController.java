package gr.aueb.cf.inventorymanagementsystem.rest;

import gr.aueb.cf.inventorymanagementsystem.core.exceptions.*;
import gr.aueb.cf.inventorymanagementsystem.core.filters.ProductFilters;
import gr.aueb.cf.inventorymanagementsystem.dto.*;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.service.ProductService;
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
    public ResponseEntity<Page<ProductReadOnlyDTO>> getPaginatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<ProductReadOnlyDTO> productsPage = productService.getPaginatedProducts(page, size);
        return new ResponseEntity<>(productsPage, HttpStatus.OK);
    }

    @PostMapping("/products/all")
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
    public ResponseEntity<List<ProductReadOnlyDTO>> getAllProducts() {
        List<ProductReadOnlyDTO> allProducts = productService.getAllProducts();
        return ResponseEntity.ok(allProducts);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ProductReadOnlyDTO> deleteProduct(@PathVariable Long productId) throws AppObjectNotFoundException {
        ProductReadOnlyDTO deletedProduct = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductReadOnlyDTO> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateDTO productUpdateDTO
    ) throws AppGenericException, AppServerException {
        productUpdateDTO.setId(productId);

        ProductReadOnlyDTO updatedProduct = productService.updateProduct(productUpdateDTO);

        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductReadOnlyDTO> getProduct(
            @PathVariable Long productId) throws AppObjectNotFoundException {

        ProductReadOnlyDTO product = productService.getProduct(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping(value = "/products/save")
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
