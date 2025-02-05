package com.westers.boothguide.model;

import com.westers.boothguide.model.dto.ExhibitorDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Exhibitor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Column(columnDefinition = "text")
    private String offers;
    private String roomNumber;

    public Exhibitor(ExhibitorDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.roomNumber = dto.getRoomNumber();
        this.offers = Arrays.stream(dto.getOffers()).map(item -> Arrays.stream(item).map(o -> o.replace(",", "\\,")).toArray(String[]::new)).map(i -> String.join(",", i)).collect(Collectors.joining(";"));
    }
}
