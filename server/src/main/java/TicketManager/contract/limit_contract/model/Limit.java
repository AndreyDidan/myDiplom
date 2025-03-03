package TicketManager.contract.limit_contract.model;

import TicketManager.contract.model.Contract;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "limits")
public class Limit {
    @Id
    @Column(name = "limits_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long limitId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contract_id")
    private Contract contractId;

    //название (Авария/Средняя/Низкая)
    @Column(name = "level_name")
    private String levelName;

    @Column(name = "category_name")
    private String categoryName;

    //Вводим процент за 1 день просрочки
    @Column(name = "penalty_price")
    private Double penaltyPrice;

    //Время после которого заявка будет просрочена
    @Column(name = "penalty_start")
    private Duration penaltysStart;

    @Transient // Это поле не должно сохраняться в базе данных
    private String formattedDuration;

    public Limit(Contract contractId, String levelName, String categoryName, Double penaltyPrice, Duration penaltysStart) {
        this.contractId = contractId;
        this.levelName = levelName;
        this.categoryName = categoryName;
        this.penaltyPrice = penaltyPrice;
        this.penaltysStart = penaltysStart;
    }

    public String getFormattedPenaltysStart() {
        if (penaltysStart == null) {
            return "0 дней, 0 часов, 0 минут";
        }

        long days = penaltysStart.toDays();
        long hours = penaltysStart.toHours() % 24; // Остаток от деления на 24
        long minutes = penaltysStart.toMinutes() % 60; // Остаток от деления на 60

        return String.format("дней: %d, часов: %d, минут: %d", days, hours, minutes);
    }
}