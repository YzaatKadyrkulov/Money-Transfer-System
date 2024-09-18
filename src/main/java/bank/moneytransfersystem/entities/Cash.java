package bank.moneytransfersystem.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.REMOVE;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cashes")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "base_id_gen", sequenceName = "cash_seq", allocationSize = 1)
public class Cash extends BaseEntity{
    private String cashName;
    private double balance;

    @OneToMany(mappedBy = "fromCash", cascade = {MERGE, REMOVE})
    private List<Transfer> outgoingTransfers;

    @OneToMany(mappedBy = "toCash", cascade = {MERGE,REMOVE})
    private List<Transfer> incomingTransfers;

    @Override
    public String toString() {
        return "Cash{id=" + getId() + ", balance=" + balance + "}";
    }
}
