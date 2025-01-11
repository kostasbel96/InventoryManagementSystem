package gr.aueb.cf.inventorymanagementsystem.rest;

import gr.aueb.cf.inventorymanagementsystem.core.exceptions.*;
import gr.aueb.cf.inventorymanagementsystem.core.filters.OrderFilters;
import gr.aueb.cf.inventorymanagementsystem.core.filters.ProductFilters;
import gr.aueb.cf.inventorymanagementsystem.dto.*;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.service.OrderService;
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
public class OrderRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRestController.class);
    private final OrderService orderService;
    private final Mapper mapper;

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderReadOnlyDTO>> getPaginatedOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<OrderReadOnlyDTO> ordersPage = orderService.getPaginatedOrders(page, size);
        return new ResponseEntity<>(ordersPage, HttpStatus.OK);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderReadOnlyDTO> getOrder(
            @PathVariable Long orderId) throws AppObjectNotFoundException {

        OrderReadOnlyDTO order = orderService.getOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    /**
     * Ενημέρωση μιας παραγγελίας
     */
    @PutMapping("/orders/{orderId}")
    public ResponseEntity<OrderReadOnlyDTO> updateOrder(
            @PathVariable Long orderId,
            @RequestBody OrderUpdateDTO orderUpdateDTO
    ) throws AppGenericException, AppServerException {
        // Ορισμός του ID της παραγγελίας στο DTO
        orderUpdateDTO.setId(orderId);

        // Κλήση της μεθόδου updateOrder στο Service
        OrderReadOnlyDTO updatedOrder = orderService.updateOrder(orderUpdateDTO);

        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @PostMapping("/orders/all")
    public ResponseEntity<Page<OrderReadOnlyDTO>> getOrders(@Nullable @RequestBody OrderFilters filters,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "5") int size,
                                                                Principal principal)
            throws AppObjectNotFoundException, AppObjectNotAuthorizedException {
        try {
            if (filters == null) filters = OrderFilters.builder().build();
            Page<OrderReadOnlyDTO> paginatedOrders = orderService.getOrdersFiltered(filters, page, size);
            return ResponseEntity.ok(paginatedOrders);
        } catch (Exception e) {
            LOGGER.error("ERROR: Could not get orders.", e);
            throw e;
        }
    }

    @PostMapping(value = "/orders/save")
    public ResponseEntity<OrderReadOnlyDTO> saveOrder(
            @Valid @RequestBody OrderInsertDTO orderInsertDTO,
            BindingResult bindingResult)
            throws ValidationException, AppObjectAlreadyExists,
            AppObjectNotFoundException, AppGenericException {

        // Check if there are validation errors
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        OrderReadOnlyDTO orderReadOnlyDTO = orderService.saveOrder(orderInsertDTO);
        return new ResponseEntity<>(orderReadOnlyDTO, HttpStatus.OK);

    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<OrderReadOnlyDTO> deleteOrder(@PathVariable Long orderId) throws AppObjectNotFoundException {
        // Κλήση της μεθόδου deleteOrder στο Service
        OrderReadOnlyDTO deletedOrder = orderService.deleteOrder(orderId);
        return new ResponseEntity<>(deletedOrder, HttpStatus.OK);
    }


}
