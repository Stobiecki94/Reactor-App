package stobiecki.reactor.demoapp.reader.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import stobiecki.reactor.demoapp.reader.BillingRecord;

import java.sql.PreparedStatement;

import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_FORWARD_ONLY;
import static stobiecki.reactor.demoapp.reader.JdbcReaderApplication.SQL_QUERY;

@Service
@Slf4j
public class AsyncBillingService {

    private final JdbcTemplate jdbcTemplate;
    private final BeanPropertyRowMapper<BillingRecord> beanPropertyRowMapper;

    public AsyncBillingService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.beanPropertyRowMapper = new BeanPropertyRowMapper(BillingRecord.class);
    }

    public Flux<BillingRecord> findBillingRecords() {
        log.info("Request for creating billing record observable");
        return Flux.create(this::findBillingRecords);
    }

    private void findBillingRecords(FluxSink<BillingRecord> emitter) {
        log.info("Billing record observable created");
        try {
            jdbcTemplate.query(preparedStatementCreator(), createRowCallbackHandler(emitter));
            emitter.complete();
            log.info("Getting data from database completed");
        } catch (Throwable ex) {
            log.error("Cannot read billing records from database", ex);
            emitter.error(ex);
        }
    }

    private RowCallbackHandler createRowCallbackHandler(FluxSink<BillingRecord> emitter) {
        return rs -> {
            BillingRecord billingRecord = beanPropertyRowMapper.mapRow(rs, 0);
            emitter.next(billingRecord);
        };
    }

    private PreparedStatementCreator preparedStatementCreator() {
        return connection -> {
            //in another thread. @Transactional is pointless so let set transaction to readonly
            connection.setAutoCommit(false);
            connection.setReadOnly(true);
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_QUERY, TYPE_FORWARD_ONLY, CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(100);
            log.info("Prepared statement created: {}", preparedStatement);
            return preparedStatement;
        };
    }

}
