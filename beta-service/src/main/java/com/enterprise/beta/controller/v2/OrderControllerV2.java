package com.enterprise.beta.controller.v2;

import com.enterprise.beta.dto.v2.OrderAnalyticsDto;
import com.enterprise.beta.dto.v2.OrderDtoV2;
import com.enterprise.beta.service.v2.OrderServiceV2;
import com.enterprise.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/orders")
@RequiredArgsConstructor
public class OrderControllerV2 {

    private final OrderServiceV2 orderService;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Beta Service V2 API is healthy"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDtoV2>>> getAllOrders(
            @RequestParam(required = false) String status) {
        List<OrderDtoV2> orders = orderService.getAllOrders(status);
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDtoV2>> getOrderById(@PathVariable Long orderId) {
        OrderDtoV2 order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDtoV2>> createOrder(@Valid @RequestBody OrderDtoV2 orderDto) {
        OrderDtoV2 createdOrder = orderService.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", createdOrder));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDtoV2>> updateOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderDtoV2 orderDto) {
        OrderDtoV2 updatedOrder = orderService.updateOrder(orderId, orderDto);
        return ResponseEntity.ok(ApiResponse.success("Order updated successfully", updatedOrder));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderDtoV2>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> statusUpdate) {
        String status = statusUpdate.get("status");
        OrderDtoV2 updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", updatedOrder));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order deleted successfully", null));
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<OrderAnalyticsDto>> getAnalytics() {
        OrderAnalyticsDto analytics = orderService.getAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Analytics retrieved successfully", analytics));
    }
}
