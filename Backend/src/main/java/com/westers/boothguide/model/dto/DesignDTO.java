package com.westers.boothguide.model.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class DesignDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String background;
    private String surface;
    private String primary;
    private int version;
}
