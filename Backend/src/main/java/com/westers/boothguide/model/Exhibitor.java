package com.westers.boothguide.model;

import com.westers.boothguide.model.dto.ExhibitorDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exhibitor_seq")
    @SequenceGenerator(name = "exhibitor_seq", sequenceName = "exhibitor_seq", allocationSize = 1)
    private Long id;
    private String name;
    @Column(columnDefinition = "text")
    private String offers;
    private String roomNumber;
    @Column(name = "http_link")
    private String httpLink;

    public Exhibitor(ExhibitorDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.roomNumber = dto.getRoomNumber();
        this.httpLink = dto.getHttpLink();
        this.offers = Arrays.stream(dto.getOffers()).map(item -> Arrays.stream(item).map(o -> o.replace(",", "\\,")).toArray(String[]::new)).map(i -> String.join(",", i)).collect(Collectors.joining(";"));
    }
}
