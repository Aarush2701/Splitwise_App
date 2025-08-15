package splitwise.splitwise.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "groupid" , nullable = false)
    private ExpenseGroup groupid;

    @ManyToOne
    @JoinColumn(name = "paidby" , nullable = false)
    private User paidby;

    @ManyToOne
    @JoinColumn(name = "paidto" , nullable = false)
    private User paidto;

    @Column(nullable = false)
    private Double amount;

    private Timestamp date;
}

