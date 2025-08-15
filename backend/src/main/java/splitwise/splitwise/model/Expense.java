package splitwise.splitwise.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseid;

    private Double amount;
    private String description;

    @Column(name = "expensedate", updatable = false)
    @CreationTimestamp
    private Timestamp expensedate;

    @ManyToOne
    @JoinColumn(name="userid", referencedColumnName = "userid", nullable = false)
    private User userid;

    @ManyToOne
    @JoinColumn(name="groupid", referencedColumnName = "groupid", nullable = false)
    private ExpenseGroup groupid;


}
