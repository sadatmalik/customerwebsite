package com.sadatmalik.customerwebsite.config;

import com.sadatmalik.customerwebsite.model.Account;
import com.sadatmalik.customerwebsite.repositories.AccountRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    JobRepository jobRepository;

    //jobLauncher - asynchronous
    //initially returns a JobExecution on run() with an ExitStatus.UNKNOWN
    @Bean
    public JobLauncher asyncJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public static RunIdIncrementer runIdIncrementer() {
        return new RunIdIncrementer();
    }

    //job
    @Bean
    public Job eodCustomerBalances(JobBuilderFactory jobBuilderFactory, Step processEodCustomerBalances) {
        return jobBuilderFactory.get("eod-customer-balances")
                .incrementer(new RunIdIncrementer())
                .start(processEodCustomerBalances)
                .build();
    }

    //step
    @Bean
    public Step processEodCustomerBalances(StepBuilderFactory stepBuilderFactory, ItemReader<Account> csvReader,
                                   AccountProcessor processor, AccountWriter writer) {
        // This step just reads the csv file and then writes the entries into the database
        return stepBuilderFactory.get("processEodCustomerBalances")
                .<Account, Account>chunk(100)
                .reader(csvReader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(false)
                .build();
    }

    //reader
    @Bean
    public FlatFileItemReader<Account> csvReader(@Value("${inputFile}") String inputFile) {
        return new FlatFileItemReaderBuilder<Account>()
                .name("csv-reader")
                .resource(new ClassPathResource(inputFile))
                .delimited()
                .names("id", "firstName", "lastName", "email", "address",
                        "creditCardNum", "creditCardType", "balance")
                .linesToSkip(1)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{setTargetType(Account.class);}})
                .build();
    }

    @Bean
    public RepositoryItemReader<Account> repositoryReader(AccountRepository accountRepository) {
        return new RepositoryItemReaderBuilder<Account>()
                .repository(accountRepository)
                .methodName("findAll")
                .sorts(Map.of("id", Sort.Direction.ASC))
                .name("repository-reader")
                .build();
    }

    //processor
    @Component
    public static class AccountProcessor implements ItemProcessor<Account, Account> {
        // This helps you to process the names of the employee at a set time
        @Override
        public Account process(Account account) {
            account.setUpdatedAt(new Date());
            return account;
        }
    }

    //writer
    @Component
    public static class AccountWriter implements ItemWriter<Account> {

        @Autowired
        private AccountRepository accountRepository;

        @Value("${sleepTime}")
        private Integer SLEEP_TIME;

        @Override
        public void write(List<? extends Account> accounts) throws InterruptedException {
            accountRepository.saveAll(accounts);
            Thread.sleep(SLEEP_TIME);
            System.out.println("Saved eod customer balances: " + accounts);
        }
    }


}
