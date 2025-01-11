package gr.aueb.cf.inventorymanagementsystem.rest;

import gr.aueb.cf.inventorymanagementsystem.core.exceptions.*;
import gr.aueb.cf.inventorymanagementsystem.core.filters.ProductFilters;
import gr.aueb.cf.inventorymanagementsystem.core.filters.SupplierFilters;
import gr.aueb.cf.inventorymanagementsystem.dto.*;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.service.SupplierService;
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
public class SupplierRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupplierRestController.class);
    private final SupplierService supplierService;

    @GetMapping("/suppliers")
    @Operation(
            summary = "Get paginated suppliers",
            description = "Fetches a paginated list of suppliers with optional page number and page size parameters."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Suppliers retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class) // Page<SupplierReadOnlyDTO> schema
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
    public ResponseEntity<Page<SupplierReadOnlyDTO>> getPaginatedSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<SupplierReadOnlyDTO> suppliersPage = supplierService.getPaginatedSuppliers(page, size);
        return new ResponseEntity<>(suppliersPage, HttpStatus.OK);
    }

    @GetMapping("/suppliers/{supplierId}")
    @Operation(
            summary = "Get supplier by ID",
            description = "Fetches the details of a specific supplier using its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Supplier retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SupplierReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Supplier not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<SupplierReadOnlyDTO> getSupplier(
            @PathVariable Long supplierId) throws AppObjectNotFoundException {

        SupplierReadOnlyDTO supplier = supplierService.getSupplier(supplierId);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    @PutMapping("/suppliers/{supplierId}")
    @Operation(
            summary = "Update supplier by ID",
            description = "Updates the details of an existing supplier using its unique ID and the provided updated data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Supplier updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SupplierReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Supplier not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<SupplierReadOnlyDTO> updateSupplier(
            @PathVariable Long supplierId,
            @RequestBody SupplierUpdateDTO supplierUpdateDTO
    ) throws AppGenericException, AppServerException {

        supplierUpdateDTO.setId(supplierId);

        SupplierReadOnlyDTO updatedSupplier = supplierService.updateSupplier(supplierUpdateDTO);

        return new ResponseEntity<>(updatedSupplier, HttpStatus.OK);
    }

    @DeleteMapping("/suppliers/{supplierId}")
    @Operation(
            summary = "Delete supplier by ID",
            description = "Deletes an existing supplier based on its unique ID and returns the details of the deleted supplier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Supplier deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SupplierReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Supplier not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<SupplierReadOnlyDTO> deleteSupplier(@PathVariable Long supplierId)
            throws AppObjectNotFoundException, AppServerException, AppGenericException {
        // Κλήση της μεθόδου deleteOrder στο Service
        SupplierReadOnlyDTO deletedSupplier = supplierService.deleteSupplier(supplierId);
        return new ResponseEntity<>(deletedSupplier, HttpStatus.OK);
    }

    @GetMapping("/suppliers/getAll")
    @Operation(
            summary = "Get all suppliers",
            description = "Fetches a complete list of all suppliers in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Suppliers retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SupplierReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<List<SupplierReadOnlyDTO>> getAllSuppliers() {
        List<SupplierReadOnlyDTO> allSuppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(allSuppliers);
    }

    @PostMapping("/suppliers/all")
    @Operation(
            summary = "Get filtered paginated suppliers",
            description = "Fetches a paginated list of suppliers based on optional filters and pagination parameters."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Suppliers retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class) // Page<SupplierReadOnlyDTO> schema
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Suppliers not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content
            )
    })
    public ResponseEntity<Page<SupplierReadOnlyDTO>> getSuppliers(@Nullable @RequestBody SupplierFilters filters,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "5") int size,
                                                                Principal principal)
            throws AppObjectNotFoundException, AppObjectNotAuthorizedException {
        try {
            if (filters == null) filters = SupplierFilters.builder().build();
            Page<SupplierReadOnlyDTO> paginatedSuppliers = supplierService.getSuppliersFiltered(filters, page, size);
            return ResponseEntity.ok(paginatedSuppliers);
        } catch (Exception e) {
            LOGGER.error("ERROR: Could not get suppliers.", e);
            throw e;
        }
    }

    @PostMapping(value = "/suppliers/save")
    @Operation(
            summary = "Save a new supplier",
            description = "Creates and saves a new supplier in the system. Validates the provided supplier details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Supplier saved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SupplierReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation errors occurred",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Supplier already exists",
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
    public ResponseEntity<SupplierReadOnlyDTO> saveSupplier(
            @Valid @RequestBody SupplierInsertDTO supplierInsertDTO,
            BindingResult bindingResult)throws ValidationException, AppObjectAlreadyExists, AppObjectNotFoundException {
        // Check if there are validation errors
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        SupplierReadOnlyDTO supplierReadOnlyDTO = supplierService.saveSupplier(supplierInsertDTO);
        return new ResponseEntity<>(supplierReadOnlyDTO, HttpStatus.OK);

    }

}
