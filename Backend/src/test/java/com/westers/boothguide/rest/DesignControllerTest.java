package com.westers.boothguide.rest;

import com.westers.boothguide.model.dto.DesignDTO;
import com.westers.boothguide.model.jpa.ApplicationInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DesignControllerTest extends AbstractRestTest {

    @Autowired
    private ApplicationInfoRepository applicationInfoRepository;

    @BeforeEach
    public void beforeEach() {
        applicationInfoRepository.deleteAll();
    }

    @Test
    public void testDesignSaving() {
        String hexColor1 = "#124574";
        String hexColor2 = "#7535AB";
        String hexColor3 = "#AA2739";

        var res = authRestTemplate().postForEntity(generateUrl("/auth/design"), new DesignDTO(hexColor1, hexColor2, hexColor3, -1), DesignDTO.class);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());

        res = restTemplate.getForEntity(generateUrl("/pub/design"), DesignDTO.class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        var remoteDesign = res.getBody();
        assertEquals(new DesignDTO(hexColor1, hexColor2, hexColor3, 1), remoteDesign);

        hexColor1 = "#AF0021";
        hexColor2 = "#987245";
        hexColor3 = "#FF0000";
        res = authRestTemplate().postForEntity(generateUrl("/auth/design"), new DesignDTO(hexColor1, hexColor2, hexColor3, 112), DesignDTO.class);
        assertTrue(res.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(204)));

        res = restTemplate.getForEntity(generateUrl("/pub/design"), DesignDTO.class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        remoteDesign = res.getBody();
        assertEquals(new DesignDTO(hexColor1, hexColor2, hexColor3, 2), remoteDesign);
    }

    @Test
    public void testInvalidDesign() {
        List<ResponseEntity<DesignDTO>> responses = new ArrayList<>();
        responses.add(authRestTemplate().postForEntity(generateUrl("/auth/design"), new DesignDTO("Invalid Hex", "#111111", "#111112", 0), DesignDTO.class));
        responses.add(authRestTemplate().postForEntity(generateUrl("/auth/design"), new DesignDTO("#111111", "Invalid Hex", "#111112", 0), DesignDTO.class));
        responses.add(authRestTemplate().postForEntity(generateUrl("/auth/design"), new DesignDTO("#111112", "#111111", "Invalid Hex", 0), DesignDTO.class));
        responses.add(authRestTemplate().postForEntity(generateUrl("/auth/design"), new DesignDTO("#111112", "#111111", "123456", 0), DesignDTO.class));
        responses.add(authRestTemplate().postForEntity(generateUrl("/auth/design"), new DesignDTO("#111112", "#111111", "", 0), DesignDTO.class));
        responses.add(authRestTemplate().postForEntity(generateUrl("/auth/design"), new DesignDTO("#111112", "##123456", "#123456", 0), DesignDTO.class));
        responses.add(authRestTemplate().postForEntity(generateUrl("/auth/design"), new DesignDTO("#<111112>", "##123456", "#123456", 0), DesignDTO.class));
        for (ResponseEntity<DesignDTO> response : responses) {
            assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(400)));
        }
    }

    @Test
    public void testNoDefaultDesign() {
        var res = restTemplate.getForEntity(generateUrl("/pub/design"), DesignDTO.class);
        assertTrue(res.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(204)));
    }

}
