package com.sadatmalik.customerwebsite.config;

import com.sadatmalik.customerwebsite.model.Account;
import com.sadatmalik.customerwebsite.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfiguration.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobRegistry jobRegistry;

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

    @Bean
    // register all jobs as they are created - required for execution restarts
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }

    //jobs
    @Bean
    public Job eodCustomerBalances(JobBuilderFactory jobBuilderFactory, Step processEodCustomerBalances,
                                   Step dailyAccounts, Step customerInvoices, Step latePaymentReminders) {
        return jobBuilderFactory.get("eod-customer-balances")
                .incrementer(new RunIdIncrementer())
                .start(processEodCustomerBalances)
                .next(dailyAccounts)
                .next(customerInvoices)
                .next(latePaymentReminders)
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

    @Bean
    public Step dailyAccounts(StepBuilderFactory stepBuilderFactory,
                              RepositoryItemReader<Account> repositoryReader,
                              DailyAccounts dailyAccounts, AccountWriter writer) {
        // This step just reads the csv file and then writes the entries into the database
        return stepBuilderFactory.get("dailyAccounts")
                .<Account, Account>chunk(100)
                .reader(repositoryReader)
                .processor(dailyAccounts)
                .writer(writer)
                .build();
    }

    @Bean
    public Step customerInvoices(StepBuilderFactory stepBuilderFactory,
                                 RepositoryItemReader<Account> repositoryReader,
                                 CustomerInvoices customerInvoices, AccountWriter writer) {
        // This step just reads the csv file and then writes the entries into the database
        return stepBuilderFactory.get("customerInvoices")
                .<Account, Account>chunk(100)
                .reader(repositoryReader)
                .processor(customerInvoices)
                .writer(writer)
                .build();
    }

    @Bean
    public Step latePaymentReminders(StepBuilderFactory stepBuilderFactory,
                                 RepositoryItemReader<Account> repositoryReader,
                                 LatePaymentReminders latePaymentReminders, AccountWriter writer) {
        // This step just reads the csv file and then writes the entries into the database
        return stepBuilderFactory.get("latePaymentReminders")
                .<Account, Account>chunk(100)
                .reader(repositoryReader)
                .processor(latePaymentReminders)
                .writer(writer)
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

    //processors
    @Component
    public static class DailyAccounts implements ItemProcessor<Account, Account> {
        // This helps you to process the names of the employee at a set time
        @Override
        public Account process(Account account) {
            account.setUpdatedAt(new Date());
            return account;
        }
    }

    @Component
    public static class AccountProcessor implements ItemProcessor<Account, Account> {
        // This helps you to process the names of the employee at a set time
        @Override
        public Account process(Account account) {
            generateAccounts(account);
            account.setUpdatedAt(new Date());
            return account;
        }
    }

    private static void generateAccounts(Account account) {
        //logic to generate daily accounting checks and balances
        LOGGER.info("Generating daily accounting records for " + account);
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {

        }
        LOGGER.info("Daily accounts complete for " + account);
    }

    @Component
    public static class CustomerInvoices implements ItemProcessor<Account, Account> {
        // This helps you to process the names of the employee at a set time
        @Override
        public Account process(Account account) {
            generateInvoices(account);
            account.setUpdatedAt(new Date());
            return account;
        }
    }

    private static void generateInvoices(Account account) {
        //logic to generate daily accounting checks and balances
        LOGGER.info("Generating daily customer invoice for " + account);
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {

        }
        LOGGER.info("Daily invoicing complete for " + account);
    }

    @Component
    public static class LatePaymentReminders implements ItemProcessor<Account, Account> {
        // This helps you to process the names of the employee at a set time
        @Override
        public Account process(Account account) {
            generateReminders(account);
            account.setUpdatedAt(new Date());
            return account;
        }
    }

    private static void generateReminders(Account account) {
        //logic to generate daily accounting checks and balances
        LOGGER.info("Generating late payment reminders for " + account);
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {

        }
        LOGGER.info("Generating late payment reminders " + account);
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
