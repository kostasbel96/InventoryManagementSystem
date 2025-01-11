package gr.aueb.cf.inventorymanagementsystem.service;


import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppGenericException;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppServerException;
import gr.aueb.cf.inventorymanagementsystem.core.filters.Paginated;
import gr.aueb.cf.inventorymanagementsystem.core.filters.ProductFilters;
import gr.aueb.cf.inventorymanagementsystem.core.specifications.ProductSpecification;
import gr.aueb.cf.inventorymanagementsystem.dto.*;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.model.*;
import gr.aueb.cf.inventorymanagementsystem.repository.CategoryRepository;
import gr.aueb.cf.inventorymanagementsystem.repository.ProductRepository;
import gr.aueb.cf.inventorymanagementsystem.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;
    private final OrderService orderService;
    private final Mapper mapper;

    @Transactional
    public ProductReadOnlyDTO saveProduct(ProductInsertDTO productInsertDTO)
            throws AppObjectAlreadyExists, AppObjectNotFoundException, AppGenericException {

        //Check if product name already exists
        if (productRepository.findByName(productInsertDTO.getName()).isPresent()) {
            throw new AppObjectAlreadyExists("Name", "Product with name " + productInsertDTO.getName() + " already exists.");
        }

        if (productInsertDTO.getPrice() <= 0) {
            throw new AppGenericException("Product", "Price must be greater than zero.");
        }

        Supplier supplier = supplierRepository.findByName(productInsertDTO.getSupplier().getName())
                .orElseThrow(() -> new AppObjectNotFoundException("Supplier","Supplier not found"));

        Category category = categoryRepository.findByName(productInsertDTO.getCategory().getName())
                .orElseThrow(() -> new AppObjectNotFoundException("Category", "Category not found"));

        Product product = mapper.mapToProductEntity(productInsertDTO);
        product.setCategory(category);
        product.setSupplier(supplier);

        // Save the product (cascades to Supplier and Category)
        Product savedProduct = productRepository.save(product);

        // Return a ProductReadOnlyDTO after saving
        return mapper.mapToProductReadOnlyDTO(savedProduct);
    }

    @Transactional
    public Paginated<ProductReadOnlyDTO> getProductsFilteredPaginated(ProductFilters filters) {
        var filtered = productRepository.findAll(getSpecsFromFilters(filters), filters.getPageable());
        return new Paginated<>(filtered.map(mapper::mapToProductReadOnlyDTO));
    }

    @Transactional
    public Page<ProductReadOnlyDTO> getProductsFiltered(ProductFilters filters, int page, int size) {
        Specification<Product> spec = Specification.where(null);

        // Προσθήκη φίλτρου για το όνομα προϊόντος
        if (filters.getName() != null && !filters.getName().isBlank()) {
            spec = spec.and(ProductSpecification.productNameIs(filters.getName()));
        }

        Pageable pageable = PageRequest.of(page, size);

        // Επιστροφή φιλτραρισμένων προϊόντων με σελιδοποίηση
        Page<Product> paginatedProducts = productRepository.findAll(spec, pageable);
        return paginatedProducts.map(mapper::mapToProductReadOnlyDTO);
    }

    @Transactional
    public Page<ProductReadOnlyDTO> getPaginatedProducts(int page, int size) {
        String defaultSort = "name";
        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());
        return productRepository.findAll(pageable).map(mapper::mapToProductReadOnlyDTO);
    }

    @Transactional
    public ProductReadOnlyDTO deleteProduct(Long productId) throws AppObjectNotFoundException {
        // Εύρεση του προϊόντος
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppObjectNotFoundException("Product", "Product with id: " + productId + " not found"));

        for (OrderItem item : product.getAllOrderItems()) {
            orderService.deleteOrder(item.getOrder().getId());
        }

        product.clearOrderItems();

        // Διαγραφή του προϊόντος
        productRepository.delete(product);

        return mapper.mapToProductReadOnlyDTO(product);
    }

    @Transactional
    public ProductReadOnlyDTO getProduct(Long id) throws AppObjectNotFoundException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("Product", "Product with id: " + id + " not found"));

        return mapper.mapToProductReadOnlyDTO(product);
    }

    @Transactional
    public ProductReadOnlyDTO updateProduct(ProductUpdateDTO productUpdateDTO)
            throws AppObjectNotFoundException, AppServerException {

        // Εύρεση του υπάρχοντος προϊόντος
        Product existingProduct = productRepository.findById(productUpdateDTO.getId())
                .orElseThrow(() -> new AppObjectNotFoundException("Product",
                        "Product with id: " + productUpdateDTO.getId() + " not found"));

        // Ενημέρωση του Supplier
        if (productUpdateDTO.getSupplier() != null) {
            existingProduct.setSupplier(supplierRepository.findByPhoneNumber(productUpdateDTO.getSupplier().getPhoneNumber())
                    .orElseThrow(() -> new AppObjectNotFoundException("Supplier",
                            "Supplier with phoneNumber: " + productUpdateDTO.getSupplier().getPhoneNumber() + " not found")));
        }

        // Ενημέρωση του κατηγορίας
        if (productUpdateDTO.getCategory() != null) {
            existingProduct.setCategory(categoryRepository.findById(productUpdateDTO.getCategory().getId())
                    .orElseThrow(() -> new AppObjectNotFoundException("Category",
                            "Category with id: " + productUpdateDTO.getCategory().getId() + " not found")));
        }
//        existingProduct.clearOrderItems();
        existingProduct.setName(productUpdateDTO.getName());
        existingProduct.setDescription(productUpdateDTO.getDescription());
        existingProduct.setPrice(productUpdateDTO.getPrice());
        existingProduct.setQuantity(productUpdateDTO.getQuantity());

        
        // Αποθήκευση της ενημερωμένης παραγγελίας
        Product updatedProduct = productRepository.save(existingProduct);

        // Επιστροφή του ενημερωμένου Order ως DTO
        return mapper.mapToProductReadOnlyDTO(updatedProduct);

    }

    @Transactional
    public List<ProductReadOnlyDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(mapper::mapToProductReadOnlyDTO)
                .toList();
    }

    private Specification<Product> getSpecsFromFilters(ProductFilters filters) {
        return Specification
                .where(ProductSpecification.trStringFieldLike("name", filters.getName()))
                .and(ProductSpecification.trSupplierNameIs(filters.getSupplierName()))
                .and(ProductSpecification.trCategoryNameIs(filters.getCategoryName()));
    }

}
