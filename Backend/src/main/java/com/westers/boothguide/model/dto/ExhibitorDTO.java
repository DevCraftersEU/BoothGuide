package com.westers.boothguide.model.dto;

import com.westers.boothguide.model.Exhibitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitorDTO {
    private Long id;
    private String name;
    private String[][] offers;
    private String roomNumber;

    public ExhibitorDTO(Exhibitor exhibitor) {
        this.id = exhibitor.getId();
        this.name = exhibitor.getName();
        this.roomNumber = exhibitor.getRoomNumber();
        if (exhibitor.getOffers().isBlank())
            this.offers = new String[0][0];
        else
            this.offers = Arrays.stream(exhibitor.getOffers().split(";")).map(i -> Arrays.stream(i.split("(?<!\\\\),")).map(item -> item.replace("\\,", ",")).toArray(String[]::new)).toArray(String[][]::new);
    }

    @Override
    public boolean equals(Object o) {
        return equals(o, false);
    }

    /**
     * Compares checks, if an object has the same values as the called instance.
     *
     * @param o        The object to compare to
     * @param ignoreID If true, the ID will not be checked for equality
     * @return If the given object is equals to the instance
     */
    public boolean equals(Object o, boolean ignoreID) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExhibitorDTO dto = (ExhibitorDTO) o;
        if (this.offers.length != dto.offers.length) return false;
        for (int i = 0; i < this.offers.length; i++) {
            if (this.offers[i].length != dto.offers[i].length) return false;
            for (int j = 0; j < this.offers[i].length; j++) {
                if (!this.offers[i][j].equals(dto.offers[i][j])) return false;
            }
        }
        if (ignoreID)
            return Objects.equals(name, dto.name) && Objects.equals(roomNumber, dto.roomNumber);
        return Objects.equals(id, dto.id) && Objects.equals(name, dto.name) && Objects.equals(roomNumber, dto.roomNumber);
    }

    @Override
    public int hashCode() {
        var offersToHash = Arrays.stream(this.offers).map(i -> String.join(",", i)).collect(Collectors.joining(";"));
        return Objects.hash(id, name, offersToHash, roomNumber);
    }
}
