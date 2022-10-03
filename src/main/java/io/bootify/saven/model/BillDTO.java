package io.bootify.saven.model;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BillDTO {

    private UUID id;

    @NotNull
    private Long electricityUsed;

    @NotNull
    private Long waterUsed;

    @NotNull
    private Long gasUsed;

    @NotNull
    private Long electricityCost;

    @NotNull
    private Long waterCost;

    @NotNull
    private Long gasCost;

    @NotNull
    private Long totalCost;

    @NotNull
    private LocalDateTime storedDateTime;

    private UUID user;

}
