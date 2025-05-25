package com.trafigura.equityapi.model;

import com.trafigura.equityapi.dto.TransactionDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @NotNull(message = "Trade ID is mandatory")
    @Column(name = "trade_id")
    private Integer tradeId;

    @NotNull(message = "Version is mandatory")
    @Column(name = "version")
    private Integer version;

    @NotBlank(message = "Security Code is mandatory")
    @Column(name = "security_code")
    private String securityCode;

    @NotNull(message = "Quantity is mandatory")
    @Positive(message = "Quantity must be positive")
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull(message = "Action is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private ActionType action;

    @NotNull(message = "Direction is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    private Direction direction;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ActionType { INSERT, UPDATE, CANCEL }
    public enum Direction { Buy, Sell }

    public TransactionDto toTransactionDto() {
        return TransactionDto.builder().action(action).direction(direction).securityCode(securityCode).version(version).quantity(quantity).tradeId(tradeId).build();
    }

}
