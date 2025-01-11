package gr.aueb.cf.inventorymanagementsystem.service;

import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppGenericException;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppServerException;
import gr.aueb.cf.inventorymanagementsystem.core.filters.OrderFilters;
import gr.aueb.cf.inventorymanagementsystem.core.filters.Paginated;
import gr.aueb.cf.inventorymanagementsystem.core.filters.ProductFilters;
import gr.aueb.cf.inventorymanagementsystem.core.specifications.OrderSpecification;
import gr.aueb.cf.inventorymanagementsystem.core.specifications.ProductSpecification;
import gr.aueb.cf.inventorymanagementsystem.dto.*;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.model.Order;
import gr.aueb.cf.inventorymanagementsystem.model.OrderItem;
import gr.aueb.cf.inventorymanagementsystem.model.Product;
import gr.aueb.cf.inventorymanagementsystem.repository.OrderRepository;
import gr.aueb.cf.inventorymanagementsystem.repository.ProductRepository;
import gr.aueb.cf.inventorymanagementsystem.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final OrderRepository orderRepository;
    private final Mapper mapper;

    @Transactional
    public OrderReadOnlyDTO saveOrder(OrderInsertDTO orderInsertDTO)
            throws AppObjectNotFoundException, AppGenericException {

        // Χαρτογράφηση του OrderInsertDTO στην οντότητα Order
        Order order = mapper.mapToOrderEntity(orderInsertDTO);

        // Εύρεση και ρύθμιση του Supplier
        order.setSupplier(supplierRepository.findByPhoneNumber(order.getSupplier().getPhoneNumber())
                .orElseThrow(() -> new AppObjectNotFoundException("Supplier",
                        "Supplier with phoneNumber: " + order.getSupplier().getPhoneNumber() + " not found")));

        // Έλεγχος ότι υπάρχουν OrderItems
        if (orderInsertDTO.getOrderItems() == null || orderInsertDTO.getOrderItems().isEmpty()) {
            throw new AppGenericException("OrderItem", "The order must contain at least one product.");
        }

        // Δημιουργία και σύνδεση OrderItems
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemInsertDTO orderItemInsertDTO : orderInsertDTO.getOrderItems()) {
            Product product = productRepository.findByName(orderItemInsertDTO.getProduct().getName())
                    .orElseThrow(() -> new AppObjectNotFoundException("Product",
                            "Product with name: " + orderItemInsertDTO.getProduct().getName() + " not found"));
            OrderItem orderItem = mapper.mapToOrderItemEntity(orderItemInsertDTO);
            orderItem.setProduct(product);
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }

        // Προσθήκη OrderItems και ρύθμιση ημερομηνίας
        orderItems.forEach(orderItem -> order.addOrderItem(orderItem.getProduct(), orderItem.getQuantity()));
        order.setOrderDate(LocalDate.now());

        // Αποθήκευση και επιστροφή του Order
        return mapper.mapToOrderReadOnlyDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderReadOnlyDTO deleteOrder(Long orderId) throws AppObjectNotFoundException {
        // Εύρεση της παραγγελίας
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppObjectNotFoundException("Order", "Order with id: " + orderId + " not found"));

        // Διαγραφή της παραγγελίας
        orderRepository.delete(order);

        return mapper.mapToOrderReadOnlyDTO(order);
    }

    @Transactional
    public OrderReadOnlyDTO getOrder(Long id) throws AppObjectNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("Order", "Order with id: " + id + " not found"));

        return mapper.mapToOrderReadOnlyDTO(order);
    }

    @Transactional
    public OrderReadOnlyDTO updateOrder(OrderUpdateDTO orderUpdateDTO)
            throws AppObjectNotFoundException, AppServerException{

        // Εύρεση της υπάρχουσας παραγγελίας
        Order existingOrder = orderRepository.findById(orderUpdateDTO.getId())
                .orElseThrow(() -> new AppObjectNotFoundException("Order",
                        "Order with id: " + orderUpdateDTO.getId() + " not found"));

        // Ενημέρωση του Supplier
        if (orderUpdateDTO.getSupplier() != null) {
            existingOrder.setSupplier(supplierRepository.findByPhoneNumber(orderUpdateDTO.getSupplier().getPhoneNumber())
                    .orElseThrow(() -> new AppObjectNotFoundException("Supplier",
                            "Supplier with phoneNumber: " + orderUpdateDTO.getSupplier().getPhoneNumber() + " not found")));
        }

        // Ενημέρωση των OrderItems
        if (orderUpdateDTO.getOrderItems() != null && !orderUpdateDTO.getOrderItems().isEmpty()) {
            List<OrderItem> updatedOrderItems = new ArrayList<>();
            for (OrderItemUpdateDTO orderItemUpdateDTO : orderUpdateDTO.getOrderItems()) {
                Product product = productRepository.findByName(orderItemUpdateDTO.getProduct().getName())
                        .orElseThrow(() -> new AppObjectNotFoundException("Product",
                                "Product with name: " + orderItemUpdateDTO.getProduct().getName() + " not found"));

                OrderItem orderItem = mapper.mapToOrderItemEntity(orderItemUpdateDTO);
                orderItem.setProduct(product);
                orderItem.setOrder(existingOrder);
                updatedOrderItems.add(orderItem);
            }

            // Αφαίρεση παλιών OrderItems και προσθήκη νέων
            existingOrder.clearOrderItems(); // Διαγράφει τα παλιά OrderItems
            updatedOrderItems.forEach(orderItem -> existingOrder.addOrderItem(orderItem.getProduct(), orderItem.getQuantity()));
        } else {
            throw new AppServerException("OrderItem", "The order must contain at least one product.");
        }

        // Ενημέρωση της ημερομηνίας παραγγελίας
        existingOrder.setOrderDate(LocalDate.now());

        // Αποθήκευση της ενημερωμένης παραγγελίας
        Order updatedOrder = orderRepository.save(existingOrder);

        // Επιστροφή του ενημερωμένου Order ως DTO
        return mapper.mapToOrderReadOnlyDTO(updatedOrder);

    }

    @Transactional
    public Paginated<OrderReadOnlyDTO> getOrdersFilteredPaginated(OrderFilters filters) {
        // Δημιουργία των Specifications από τα φίλτρα
        Specification<Order> spec = getSpecsFromFilters(filters);

        // Αναζήτηση με Specification και Pageable
        var filtered = orderRepository.findAll(spec, filters.getPageable());

        // Μετατροπή των αποτελεσμάτων σε DTO και επιστροφή με Paginated
        return new Paginated<>(filtered.map(mapper::mapToOrderReadOnlyDTO));
    }

    @Transactional
    public Page<OrderReadOnlyDTO> getOrdersFiltered(OrderFilters filters, int page, int size) {
        Specification<Order> spec = Specification.where(null);

        // Προσθήκη φίλτρου για το όνομα προϊόντος
        if (filters.getName() != null && !filters.getName().isBlank()) {
            spec = spec.and(OrderSpecification.trSupplierNameIs(filters.getName()));
        }

        Pageable pageable = PageRequest.of(page, size);

        // Επιστροφή φιλτραρισμένων προϊόντων με σελιδοποίηση
        Page<Order> paginatedOrders = orderRepository.findAll(spec, pageable);
        return paginatedOrders.map(mapper::mapToOrderReadOnlyDTO);
    }

    @Transactional
    public Page<OrderReadOnlyDTO> getPaginatedOrders(int page, int size) {
        String defaultSort = "orderDate";
        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());
        return orderRepository.findAll(pageable).map(mapper::mapToOrderReadOnlyDTO);
    }

    private Specification<Order> getSpecsFromFilters(OrderFilters filters) {
        return Specification
                .where(OrderSpecification.trStringFieldLike("name", filters.getName()))
                .and(OrderSpecification.trSupplierNameIs(filters.getName()));
    }

}

