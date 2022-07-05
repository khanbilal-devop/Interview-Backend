package com.interview.model;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class GenericFilter {

    private Short currentPage;
    private Short pageSize;
    private boolean isPageable = false;
    private boolean ascending;
    private String orderBy;

    private User user;
}
