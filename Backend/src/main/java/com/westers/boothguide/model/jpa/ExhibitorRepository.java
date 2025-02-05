package com.westers.boothguide.model.jpa;

import com.westers.boothguide.model.Exhibitor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ExhibitorRepository extends CrudRepository<Exhibitor, Long> {

    @NonNull
    List<Exhibitor> findAll();

    void deleteById(@NonNull Long id);

}
