package com.enterprise.gamma.service;

import com.enterprise.gamma.dto.InventoryDto;
import com.enterprise.gamma.entity.Inventory;
import com.enterprise.gamma.repository.InventoryRepository;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryDto> getAllInventory() {
        log.info("Fetching all inventory items");
        return inventoryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventoryDto getInventoryByProductId(String productId) {
        log.info("Fetching inventory for product: {}", productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
        return toDto(inventory);
    }

    @Transactional
    public InventoryDto addInventory(InventoryDto inventoryDto) {
        log.info("Adding inventory for product: {}", inventoryDto.getProductId());

        if (inventoryRepository.existsByProductId(inventoryDto.getProductId())) {
            throw new BusinessException("Inventory already exists for product: " + inventoryDto.getProductId(), "INVENTORY_EXISTS");
        }

        Inventory inventory = toEntity(inventoryDto);
        Inventory savedInventory = inventoryRepository.save(inventory);

        log.info("Added inventory with id: {}", savedInventory.getId());
        return toDto(savedInventory);
    }

    @Transactional
    public InventoryDto updateQuantity(String productId, Integer quantity) {
        log.info("Updating quantity for product: {} to {}", productId, quantity);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));

        if (quantity < 0) {
            throw new BusinessException("Quantity cannot be negative", "INVALID_QUANTITY");
        }

        inventory.setQuantity(quantity);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        log.info("Updated quantity for product: {}", productId);
        return toDto(updatedInventory);
    }

    @Transactional
    public void deleteInventory(String productId) {
        log.info("Deleting inventory for product: {}", productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));

        inventoryRepository.delete(inventory);
        log.info("Deleted inventory for product: {}", productId);
    }

    private InventoryDto toDto(Inventory inventory) {
        return InventoryDto.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .productName(inventory.getProductName())
                .quantity(inventory.getQuantity())
                .unitPrice(inventory.getUnitPrice())
                .warehouse(inventory.getWarehouse())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    private Inventory toEntity(InventoryDto dto) {
        return Inventory.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .warehouse(dto.getWarehouse())
                .build();
    }
}
