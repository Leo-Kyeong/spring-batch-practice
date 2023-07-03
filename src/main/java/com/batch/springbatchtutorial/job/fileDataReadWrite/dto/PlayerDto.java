package com.batch.springbatchtutorial.job.fileDataReadWrite.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerDto {
    private String id;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
}
