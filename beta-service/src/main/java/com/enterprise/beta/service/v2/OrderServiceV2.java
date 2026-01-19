package com.enterprise.beta.service.v2;

import com.enterprise.beta.dto.v2.OrderAnalyticsDto;
import com.enterprise.beta.dto.v2.OrderDtoV2;
import com.enterprise.beta.entity.Order;
import com.enterprise.beta.repository.OrderRepository;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceV2 {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderDtoV2> getAllOrders(String status) {
        log.info("[V2] Fetching orders with status filter: {}", status);

        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByStatus(status);
        } else {
            orders = orderRepository.findAll();
        }

        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDtoV2 getOrderById(Long orderId) {
        log.info("[V2] Fetching order with id: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return toDto(order);
    }

    @Transactional
    public OrderDtoV2 createOrder(OrderDtoV2 orderDto) {
        log.info("[V2] Creating new order with number: {}", orderDto.getOrderNumber());

        if (orderRepository.existsByOrderNumber(orderDto.getOrderNumber())) {
            throw new BusinessException("Order with number already exists: " + orderDto.getOrderNumber(), "ORDER_EXISTS");
        }

        Order order = toEntity(orderDto);
        order.setStatus("PENDING");
        Order savedOrder = orderRepository.save(order);

        log.info("[V2] Created order with id: {}", savedOrder.getId());
        return toDto(savedOrder);
    }

    @Transactional
    public OrderDtoV2 updateOrder(Long orderId, OrderDtoV2 orderDto) {
        log.info("[V2] Updating order with id: {}", orderId);

        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        existingOrder.setCustomerId(orderDto.getCustomerId());
        existingOrder.setTotalAmount(orderDto.getTotalAmount());
        existingOrder.setCustomerEmail(orderDto.getCustomerEmail());
        existingOrder.setTaxAmount(orderDto.getTaxAmount());
        existingOrder.setShippingCost(orderDto.getShippingCost());
        existingOrder.setPaymentMethod(orderDto.getPaymentMethod());
        existingOrder.setShippingAddress(orderDto.getShippingAddress());
        if (orderDto.getStatus() != null) {
            existingOrder.setStatus(orderDto.getStatus());
        }

        Order updatedOrder = orderRepository.save(existingOrder);
        log.info("[V2] Updated order with id: {}", updatedOrder.getId());
        return toDto(updatedOrder);
    }

    @Transactional
    public OrderDtoV2 updateOrderStatus(Long orderId, String status) {
        log.info("[V2] Updating status for order id: {} to {}", orderId, status);

        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        existingOrder.setStatus(status);
        Order updatedOrder = orderRepository.save(existingOrder);

        log.info("[V2] Updated status for order id: {}", updatedOrder.getId());
        return toDto(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        log.info("[V2] Deleting order with id: {}", orderId);

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }

        orderRepository.deleteById(orderId);
        log.info("[V2] Deleted order with id: {}", orderId);
    }

    @Transactional(readOnly = true)
    public OrderAnalyticsDto getAnalytics() {
        log.info("[V2] Generating order analytics");

        List<Order> allOrders = orderRepository.findAll();

        long totalOrders = allOrders.size();

        BigDecimal totalRevenue = allOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageOrderValue = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Map<String, Long> ordersByStatus = allOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));

        Map<String, Long> ordersByPaymentMethod = allOrders.stream()
                .filter(o -> o.getPaymentMethod() != null)
                .collect(Collectors.groupingBy(Order::getPaymentMethod, Collectors.counting()));

        return OrderAnalyticsDto.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .ordersByStatus(ordersByStatus)
                .ordersByPaymentMethod(ordersByPaymentMethod)
                .build();
    }

    private OrderDtoV2 toDto(Order order) {
        return OrderDtoV2.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .customerEmail(order.getCustomerEmail())
                .totalAmount(order.getTotalAmount())
                .taxAmount(order.getTaxAmount())
                .shippingCost(order.getShippingCost())
                .paymentMethod(order.getPaymentMethod())
                .shippingAddress(order.getShippingAddress())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private Order toEntity(OrderDtoV2 dto) {
        return Order.builder()
                .orderNumber(dto.getOrderNumber())
                .customerId(dto.getCustomerId())
                .customerEmail(dto.getCustomerEmail())
                .totalAmount(dto.getTotalAmount())
                .taxAmount(dto.getTaxAmount())
                .shippingCost(dto.getShippingCost())
                .paymentMethod(dto.getPaymentMethod())
                .shippingAddress(dto.getShippingAddress())
                .status(dto.getStatus())
                .build();
    }
}
