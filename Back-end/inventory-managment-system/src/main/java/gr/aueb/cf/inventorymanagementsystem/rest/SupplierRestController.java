package gr.aueb.cf.inventorymanagementsystem.rest;

import gr.aueb.cf.inventorymanagementsystem.core.exceptions.*;
import gr.aueb.cf.inventorymanagementsystem.core.filters.ProductFilters;
import gr.aueb.cf.inventorymanagementsystem.core.filters.SupplierFilters;
import gr.aueb.cf.inventorymanagementsystem.dto.*;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.service.SupplierService;
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
    private final Mapper mapper;

    @GetMapping("/suppliers")
    public ResponseEntity<Page<SupplierReadOnlyDTO>> getPaginatedSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<SupplierReadOnlyDTO> suppliersPage = supplierService.getPaginatedSuppliers(page, size);
        return new ResponseEntity<>(suppliersPage, HttpStatus.OK);
    }

    @GetMapping("/suppliers/{supplierId}")
    public ResponseEntity<SupplierReadOnlyDTO> getSupplier(
            @PathVariable Long supplierId) throws AppObjectNotFoundException {

        SupplierReadOnlyDTO supplier = supplierService.getSupplier(supplierId);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    @PutMapping("/suppliers/{supplierId}")
    public ResponseEntity<SupplierReadOnlyDTO> updateSupplier(
            @PathVariable Long supplierId,
            @RequestBody SupplierUpdateDTO supplierUpdateDTO
    ) throws AppGenericException, AppServerException {
        // Ορισμός του ID της παραγγελίας στο DTO
        supplierUpdateDTO.setId(supplierId);

        // Κλήση της μεθόδου updateOrder στο Service
        SupplierReadOnlyDTO updatedSupplier = supplierService.updateSupplier(supplierUpdateDTO);

        return new ResponseEntity<>(updatedSupplier, HttpStatus.OK);
    }

    @DeleteMapping("/suppliers/{supplierId}")
    public ResponseEntity<SupplierReadOnlyDTO> deleteSupplier(@PathVariable Long supplierId)
            throws AppObjectNotFoundException, AppServerException, AppGenericException {
        // Κλήση της μεθόδου deleteOrder στο Service
        SupplierReadOnlyDTO deletedSupplier = supplierService.deleteSupplier(supplierId);
        return new ResponseEntity<>(deletedSupplier, HttpStatus.OK);
    }

    @GetMapping("/suppliers/getAll")
    public ResponseEntity<List<SupplierReadOnlyDTO>> getAllSuppliers() {
        List<SupplierReadOnlyDTO> allSuppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(allSuppliers);
    }

    @PostMapping("/suppliers/all")
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
