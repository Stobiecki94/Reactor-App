package stobiecki.reactor.demoapp.reader.sync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stobiecki.reactor.demoapp.reader.BillingRecord;

import java.util.List;

import static stobiecki.reactor.demoapp.reader.JdbcReaderApplication.SQL_QUERY;


@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public List<BillingRecord> findBillingRecords() {
        log.info("Loading all billing data");
        return jdbcTemplate.query(SQL_QUERY, new BeanPropertyRowMapper<>(BillingRecord.class));
    }
}
