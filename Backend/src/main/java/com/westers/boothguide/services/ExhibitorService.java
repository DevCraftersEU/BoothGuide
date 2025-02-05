package com.westers.boothguide.services;

import com.westers.boothguide.model.Exhibitor;
import com.westers.boothguide.model.dto.ExhibitorDTO;
import com.westers.boothguide.services.impl.exceptions.ApplicationEntityNotFoundException;

import java.util.Collection;
import java.util.List;

/**
 * Service interface for managing exhibitors.
 */
public interface ExhibitorService {
    /**
     * Returns all persisted exhibitors of the application
     *
     * @return List of {@link Exhibitor} that exist
     */
    List<Exhibitor> getAllExhibitors();

    /**
     * Creates and saves a new {@link Exhibitor}
     *
     * @param name       Name of the new exhibitor
     * @param offers     Offers provided by the exhibitor
     * @param roomNumber Room number where the exhibitor is located
     * @return The created exhibitor.
     */
    Exhibitor createExhibitor(String name, String offers, String roomNumber);

    /**
     * Creates a new {@link Exhibitor} from the given {@link ExhibitorDTO}.
     *
     * @param exhibitorDTO The data transfer object containing exhibitor details.
     * @return The created exhibitor.
     */
    Exhibitor createExhibitorFromDTO(ExhibitorDTO exhibitorDTO);

    /**
     * Imports a collection of {@link Exhibitor}s.
     *
     * @param exhibitors The exhibitors to import.
     * @param overwrite  Flag indicating whether to delete already existing exhibitors. <b>Caution!</b> If set to <code>true</code> all previously saved exhibitors will be deleted! If set to <code>false</code> the imported data will be added to the already existing exhibitors.
     * @return the imported exhibitors.
     */
    Collection<Exhibitor> importExhibitors(List<Exhibitor> exhibitors, boolean overwrite);

    /**
     * Updates the details of an existing exhibitor.
     *
     * @param exhibitor The exhibitor with updated details.
     * @return The updated exhibitor.
     * @throws ApplicationEntityNotFoundException If the exhibitor with the given ID is not found.
     */
    Exhibitor updateExhibitor(Exhibitor exhibitor) throws ApplicationEntityNotFoundException;

    /**
     * Deletes an exhibitor by its ID.
     *
     * @param id the ID of the exhibitor to delete.
     * @throws ApplicationEntityNotFoundException If no exhibitor for the given ID is found.
     */
    void deleteExhibitor(Long id) throws ApplicationEntityNotFoundException;
}
