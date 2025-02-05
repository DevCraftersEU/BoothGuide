package com.westers.boothguide.rest;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@RestController
public class FrontendController {

    private static final Logger logger = LoggerFactory.getLogger(FrontendController.class);

    @Value("${application.name}")
    private String applicationName;

    @Value("${application.subtitle}")
    private String applicationSubtitle;

    @Value("${application.show-footer}")
    private boolean showFooter;

    @Value("${application.footer-message}")
    private String footerMessage;

    @Value("${application.footer-image-xs}")
    private String footerImageXS;

    @Value("${application.footer-image-sm}")
    private String footerImageSM;

    @Value("${application.footer-image-md}")
    private String footerImageMD;

    @Value("${application.footer-image-lg}")
    private String footerImageLG;

    @Value("${application.footer-image-xl}")
    private String footerImageXL;

    @Value("${application.footer-image-xxl}")
    private String footerImageXXL;

    @GetMapping("/pub/instanceName")
    public String getInstanceName() {
        return applicationName;
    }

    @GetMapping("/pub/instanceSubtitle")
    public String getInstanceSubtitle() {
        return applicationSubtitle;
    }

    @GetMapping("/pub/footerMessage")
    public ResponseEntity<String> getFooterMessage() {
        if (!showFooter)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(footerMessage);
    }

    @GetMapping("/pub/footerImage")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getFooterImage(@RequestParam(value = "deviceSize", required = false) String deviceSize) {
        var imagePath = getImageForDeviceSize(DeviceSize.fromString(deviceSize));
        if (imagePath == null)
            return ResponseEntity.noContent().build();
        return getInputStreamResourceResponseEntity(imagePath);
    }

    private String getImageForDeviceSize(DeviceSize deviceSize) {
        String returnValue = switch (deviceSize) {
            case XS -> footerImageXS;
            case SM -> footerImageSM;
            case MD -> footerImageMD;
            case LG -> footerImageLG;
            case XL -> footerImageXL;
            case XXL -> footerImageXXL;
        };

        if (!returnValue.isEmpty()) {
            Path path = Path.of(returnValue);
            if (!Files.exists(path)) {
                logger.warn("Could not find footer image file: {}", returnValue);
            } else if (!Files.isRegularFile(path)) {
                logger.error("Path for footer image {} is not a file! Maybe it's a directory?", returnValue);
            } else {
                return returnValue;
            }
        }
        if (deviceSize != DeviceSize.XS) {
            return getImageForDeviceSize(deviceSize.getLowerSize());
        }
        return null;
    }

    private ResponseEntity<InputStreamResource> getInputStreamResourceResponseEntity(String footerImage) {
        MediaType contentType;
        if (footerImage.toLowerCase().endsWith(".jpg") || footerImage.toLowerCase().endsWith(".jpeg")) {
            contentType = MediaType.IMAGE_JPEG;
        } else if (footerImage.toLowerCase().endsWith(".png")) {
            contentType = MediaType.IMAGE_PNG;
        } else if (footerImage.toLowerCase().endsWith(".svg")) {
            contentType = MediaType.valueOf("image/svg+xml");
        } else {
            throw new IllegalArgumentException("Unsupported image type: " + footerImage);
        }
        InputStream in;
        try {
            in = new FileInputStream(footerImage);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Image not found: " + footerImage);
        }
        return ResponseEntity.ok()
                .contentType(contentType).cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                .body(new InputStreamResource(in));
    }

    @Getter
    private enum DeviceSize {
        XS(0), SM(1), MD(2), LG(3), XL(4), XXL(5);

        private final int id;

        DeviceSize(int id) {
            this.id = id;
        }

        public DeviceSize getLowerSize() {
            return fromId(getId() - 1);
        }

        public static DeviceSize fromId(int id) {
            return switch (id) {
                case 1 -> SM;
                case 2 -> MD;
                case 3 -> LG;
                case 4 -> XL;
                case 5 -> XXL;
                default -> XS;
            };
        }

        public static DeviceSize fromString(String s) {
            var upper = s.toUpperCase();
            return switch (upper) {
                case "SM" -> SM;
                case "MD" -> MD;
                case "LG" -> LG;
                case "XL" -> XL;
                case "XXL" -> XXL;
                default -> XS;
            };
        }
    }
}
