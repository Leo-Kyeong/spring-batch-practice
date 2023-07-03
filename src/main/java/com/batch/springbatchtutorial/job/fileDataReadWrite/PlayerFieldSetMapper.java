package com.batch.springbatchtutorial.job.fileDataReadWrite;

import com.batch.springbatchtutorial.job.fileDataReadWrite.dto.PlayerDto;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class PlayerFieldSetMapper implements FieldSetMapper<PlayerDto> {

    public PlayerDto mapFieldSet(FieldSet fieldSet) {
        return PlayerDto.builder()
                .id(fieldSet.readString(0))
                .lastName(fieldSet.readString(1))
                .firstName(fieldSet.readString(2))
                .position(fieldSet.readString(3))
                .birthYear(fieldSet.readInt(4))
                .debutYear(fieldSet.readInt(5))
                .build();
    }
}
