package com.westers.boothguide.rest;

import com.westers.boothguide.model.Exhibitor;
import com.westers.boothguide.model.dto.ExhibitorDTO;
import com.westers.boothguide.services.ExhibitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ExhibitorController extends AbstractRestController {

    private static final Logger logger = LoggerFactory.getLogger(ExhibitorController.class);

    private final ExhibitorService exhibitorService;

    public ExhibitorController(ExhibitorService exhibitorService) {
        this.exhibitorService = exhibitorService;
    }

    @GetMapping("/pub/exhibitors")
    List<ExhibitorDTO> getAllExhibitors() {
        return exhibitorService.getAllExhibitors().stream().map(ExhibitorDTO::new).collect(Collectors.toList());
    }

    @PostMapping("/auth/exhibitors")
    ExhibitorDTO addExhibitor(@RequestBody ExhibitorDTO dto, Authentication authentication) {
        logger.info("{} requested creation of new exhibitor {}", authentication.getName(), dto);
        if (dto.getId() != null && dto.getId() >= 0) {
            throw new WrongMethodException();
        }
        var created = exhibitorService.createExhibitorFromDTO(dto);
        logger.info("Exhibitor with id {} created", created.getId());
        return Optional.of(created).map(ExhibitorDTO::new).orElse(null);
    }

    @PostMapping("/auth/exhibitors/import/{deleteExisting}")
    List<ExhibitorDTO> importExhibitors(@RequestBody List<ExhibitorDTO> dtos, @PathVariable(required = false) boolean deleteExisting, Authentication authentication) {
        logger.info("{} requested import of exhibitors set (count: {})", authentication.getName(), dtos.size());
        var savedExhibitors = exhibitorService.importExhibitors(dtos.stream().map(Exhibitor::new).collect(Collectors.toList()), deleteExisting);
        logger.info("Exhibitors were successfully imported: {}", savedExhibitors.size());
        return savedExhibitors.stream().map(ExhibitorDTO::new).toList();
    }

    @PutMapping("/auth/exhibitors")
    @Transactional
    ResponseEntity<ExhibitorDTO> updateExhibitor(@RequestBody ExhibitorDTO dto, Authentication authentication) {
        logger.info("{} requested update of exhibitor {} with id {}", authentication.getName(), dto.getName(), dto.getId());
        var updatedEx = exhibitorService.updateExhibitor(new Exhibitor(dto));
        logger.info("Exhibitor with id {} was successfully updated", updatedEx.getId());
        return ResponseEntity.ok(new ExhibitorDTO(updatedEx));
    }

    @DeleteMapping("/auth/exhibitors/{id}")
    void deleteExhibitor(@PathVariable Long id, Authentication authentication) {
        logger.info("{} requested deletion of exhibitor with id {}", authentication.getName(), id);
        exhibitorService.deleteExhibitor(id);
        logger.info("Exhibitor with id {} was successfully deleted", id);
    }
}
