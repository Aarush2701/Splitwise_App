package splitwise.splitwise.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="expensesplits")
@IdClass(ExpenseSplitId.class)
public class ExpenseSplit {

    @Id
    private Long expenseid;

    @Id
    private Long userid;

    private Double amount;

}
