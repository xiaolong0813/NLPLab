package autocheck;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.xm.Similarity;
import org.xm.similarity.text.*;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
@EnableAsync
public class Application {
    private static final Logger logger= LogManager.getLogger(Application.class);

    @Value("${async.core-pool-size}")
    private Integer corePoolSize;

    @Value("${async.max-pool-size}")
    private Integer maxPoolSize;

    @Value("${async.queue-capacity}")
    private Integer queueCapacity;

    @Bean
    public Executor asyncExecutor() {
        logger.info("Initialize async executor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("Async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
    public static void main(String[] args) {
        logger.info("Start the server");
        SpringApplication.run(Application.class, args);
    }
}
