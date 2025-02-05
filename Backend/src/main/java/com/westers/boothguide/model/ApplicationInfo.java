package com.westers.boothguide.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ApplicationInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String infoName;
    @Column(nullable = false, length = 4096)
    private String infoValue;
    @Column(nullable = false)
    private String infoType;
}
