package com.westers.boothguide.model;

import com.westers.boothguide.model.dto.ExhibitorDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConversionTests {

    @Test
    void exhibitorOffersJoiningWorks() {
        List<String[]> offers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int entryCount = (int) (Math.ceil(Math.random() * 10) + 1);
            var offer = new String[entryCount];
            offer[0] = "Category " + i;
            for (int j = 1; j < entryCount; j++) {
                offer[j] = UUID.randomUUID().toString();
            }
            offers.add(offer);
        }

        var dto = new ExhibitorDTO(null, "Test", offers.toArray(String[][]::new), "Room X");
        var ex = new Exhibitor(dto);
        var newDTO = new ExhibitorDTO(ex);

        assertEquals(dto.hashCode(), newDTO.hashCode());
        assertEquals(dto, newDTO);
    }

    @Test
    void testDtoConversion() {
        var dto = new ExhibitorDTO(3L, "Example Name", new String[0][], "Roomnumber");
        var converted = new Exhibitor(dto);
        var revertedDTO = new ExhibitorDTO(converted);
        assertEquals(dto, revertedDTO);

        var ex = new Exhibitor(17L, "Example Name", "", "Roomnumber");
        var convertedDTO = new ExhibitorDTO(ex);
        var revertedEx = new Exhibitor(convertedDTO);
        assertEquals(ex, revertedEx);
    }

}
