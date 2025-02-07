package com.westers.boothguide.rest;

import com.westers.boothguide.model.dto.DesignDTO;
import com.westers.boothguide.services.ApplicationInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class DesignController {

    private final Logger logger = LoggerFactory.getLogger(DesignController.class);

    @Value("${design.default.enable}")
    private boolean defaultDesignEnabled;
    @Value("${design.default.background}")
    private String defaultBackground;
    @Value("${design.default.surface}")
    private String defaultSurface;
    @Value("${design.default.primary}")
    private String defaultPrimary;

    private static final String DESIGN_INFO_TYPE_KEY = "DESIGN";

    private final ApplicationInfoService applicationInfoService;

    public DesignController(ApplicationInfoService applicationInfoService) {
        this.applicationInfoService = applicationInfoService;
    }

    @GetMapping("/pub/design")
    public ResponseEntity<DesignDTO> getDesign() {
        var design = applicationInfoService.getApplicationInfo(DESIGN_INFO_TYPE_KEY, "default");
        if (design.isEmpty()) {
            return defaultDesignEnabled ? ResponseEntity.ok(new DesignDTO(defaultBackground, defaultSurface, defaultPrimary, 0)) : ResponseEntity.noContent().build();
        }
        try {
            return ResponseEntity.ok(deserialize(design.get().getInfoValue()));
        } catch (Exception e) {
            logger.error("Unexpected error while loading design. Returning no design instead", e);
            return ResponseEntity.noContent().build();
        }
    }

    private static final String HEX_COLOR_REGEX_PATTERN = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$";
    private static final Pattern hex_color_pattern = Pattern.compile(HEX_COLOR_REGEX_PATTERN);


    @PostMapping("/auth/design")
    public ResponseEntity<DesignDTO> setDesign(@RequestBody DesignDTO designDTO, Authentication authentication) {
        logger.info("{} requested design change", authentication.getName());
        if (isInvalidHexColor(designDTO.getBackground()) || isInvalidHexColor(designDTO.getSurface()) || isInvalidHexColor(designDTO.getPrimary())) {
            logger.info("Invalid hex color in {}", designDTO);
            return ResponseEntity.badRequest().build();
        }
        var currentVersion = applicationInfoService.getApplicationInfo(DESIGN_INFO_TYPE_KEY, "default").map(applicationInfo -> {
            try {
                return deserialize(applicationInfo.getInfoValue()).getVersion();
            } catch (IOException | ClassNotFoundException e) {
                logger.warn("Tried to deserialize design from application info. This should not happen", e);
                return 0;
            }
        });
        designDTO.setVersion(currentVersion.orElse(0) + 1);
        try {
            applicationInfoService.saveApplicationInfo(DESIGN_INFO_TYPE_KEY, "default", serialize(designDTO), true);
            logger.info("Design successfully saved by {}!", authentication.getName());
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            logger.error("Could not serialize new design: {}", designDTO, e);
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean isInvalidHexColor(String hexColor) {
        Matcher matcher = hex_color_pattern.matcher(hexColor);
        return !matcher.matches();
    }

    String serialize(DesignDTO design) throws IOException {
        var byteArrayStream = new ByteArrayOutputStream();
        var objectStream = new ObjectOutputStream(byteArrayStream);
        objectStream.writeObject(design);
        objectStream.flush();
        objectStream.close();
        return Base64.getEncoder().encodeToString(byteArrayStream.toByteArray());
    }

    DesignDTO deserialize(String base64) throws IOException, ClassNotFoundException {
        var byteArrayStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
        var objectStream = new ObjectInputStream(byteArrayStream);
        return (DesignDTO) objectStream.readObject();
    }
}
