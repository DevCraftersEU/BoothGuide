package com.westers.boothguide.rest;

import com.westers.boothguide.model.Exhibitor;
import com.westers.boothguide.model.dto.ExhibitorDTO;
import com.westers.boothguide.model.jpa.ExhibitorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ExhibitorControllerTest extends AbstractRestTest {

    @Autowired
    private ExhibitorRepository exhibitorRepository;

    @BeforeEach
    public void before() {
        exhibitorRepository.deleteAll();
    }

    private String generateRandomOffers(int count) {
        var offers = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            offers.add(String.join(",", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        }
        return String.join(";", offers);
    }

    private ExhibitorDTO generateRandomDTO() {
        Exhibitor exhibitor = new Exhibitor(null, UUID.randomUUID().toString(), generateRandomOffers(2), UUID.randomUUID().toString());
        return new ExhibitorDTO(exhibitor);
    }

    @Test
    public void testStorageOfExhibitor() {
        var dto = generateRandomDTO();
        checkRemoteCountOfExhibitors(0, Collections.emptyList());
        var postResponse = authRestTemplate().postForEntity(generateUrl("/auth/exhibitors"), dto, ExhibitorDTO.class);
        assertTrue(postResponse.getStatusCode().is2xxSuccessful());
        dto.setId(Objects.requireNonNull(postResponse.getBody()).getId());
        assertEquals(dto, postResponse.getBody());
        var remoteEntities = checkRemoteCountOfExhibitors(1, List.of(dto));
        assertEquals(postResponse.getBody(), Arrays.stream(remoteEntities).findFirst().orElse(null));
        var secondDTO = generateRandomDTO();
        secondDTO.setOffers(new String[0][]);
        postResponse = authRestTemplate().postForEntity(generateUrl("/auth/exhibitors"), secondDTO, ExhibitorDTO.class);
        assertTrue(postResponse.getStatusCode().is2xxSuccessful());
        assertTrue(secondDTO.equals(postResponse.getBody(), true));
    }

    @Test
    public void testBulkImportAndExport() {
        List<Exhibitor> exhibitorsRound1 = new ArrayList<>();
        List<Exhibitor> exhibitorsRound2 = new ArrayList<>();
        List<Exhibitor> exhibitorsRound3 = new ArrayList<>();
        int countOfExhibitors = 20;
        for (int i = 0; i < countOfExhibitors; i++) {
            exhibitorsRound1.add(new Exhibitor(null, UUID.randomUUID().toString(), generateRandomOffers(3), UUID.randomUUID().toString()));
            exhibitorsRound2.add(new Exhibitor(null, UUID.randomUUID().toString(), generateRandomOffers(3), UUID.randomUUID().toString()));
            exhibitorsRound3.add(new Exhibitor(null, UUID.randomUUID().toString(), generateRandomOffers(3), UUID.randomUUID().toString()));
        }
        checkRemoteCountOfExhibitors(0, Collections.emptyList());
        var postResponse = authRestTemplate().postForEntity(generateUrl("/auth/exhibitors/import/true"), exhibitorsRound1.stream().map(ExhibitorDTO::new).toList(), ExhibitorDTO[].class);
        assertTrue(postResponse.getStatusCode().is2xxSuccessful());
        assertEquals(exhibitorsRound1.size(), Objects.requireNonNull(postResponse.getBody()).length);
        checkRemoteCountOfExhibitors(exhibitorsRound1.size(), exhibitorsRound1.stream().map(ExhibitorDTO::new).toList());
        var postResponseRound2 = authRestTemplate().postForEntity(generateUrl("/auth/exhibitors/import/false"), exhibitorsRound2.stream().map(ExhibitorDTO::new).toList(), ExhibitorDTO[].class);
        assertTrue(postResponseRound2.getStatusCode().is2xxSuccessful());
        assertEquals(exhibitorsRound1.size() + exhibitorsRound2.size(), Objects.requireNonNull(postResponseRound2.getBody()).length);
        checkRemoteCountOfExhibitors(exhibitorsRound1.size() + exhibitorsRound2.size(), Stream.concat(exhibitorsRound1.stream(), exhibitorsRound2.stream()).map(ExhibitorDTO::new).toList());
        var postResponseRound3 = authRestTemplate().postForEntity(generateUrl("/auth/exhibitors/import/true"), exhibitorsRound3.stream().map(ExhibitorDTO::new).toList(), ExhibitorDTO[].class);
        assertTrue(postResponseRound3.getStatusCode().is2xxSuccessful());
        assertEquals(exhibitorsRound3.size(), Objects.requireNonNull(postResponseRound3.getBody()).length);
        checkRemoteCountOfExhibitors(exhibitorsRound3.size(), exhibitorsRound3.stream().map(ExhibitorDTO::new).toList());
    }

    @Test
    public void testUpdateInvalidExhibitor() {
        var dto = generateRandomDTO();
        var putResponse = authRestTemplate().exchange(generateUrl("/auth/exhibitors"), HttpMethod.PUT, new HttpEntity<>(dto), ExhibitorDTO.class);
        assertTrue(putResponse.getStatusCode().is4xxClientError());
        var persistedEntity = authRestTemplate().postForEntity(generateUrl("/auth/exhibitors"), dto, ExhibitorDTO.class).getBody();
        assertNotNull(persistedEntity);
        persistedEntity.setId(persistedEntity.getId()+1);
        putResponse = authRestTemplate().exchange(generateUrl("/auth/exhibitors"), HttpMethod.PUT, new HttpEntity<>(dto), ExhibitorDTO.class);
        assertTrue(putResponse.getStatusCode().is4xxClientError());
    }

    @Test
    public void testUpdateExhibitor() {
        checkRemoteCountOfExhibitors(0, Collections.emptyList());
        var dto = generateRandomDTO();
        var postResponse = authRestTemplate().postForEntity(generateUrl("/auth/exhibitors"), dto, ExhibitorDTO.class);
        assertTrue(postResponse.getStatusCode().is2xxSuccessful());
        checkRemoteCountOfExhibitors(1, List.of(dto));
        var persistedEntity = postResponse.getBody();
        assertNotNull(persistedEntity);
        persistedEntity.setName("CHANGED");
        persistedEntity.setOffers(new String[0][]);
        persistedEntity.setRoomNumber("CHANGED");
        var putResponse = authRestTemplate().exchange(generateUrl("/auth/exhibitors"), HttpMethod.PUT, new HttpEntity<>(persistedEntity), ExhibitorDTO.class);
        assertTrue(putResponse.getStatusCode().is2xxSuccessful());
        assertEquals(persistedEntity, putResponse.getBody());
    }

    private ExhibitorDTO[] checkRemoteCountOfExhibitors(int expectedCount, List<ExhibitorDTO> compareTo) {
        var response = restTemplate.getForEntity(generateUrl("/pub/exhibitors"), ExhibitorDTO[].class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(Objects.requireNonNull(response.getBody()).length, expectedCount);
        if (compareTo != null) {
            var entities = response.getBody();
            assertTrue(Arrays.stream(entities).noneMatch(exhibitorDTO -> compareTo.stream().noneMatch(compareDTO -> compareDTO.equals(exhibitorDTO, true))));
            assertTrue(compareTo.stream().noneMatch(compareToEntity -> Arrays.stream(entities).noneMatch(exhibitorDTO -> exhibitorDTO.equals(compareToEntity, true))));
        }
        return response.getBody();
    }
}
