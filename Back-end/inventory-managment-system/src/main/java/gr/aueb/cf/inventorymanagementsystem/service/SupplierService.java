package gr.aueb.cf.inventorymanagementsystem.service;

import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppGenericException;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppServerException;
import gr.aueb.cf.inventorymanagementsystem.core.filters.Paginated;
import gr.aueb.cf.inventorymanagementsystem.core.filters.SupplierFilters;
import gr.aueb.cf.inventorymanagementsystem.core.specifications.SupplierSpecification;
import gr.aueb.cf.inventorymanagementsystem.dto.*;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.model.Supplier;
import gr.aueb.cf.inventorymanagementsystem.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupplierService.class);
    private final SupplierRepository supplierRepository;
    private final Mapper mapper;

    @Transactional
    public SupplierReadOnlyDTO saveSupplier(SupplierInsertDTO supplierInsertDTO)
            throws AppObjectAlreadyExists, AppObjectNotFoundException {

        if (supplierRepository.findByName(supplierInsertDTO.getName()).isPresent()){
            throw new AppObjectAlreadyExists("Supplier", "Supplier with name " + supplierInsertDTO.getName() + " already exists.");
        }
        //Check if product name already exists
        if (supplierRepository.findByEmail(supplierInsertDTO.getEmail()).isPresent()) {
            throw new AppObjectAlreadyExists("Supplier", "Supplier with email " + supplierInsertDTO.getEmail() + " already exists.");
        }
        if (supplierRepository.findByPhoneNumber(supplierInsertDTO.getPhoneNumber()).isPresent()){
            throw new AppObjectAlreadyExists("Supplier", "Supplier with phone number " + supplierInsertDTO.getPhoneNumber() + " already exists.");
        }

        Supplier supplier = mapper.mapToSupplierEntity(supplierInsertDTO);

        Supplier savedSupplier = supplierRepository.save(supplier);

        // Return a SupplierReadOnlyDTO after saving
        return mapper.mapToSupplierReadOnlyDTO(savedSupplier);
    }

    @Transactional
    public SupplierReadOnlyDTO updateSupplier(SupplierUpdateDTO supplierUpdateDTO)
            throws AppObjectNotFoundException, AppServerException {

        // Εύρεση της υπάρχουσας παραγγελίας
        Supplier existingSupplier = supplierRepository.findById(supplierUpdateDTO.getId())
                .orElseThrow(() -> new AppObjectNotFoundException("Supplier",
                        "Supplier with id: " + supplierUpdateDTO.getId() + " not found"));

        // Ενημέρωση της ημερομηνίας παραγγελίας
        existingSupplier.setName(supplierUpdateDTO.getName());
        existingSupplier.setEmail(supplierUpdateDTO.getEmail());
        existingSupplier.setPhoneNumber(supplierUpdateDTO.getPhoneNumber());

        // Αποθήκευση της ενημερωμένης παραγγελίας
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);

        // Επιστροφή του ενημερωμένου Order ως DTO
        return mapper.mapToSupplierReadOnlyDTO(updatedSupplier);

    }

    @Transactional
    public SupplierReadOnlyDTO deleteSupplier(Long supplierId) throws AppGenericException {
        // Εύρεση της παραγγελίας
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new AppObjectNotFoundException("Supplier", "Supplier with id: " + supplierId + " not found"));
        try{
            supplierRepository.delete(supplier);
        }
        catch (DataIntegrityViolationException ex) {
            throw new AppGenericException("Supplier", "Cannot delete supplier because it is associated with products.");
        } catch (Exception ex) {
            throw new AppGenericException("Supplier", "An unexpected error occurred while deleting the supplier.");
        }

        // Επιστροφή δεδομένων
        return mapper.mapToSupplierReadOnlyDTO(supplier);
    }

    @Transactional
    public SupplierReadOnlyDTO getSupplier(Long id) throws AppObjectNotFoundException {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("Supplier", "Supplier with id: " + id + " not found"));

        return mapper.mapToSupplierReadOnlyDTO(supplier);
    }

    @Transactional
    public List<SupplierReadOnlyDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(mapper::mapToSupplierReadOnlyDTO)
                .toList();
    }

    @Transactional
    public Paginated<SupplierReadOnlyDTO> getSuppliersFilteredPaginated(SupplierFilters filters) {
        var filtered = supplierRepository.findAll(getSpecsFromFilters(filters), filters.getPageable());
        return new Paginated<>(filtered.map(mapper::mapToSupplierReadOnlyDTO));
    }

    @Transactional
    public Page<SupplierReadOnlyDTO> getSuppliersFiltered(SupplierFilters filters, int page, int size) {
        Specification<Supplier> spec = Specification.where(null);

        // Προσθήκη φίλτρου για το όνομα προϊόντος
        if (filters.getName() != null && !filters.getName().isBlank()) {
            spec = spec.and(SupplierSpecification.supplierNameIs(filters.getName()));
        }

        Pageable pageable = PageRequest.of(page, size);

        // Επιστροφή φιλτραρισμένων προϊόντων με σελιδοποίηση
        Page<Supplier> paginatedSuppliers = supplierRepository.findAll(spec, pageable);
        return paginatedSuppliers.map(mapper::mapToSupplierReadOnlyDTO);
    }

    @Transactional
    public Page<SupplierReadOnlyDTO> getPaginatedSuppliers(int page, int size) {
        String defaultSort = "name";
        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());
        return supplierRepository.findAll(pageable).map(mapper::mapToSupplierReadOnlyDTO);
    }

    private Specification<Supplier> getSpecsFromFilters(SupplierFilters filters) {
        return Specification
                .where(SupplierSpecification.trStringFieldLike("name", filters.getName()));
    }

}
