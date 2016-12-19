package com.evrikaklin.batch;

import com.evrikaklin.dmodel.ChangedDealStatus;
import com.evrikaklin.dmodel.ChangedEntityStatus;
import com.evrikaklin.service.AmoCRMLeadsFromSiteService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by berz on 18.12.2016.
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
@PropertySource("classpath:batch.properties")
public class RecreatingSuccessfullDeals {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    AmoCRMLeadsFromSiteService amoCRMLeadsFromSiteService;


    public static int PERIOD_DAYS = 30;

    @Bean
    ItemReader<ChangedDealStatus> changedDealStatusItemReader() {
        JpaPagingItemReader<ChangedDealStatus> reader = new JpaPagingItemReader<>();

        reader.setEntityManagerFactory(entityManager.getEntityManagerFactory());
        reader.setQueryString("select cds from ChangedDealStatus cds where reactionState = :st and dtmCreate <= :dt");

        // Определяем дату на PERIOD_DAYS раньше
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, (-1) * PERIOD_DAYS);

        HashMap<String, Object> params = new LinkedHashMap<>();
        params.put("st", ChangedEntityStatus.ReactionState.NEW);
        params.put("dt", calendar.getTime());
        reader.setParameterValues(params);

        return reader;
    }

    @Bean
    ItemProcessor<ChangedDealStatus, ChangedDealStatus> changedDealStatusProcessor() {
        return new ItemProcessor<ChangedDealStatus, ChangedDealStatus>() {
            @Override
            public ChangedDealStatus process(ChangedDealStatus changedDealStatus) throws Exception {
                try {
                    // lets go
                    return amoCRMLeadsFromSiteService.processSuccessfullyClosedDeal(changedDealStatus);

                } catch (RuntimeException e) {
                    System.out.println("exception while processing ChangedDealStatus");
                    e.printStackTrace();
                    throw e;
                }
            }
        };
    }

    @Bean
    public Step processChangedDealStatusStep(
            StepBuilderFactory stepBuilderFactory,
            ItemReader<ChangedDealStatus> changedDealStatusItemReader,
            ItemProcessor<ChangedDealStatus, ChangedDealStatus> changedDealStatusProcessor

    ) {
        return stepBuilderFactory.get("processChangedDealStatusStep")
                .<ChangedDealStatus, ChangedDealStatus>chunk(1)
                .reader(changedDealStatusItemReader)
                .processor(changedDealStatusProcessor)
                .faultTolerant()
                .skip(RuntimeException.class)
                .skipLimit(2000)
                .build();
    }

    @Bean
    public Job recreatingSuccessfullDealsJob(
            Step processChangedDealStatusStep
    ) {
        RunIdIncrementer runIdIncrementer = new RunIdIncrementer();

        return jobBuilderFactory.get("recreatingSuccessfullDealsJob")
                .incrementer(runIdIncrementer)
                .flow(processChangedDealStatusStep)
                .end()
                .build();
    }
}
