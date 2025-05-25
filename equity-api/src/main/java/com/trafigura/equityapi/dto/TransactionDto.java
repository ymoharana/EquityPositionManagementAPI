package com.trafigura.equityapi.dto;

import com.trafigura.equityapi.model.Transaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    @NotNull
    private Integer tradeId;

    @NotNull
    private Integer version;

    @NotBlank
    private String securityCode;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    private Transaction.ActionType action;

    @NotNull
    private Transaction.Direction direction;

    public Transaction toEntity() {
        return Transaction.builder()
                .tradeId(tradeId)
                .version(version)
                .securityCode(securityCode)
                .quantity(quantity)
                .action(action)
                .direction(direction)
                .build();
    }
}