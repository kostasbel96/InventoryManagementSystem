package gr.aueb.cf.inventorymanagementsystem.rest;

import gr.aueb.cf.inventorymanagementsystem.core.exceptions.*;
import gr.aueb.cf.inventorymanagementsystem.core.filters.OrderFilters;
import gr.aueb.cf.inventorymanagementsystem.core.filters.ProductFilters;
import gr.aueb.cf.inventorymanagementsystem.dto.*;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.service.OrderService;
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
public class OrderRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRestController.class);
    private final OrderService orderService;
    private final Mapper mapper;

    @GetMapping("/orders")
    @Operation(
            summary = "Get paginated orders",
            description = "Fetches a paginated list of orders. Allows specifying page number and page size."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class) // Page<OrderReadOnlyDTO> schema
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content
            )
    })
    public ResponseEntity<Page<OrderReadOnlyDTO>> getPaginatedOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<OrderReadOnlyDTO> ordersPage = orderService.getPaginatedOrders(page, size);
        return new ResponseEntity<>(ordersPage, HttpStatus.OK);
    }

    @GetMapping("/orders/{orderId}")
    @Operation(
            summary = "Get order by ID",
            description = "Fetches a specific order by its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content
            )
    })
    public ResponseEntity<OrderReadOnlyDTO> getOrder(
            @PathVariable Long orderId) throws AppObjectNotFoundException {

        OrderReadOnlyDTO order = orderService.getOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("/orders/{orderId}")
    @Operation(
            summary = "Update an order",
            description = "Updates the details of an existing order using the provided order ID and data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
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
    @Operation(
            summary = "Get filtered paginated orders",
            description = "Fetches a paginated list of orders based on provided filters. Supports optional filtering and pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class) // Page<OrderReadOnlyDTO> schema
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orders not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content
            )
    })
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
    @Operation(
            summary = "Save a new order",
            description = "Creates and saves a new order in the system. Validates the provided order details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order saved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation errors occurred",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Order already exists",
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
    @Operation(
            summary = "Delete an order by ID",
            description = "Deletes an existing order based on its unique ID. Returns the details of the deleted order."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderReadOnlyDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<OrderReadOnlyDTO> deleteOrder(@PathVariable Long orderId) throws AppObjectNotFoundException {
        // Κλήση της μεθόδου deleteOrder στο Service
        OrderReadOnlyDTO deletedOrder = orderService.deleteOrder(orderId);
        return new ResponseEntity<>(deletedOrder, HttpStatus.OK);
    }


}
