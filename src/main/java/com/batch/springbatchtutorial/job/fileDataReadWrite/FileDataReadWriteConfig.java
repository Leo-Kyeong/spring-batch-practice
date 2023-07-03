package com.batch.springbatchtutorial.job.fileDataReadWrite;

import com.batch.springbatchtutorial.job.fileDataReadWrite.dto.PlayerDto;
import com.batch.springbatchtutorial.job.fileDataReadWrite.dto.PlayerYearsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.time.Year;
import java.util.List;

/**
 * desc: 파일 읽고 쓰기
 * run: --spring.batch.job.names=fileReadWriteJob
 */
@Configuration
@RequiredArgsConstructor
public class FileDataReadWriteConfig {

    private final int CHUNK_SIZE = 5;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fileReadWriteJob(Step fileReadWriteStep) {
        return jobBuilderFactory.get("fileReadWriteJob")
                .incrementer(new RunIdIncrementer())
                .start(fileReadWriteStep)
                .build();
    }

    @JobScope
    @Bean
    public Step fileReadWriteStep(ItemReader playerItemReader, ItemProcessor playerItemProcessor, ItemWriter playerItemWriter) {
        return stepBuilderFactory.get("fileReadWriteStep")
                .<PlayerDto, PlayerYearsDto>chunk(CHUNK_SIZE)
                .reader(playerItemReader)
                .processor(playerItemProcessor)
                .writer(playerItemWriter)
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemReader<PlayerDto> playerItemReader() {
        return new FlatFileItemReaderBuilder<PlayerDto>()
                .name("playerItemReader")
                .resource(new FileSystemResource("players.csv"))
                .lineTokenizer(new DelimitedLineTokenizer()) // 데이터를 나누는 기준
                .fieldSetMapper(new PlayerFieldSetMapper()) // 파일에서 읽어온 데이터를 객체로 변환
                .linesToSkip(1) // 파일의 첫 번째 줄(필드명) 스킵
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<PlayerDto, PlayerYearsDto> playerItemProcessor() {
        return new ItemProcessor<PlayerDto, PlayerYearsDto>() {
            @Override
            public PlayerYearsDto process(PlayerDto item) throws Exception {
                return PlayerYearsDto.builder()
                        .id(item.getId())
                        .birthYear(item.getBirthYear())
                        .debutYear(item.getDebutYear())
                        .firstName(item.getFirstName())
                        .lastName(item.getLastName())
                        .position(item.getPosition())
                        .yearsExperience(Year.now().getValue() - item.getDebutYear())
                        .build();
            }
        };
    }

    @StepScope
    @Bean
    public FlatFileItemWriter<PlayerYearsDto> playerItemWriter() {
        // 새로운 파일에 어떤 필드를 사용을 할지 명시
        BeanWrapperFieldExtractor<PlayerYearsDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "lastName", "position", "yearsExperience"});
        fieldExtractor.afterPropertiesSet();

        // 어떤 기준으로 파일을 만들어 주는지 명시
        DelimitedLineAggregator<PlayerYearsDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        // 출력 파일 리소스 생성
        FileSystemResource outputResource = new FileSystemResource("players_output.txt");

        return new FlatFileItemWriterBuilder<PlayerYearsDto>()
                .name("playerItemWriter")
                .resource(outputResource)
                .lineAggregator(lineAggregator)
                .build();
    }
}
