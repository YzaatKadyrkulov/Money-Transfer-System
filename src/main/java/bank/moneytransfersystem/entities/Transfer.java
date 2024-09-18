package bank.moneytransfersystem.entities;

import bank.moneytransfersystem.enums.Currency;
import bank.moneytransfersystem.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "transfers")
@Data
@SequenceGenerator(name = "base_id_gen", sequenceName = "transfer_seq", allocationSize = 1)
public class Transfer extends BaseEntity {
    private double amount;
    @Column(unique = true)
    private String code;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String senderFullName;
    private String receiverFullName;
    private String senderPhoneNumber;
    private String receiverPhoneNumber;
    private String comment;
    private LocalDateTime createdAt;

    @ManyToOne(cascade = {MERGE, DETACH})
    @JoinColumn(name = "from_cash_id")
    private Cash fromCash;

    @ManyToOne(cascade = {MERGE, DETACH})
    @JoinColumn(name = "to_cash_id")
    private Cash toCash;

    @Override
    public String toString() {
        return "Transfer{id=" + getId() + ", amount=" + amount + ", fromCashId=" + (fromCash != null ? fromCash.getId() : "null") + ", toCashId=" + (toCash != null ? toCash.getId() : "null") + "}";
    }
}