package com.dharshi.purely.modals;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItem {

    private Product product;

    private int quantity;

    private double totalPrice;

}
