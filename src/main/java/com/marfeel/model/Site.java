package com.marfeel.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by masber on 11/06/2017.
 */
@Document(collection = "site")
@Data
@Builder
public class Site {

    @Id
    private String id;

    private String url;
    private int rank;
    private boolean marfeelizable;
    private String error;

}
