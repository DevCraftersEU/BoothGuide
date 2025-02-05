package com.westers.boothguide.services.impl;

import com.westers.boothguide.model.Exhibitor;
import com.westers.boothguide.model.dto.ExhibitorDTO;
import com.westers.boothguide.model.jpa.ExhibitorRepository;
import com.westers.boothguide.services.ExhibitorService;
import com.westers.boothguide.services.impl.exceptions.ApplicationEntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExhibitorServiceImpl implements ExhibitorService {

    private final ExhibitorRepository exhibitorRepository;

    public ExhibitorServiceImpl(ExhibitorRepository exhibitorRepository) {
        this.exhibitorRepository = exhibitorRepository;
    }

    @Override
    public List<Exhibitor> getAllExhibitors() {
        return exhibitorRepository.findAll();
    }

    @Override
    public Exhibitor createExhibitor(String name, String offers, String roomNumber) {
        return exhibitorRepository.save(new Exhibitor(null, name, offers, roomNumber));
    }

    @Override
    public Exhibitor createExhibitorFromDTO(ExhibitorDTO exhibitorDTO) {
        var exhibitor = new Exhibitor(exhibitorDTO);
        exhibitor.setId(null);
        return exhibitorRepository.save(exhibitor);
    }

    @Override
    @Transactional
    public List<Exhibitor> importExhibitors(List<Exhibitor> exhibitors, boolean overwrite) {
        if (overwrite)
            exhibitorRepository.deleteAll();
        exhibitors.forEach(exhibitor -> exhibitor.setId(null));
        exhibitorRepository.saveAll(exhibitors);
        return getAllExhibitors();
    }

    @Override
    @Transactional
    public Exhibitor updateExhibitor(Exhibitor exhibitor) throws ApplicationEntityNotFoundException {
        if (exhibitor == null || exhibitor.getId() == null || !exhibitorRepository.existsById(exhibitor.getId()))
            throw new ApplicationEntityNotFoundException();
        var ex = exhibitorRepository.findById(exhibitor.getId()).orElseThrow(ApplicationEntityNotFoundException::new);
        ex.setName(exhibitor.getName());
        ex.setOffers(exhibitor.getOffers());
        ex.setRoomNumber(exhibitor.getRoomNumber());
        return exhibitorRepository.save(ex);
    }

    @Override
    public void deleteExhibitor(Long id) throws ApplicationEntityNotFoundException {
        if (id == null || !exhibitorRepository.existsById(id))
            throw new ApplicationEntityNotFoundException();
        exhibitorRepository.deleteById(id);
    }

}
