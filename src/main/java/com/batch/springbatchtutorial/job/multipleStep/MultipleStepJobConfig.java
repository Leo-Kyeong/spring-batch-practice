package com.batch.springbatchtutorial.job.multipleStep;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * desc: 다중 step을 사용하기 및 step to step 데이터 전달
 * run: --spring.batch.job.names=multipleStepJob
 */
@Configuration
@RequiredArgsConstructor
public class MultipleStepJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job multipleStepJob(Step multipleStep1, Step multipleStep2, Step multipleStep3) {
        return jobBuilderFactory.get("multipleStepJob")
                .incrementer(new RunIdIncrementer())
                .start(multipleStep1) // Step 순차 등록
                .next(multipleStep2)
                .next(multipleStep3)
                .build();
    }

    @JobScope
    @Bean
    public Step multipleStep1() {
       return stepBuilderFactory.get("multipleStep1")
               .tasklet((contribution, chunkContext) -> {
                   System.out.println("step1");
                   return RepeatStatus.FINISHED;
               })
               .build();
    }

    @JobScope
    @Bean
    public Step multipleStep2() {
        return stepBuilderFactory.get("multipleStep2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2");

                    // 다음 스텝에 데이터를 넘겨줄 수 있다.
                    ExecutionContext executionContext = chunkContext
                            .getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext();

                    executionContext.put("someKey", "hello!!"); // Context에 데이터를 담아서 Step 끼리 공유할 수 있다.

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @JobScope
    @Bean
    public Step multipleStep3() {
        return stepBuilderFactory.get("multipleStep3")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step3");

                    ExecutionContext executionContext = chunkContext
                            .getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext();

                    System.out.println(executionContext.get("someKey")); //  Context에서 이전 스텝에서 저장한 데이터를 가져올 수 있다.

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
