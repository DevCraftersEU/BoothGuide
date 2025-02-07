package com.westers.boothguide.rest;

import com.westers.boothguide.model.dto.DesignDTO;
import com.westers.boothguide.model.jpa.ApplicationInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles("test-with-default-design")
public class DefaultDesignControllerTest extends AbstractRestTest {

    private static final String REGEX_HEX = "^#(?:[0-9a-fA-F]{3}){1,2}$";
    private static final Pattern hexPattern = Pattern.compile(REGEX_HEX);

    @Autowired
    private ApplicationInfoRepository applicationInfoRepository;

    @BeforeEach
    public void beforeEach() {
        applicationInfoRepository.deleteAll();
    }

    @Test
    public void testDefaultDesignWorks() {
        var res = restTemplate.getForEntity(generateUrl("/pub/design"), DesignDTO.class);
        assertTrue(res.getStatusCode().is2xxSuccessful());
        var design = res.getBody();
        assertNotNull(design);
        assertTrue(design.getBackground() != null && design.getSurface() != null && design.getPrimary() != null);
        assertTrue(hexPattern.matcher(design.getBackground()).matches());
        assertTrue(hexPattern.matcher(design.getSurface()).matches());
        assertTrue(hexPattern.matcher(design.getPrimary()).matches());
        assertEquals(0, design.getVersion());
    }

}
