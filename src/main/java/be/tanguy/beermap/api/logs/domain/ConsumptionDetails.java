package be.tanguy.beermap.api.logs.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsumptionDetails {
    @NotNull
    private Instant consumedAt;

    @Positive
    private int quantity;

    private Integer volumeMl;

    @Min(1) @Max(10)
    private Integer rating;

    @Column(length = 2000)
    private String description;

    @Column(length = 2048)
    private String photoUrl;

    // Logic to normalize strings/volumes goes here
}