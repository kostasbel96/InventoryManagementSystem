package gr.aueb.cf.inventorymanagementsystem.mapper;

import gr.aueb.cf.inventorymanagementsystem.dto.*;
import gr.aueb.cf.inventorymanagementsystem.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final PasswordEncoder passwordEncoder;

    public ProductReadOnlyDTO mapToProductReadOnlyDTO(Product product){
        ProductReadOnlyDTO dto = new ProductReadOnlyDTO();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setSupplier(mapToSupplierReadOnlyDTO(product.getSupplier()));
        dto.setCategory(mapToCategoryReadOnlyDTO(product.getCategory()));

        return dto;
    }

    public ProductReadOnlyDTO2 mapToProductReadOnlyDTO2(Product product){
        return new ProductReadOnlyDTO2(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getSupplier().getName(),
                product.getCategory().getName()
        );
    }

    public CategoryReadOnlyDTO mapToCategoryReadOnlyDTO(Category category) {
        if(category == null)
            return null;

        return new CategoryReadOnlyDTO(
                category.getId(),
                category.getName()
        );
    }

    public OrderItemReadOnlyDTO mapToOrderItemReadOnlyDTO(OrderItem orderItem) {
        OrderItemReadOnlyDTO itemDTO = new OrderItemReadOnlyDTO();
        itemDTO.setId(orderItem.getId());
        itemDTO.setQuantity(orderItem.getQuantity());
        itemDTO.setProduct(mapToProductReadOnlyDTO(orderItem.getProduct()));
        // Μην περιλαμβάνετε την παραγγελία για να αποφύγετε την κυκλική εξάρτηση
        return itemDTO;
    }

    public OrderReadOnlyDTO mapToOrderReadOnlyDTO(Order order) {
        OrderReadOnlyDTO orderDTO = new OrderReadOnlyDTO();
        orderDTO.setId(order.getId());
        orderDTO.setSupplier(mapToSupplierReadOnlyDTO(order.getSupplier()));
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setOrderItems(order.getAllOrderItems().
                stream().
                map(this::mapToOrderItemReadOnlyDTO)
                .toList());
        return orderDTO;
    }

    public SupplierReadOnlyDTO mapToSupplierReadOnlyDTO(Supplier supplier) {
        if(supplier == null)
            return null;

        return new SupplierReadOnlyDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getEmail(),
                supplier.getPhoneNumber()
        );
    }

    public Product mapToProductEntity(ProductInsertDTO productInsertDTO) {
        if(productInsertDTO == null)
            return null;

        return new Product(
                null,
                productInsertDTO.getName(),
                productInsertDTO.getDescription(),
                productInsertDTO.getPrice(),
                productInsertDTO.getQuantity(),
                mapToSupplierEntity(productInsertDTO.getSupplier()),
                mapToCategoryEntity(productInsertDTO.getCategory()),
                null
        );
    }

    public OrderItem mapToOrderItemEntity(OrderItemInsertDTO orderItemInsertDTO) {
        if(orderItemInsertDTO == null)
            return null;

        return new OrderItem(
                null,
                null,
                mapToProductEntity(orderItemInsertDTO.getProduct()),
                orderItemInsertDTO.getQuantity()
        );
    }

    public Order mapToOrderEntity(OrderInsertDTO order) {
        if(order == null)
            return null;

        return new Order(
                null,
                mapToSupplierEntity(order.getSupplier()),
                null,
                null
        );
    }

    public Category mapToCategoryEntity(CategoryInsertDTO category) {
        if(category == null)
            return null;

        return new Category(
                null,
                category.getName()
        );
    }

    public Supplier mapToSupplierEntity(SupplierInsertDTO supplier) {
        if(supplier==null)
            return null;

        return new Supplier(
                null,
                supplier.getName(),
                supplier.getEmail(),
                supplier.getPhoneNumber()
        );
    }

    public Category mapToCategoryEntity(CategoryUpdateDTO categoryUpdateDTO) {
        if(categoryUpdateDTO == null)
            return null;

        return new Category(
                categoryUpdateDTO.getId(),
                categoryUpdateDTO.getName()
        );
    }

    public Supplier mapToSupplierEntity(SupplierUpdateDTO supplierUpdateDTO) {
        if(supplierUpdateDTO==null)
            return null;

        return new Supplier(
                supplierUpdateDTO.getId(),
                supplierUpdateDTO.getName(),
                supplierUpdateDTO.getEmail(),
                supplierUpdateDTO.getPhoneNumber()
        );
    }

    public Product mapToProductEntity(ProductUpdateDTO productUpdateDTO){
        if(productUpdateDTO == null)
            return null;

        return new Product(
                productUpdateDTO.getId(),
                productUpdateDTO.getName(),
                productUpdateDTO.getDescription(),
                productUpdateDTO.getPrice(),
                productUpdateDTO.getQuantity(),
                mapToSupplierEntity(productUpdateDTO.getSupplier()),
                mapToCategoryEntity(productUpdateDTO.getCategory()),
                null
        );
    }

    public OrderItem mapToOrderItemEntity(OrderItemUpdateDTO orderItemUpdateDTO) {
        if(orderItemUpdateDTO == null)
            return null;

        return new OrderItem(
                orderItemUpdateDTO.getId(),
                null,
                mapToProductEntity(orderItemUpdateDTO.getProduct()),
                orderItemUpdateDTO.getQuantity()
        );
    }

    public User mapToUserEntity(UserInsertDTO userInsertDTO){
        User user = new User();
        user.setFirstname(userInsertDTO.getFirstname());
        user.setLastname(userInsertDTO.getLastname());
        user.setUsername(userInsertDTO.getUsername());
        user.setRole(userInsertDTO.getRole());
        user.setPassword(passwordEncoder.encode(userInsertDTO.getPassword()));

        return user;
    }

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user){
        UserReadOnlyDTO userReadOnlyDTO = new UserReadOnlyDTO();
        userReadOnlyDTO.setUsername(user.getUsername());
        userReadOnlyDTO.setId(user.getId());
        userReadOnlyDTO.setFirstname(user.getFirstname());
        userReadOnlyDTO.setLastname(user.getLastname());
        userReadOnlyDTO.setRole(user.getRole());

        return userReadOnlyDTO;
    }

}
