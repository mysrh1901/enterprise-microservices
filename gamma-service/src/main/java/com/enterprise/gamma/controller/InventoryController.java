package com.enterprise.gamma.controller;

import com.enterprise.gamma.dto.InventoryDto;
import com.enterprise.gamma.service.InventoryService;
import com.enterprise.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Gamma Service is healthy"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryDto>>> getAllInventory() {
        List<InventoryDto> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(ApiResponse.success("Inventory retrieved successfully", inventory));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<InventoryDto>> getInventoryByProductId(@PathVariable String productId) {
        InventoryDto inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Inventory retrieved successfully", inventory));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryDto>> addInventory(@Valid @RequestBody InventoryDto inventoryDto) {
        InventoryDto createdInventory = inventoryService.addInventory(inventoryDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inventory added successfully", createdInventory));
    }

    @PutMapping("/{productId}/quantity")
    public ResponseEntity<ApiResponse<InventoryDto>> updateQuantity(
            @PathVariable String productId,
            @RequestParam Integer quantity) {
        InventoryDto updatedInventory = inventoryService.updateQuantity(productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Quantity updated successfully", updatedInventory));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteInventory(@PathVariable String productId) {
        inventoryService.deleteInventory(productId);
        return ResponseEntity.ok(ApiResponse.success("Inventory deleted successfully", null));
    }
}
