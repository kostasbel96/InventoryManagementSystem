package gr.aueb.cf.inventorymanagementsystem.service;

import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppGenericException;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppServerException;
import gr.aueb.cf.inventorymanagementsystem.core.filters.CategoryFilters;
import gr.aueb.cf.inventorymanagementsystem.core.filters.Paginated;
import gr.aueb.cf.inventorymanagementsystem.core.specifications.CategorySpecification;
import gr.aueb.cf.inventorymanagementsystem.dto.*;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.model.Category;
import gr.aueb.cf.inventorymanagementsystem.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
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
public class CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
    private final Mapper mapper;


    /**
     * Saves a new category into the repository.
     *
     * <p>This method checks if a category with the same name already exists in the repository.
     * If it exists, an {@link AppObjectAlreadyExists} exception is thrown. Otherwise, the new
     * category is mapped, saved, and returned as a {@link CategoryReadOnlyDTO}.
     * </p>
     *
     * @param categoryInsertDTO the DTO containing the details of the category to be created.
     * @return a {@link CategoryReadOnlyDTO} representing the saved category.
     * @throws AppObjectAlreadyExists if a category with the specified name already exists.
     */
    @Transactional(rollbackFor = Exception.class)
    public CategoryReadOnlyDTO saveCategory(CategoryInsertDTO categoryInsertDTO)
            throws AppObjectAlreadyExists {
        try{
            if (categoryRepository.findByName(categoryInsertDTO.getName()).isPresent()){
                throw new AppObjectAlreadyExists("Category", "Category with name " + categoryInsertDTO.getName() + " already exists.");
            }
            Category category = mapper.mapToCategoryEntity(categoryInsertDTO);

            Category savedCategory = categoryRepository.save(category);

            // Return a CategoryReadOnlyDTO after saving
            return mapper.mapToCategoryReadOnlyDTO(savedCategory);
        } catch(AppObjectAlreadyExists e){
            LOGGER.warn("Category already exists: {}", categoryInsertDTO.getName(), e);
            throw e;
        }
    }

    @Transactional
    public CategoryReadOnlyDTO updateCategory(CategoryUpdateDTO categoryUpdateDTO)
            throws AppObjectNotFoundException, AppServerException {

        // Εύρεση της υπάρχουσας παραγγελίας
        Category existingCategory = categoryRepository.findById(categoryUpdateDTO.getId())
                .orElseThrow(() -> new AppObjectNotFoundException("Category",
                        "Category with id: " + categoryUpdateDTO.getId() + " not found"));

        // Ενημέρωση της ημερομηνίας παραγγελίας
        existingCategory.setName(categoryUpdateDTO.getName());

        // Αποθήκευση της ενημερωμένης παραγγελίας
        Category updatedCategory = categoryRepository.save(existingCategory);

        // Επιστροφή του ενημερωμένου Order ως DTO
        return mapper.mapToCategoryReadOnlyDTO(updatedCategory);

    }

    /**
     * Deletes an existing category by its ID.
     *
     * <p>This method removes a category from the database if it exists. If the category
     * is associated with products or any other entities, it throws an exception to prevent
     * the deletion. In case of unexpected errors, a generic exception is thrown.</p>
     *
     * @param categoryId the ID of the category to be deleted
     * @return the deleted category as a read-only DTO
     * @throws AppObjectNotFoundException if the category with the given ID does not exist
     * @throws AppGenericException if the category is associated with other entities or an unexpected error occurs
     */
    public CategoryReadOnlyDTO deleteCategory(Long categoryId) throws AppGenericException {

        LOGGER.info("Starting deleteCategory for ID: {}", categoryId);

        // Εύρεση της κατηγορίας
        LOGGER.debug("Fetching category with ID: {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppObjectNotFoundException(
                        "Category",
                        "Category with id: " + categoryId + " not found"));

        try {
            // Διαγραφή της κατηγορίας
            LOGGER.debug("Deleting category with ID: {}", categoryId);
            categoryRepository.delete(category);
            LOGGER.info("Category with ID {} deleted successfully", categoryId);

        } catch (DataIntegrityViolationException ex) {
            LOGGER.error("Cannot delete category with ID {}: it is associated with other entities.", categoryId);
            throw new AppGenericException("Category", "Cannot delete category because it is associated with products.");

        } catch (Exception ex) {
            LOGGER.error("An unexpected error occurred while deleting category with ID {}: {}", categoryId, ex.getMessage());
            throw new AppGenericException("Category", "An unexpected error occurred while deleting the category.");
        }

        // Επιστροφή δεδομένων
        LOGGER.debug("Returning deleted category data for ID: {}", categoryId);
        return mapper.mapToCategoryReadOnlyDTO(category);
    }

    /**
     * Fetches a category by its ID.
     *
     * <p>This method retrieves a category from the database based on the provided
     * ID. If the category does not exist, it throws an {@link AppObjectNotFoundException}.
     * </p>
     *
     * @param id the ID of the category to be fetched
     * @return the category as a {@link CategoryReadOnlyDTO}
     * @throws AppObjectNotFoundException if the category with the given ID does not exist
     */
    public CategoryReadOnlyDTO getCategory(Long id) throws AppObjectNotFoundException {

        LOGGER.info("Starting getCategory for ID: {}", id);

        // Εύρεση της κατηγορίας
        LOGGER.debug("Fetching category with ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException(
                        "Category",
                        "Category with id: " + id + " not found"));

        LOGGER.info("Category with ID {} fetched successfully", id);

        // Επιστροφή του DTO
        LOGGER.debug("Mapping category with ID {} to DTO", id);
        return mapper.mapToCategoryReadOnlyDTO(category);
    }


    /**
     * Fetches all categories from the database.
     *
     * <p>This method retrieves all categories and maps them to read-only DTOs.
     * </p>
     *
     * @return a list of categories as {@link CategoryReadOnlyDTO}
     */
    @Transactional
    public List<CategoryReadOnlyDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(mapper::mapToCategoryReadOnlyDTO)
                .toList();
    }

    @Transactional
    public Paginated<CategoryReadOnlyDTO> getCategoriesFilteredPaginated(CategoryFilters filters) {
        var filtered = categoryRepository.findAll(getSpecsFromFilters(filters), filters.getPageable());
        return new Paginated<>(filtered.map(mapper::mapToCategoryReadOnlyDTO));
    }

    @Transactional
    public Page<CategoryReadOnlyDTO> getCategoriesFiltered(CategoryFilters filters, int page, int size) {
        Specification<Category> spec = Specification.where(null);

        // Προσθήκη φίλτρου για το όνομα προϊόντος
        if (filters.getName() != null && !filters.getName().isBlank()) {
            spec = spec.and(CategorySpecification.categoryNameIs(filters.getName()));
        }

        Pageable pageable = PageRequest.of(page, size);

        // Επιστροφή φιλτραρισμένων προϊόντων με σελιδοποίηση
        Page<Category> paginatedCategories = categoryRepository.findAll(spec, pageable);
        return paginatedCategories.map(mapper::mapToCategoryReadOnlyDTO);
    }

    @Transactional
    public Page<CategoryReadOnlyDTO> getPaginatedCategories(int page, int size) {
        String defaultSort = "name";
        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());
        return categoryRepository.findAll(pageable).map(mapper::mapToCategoryReadOnlyDTO);
    }

    private Specification<Category> getSpecsFromFilters(CategoryFilters filters) {
        return Specification
                .where(CategorySpecification.trStringFieldLike("name", filters.getName()));
    }

}
