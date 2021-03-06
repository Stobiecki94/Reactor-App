package stobiecki.reactor.demoapp.reader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import stobiecki.reactor.demoapp.reader.async.AsyncBillingService;
import stobiecki.reactor.demoapp.reader.async.AsyncBillingServlet;
import stobiecki.reactor.demoapp.reader.sync.BillingService;
import stobiecki.reactor.demoapp.reader.sync.BillingServlet;

@SpringBootApplication
public class JdbcReaderApplication {

    public static final String SQL_QUERY = "select first_name, last_name, type, start_time, duration from billing_record";

    public static void main(String[] args) {
        SpringApplication.run(JdbcReaderApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(BillingService billingService) {
        BillingServlet billingServlet = new BillingServlet(billingService);
        return new ServletRegistrationBean(billingServlet, "/billingSync");
    }

    @Bean
    public ServletRegistrationBean asyncBillingServletRegistrationBean(AsyncBillingService asyncBillingService) {
        AsyncBillingServlet asyncBillingServlet = new AsyncBillingServlet(asyncBillingService);
        return new ServletRegistrationBean(asyncBillingServlet, "/billingAsync");
    }

}
