package stobiecki.reactor.demoapp.reader.async;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import reactor.core.scheduler.Schedulers;
import stobiecki.reactor.demoapp.reader.BillingRecord;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
public class AsyncBillingServlet extends HttpServlet {

    private final AsyncBillingService asyncBillingService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        log.info("Asynchronous processing of getting billing record started");
        AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(0);
        asyncContext.start(() -> processAsynchronously(asyncContext));
        log.info("Servlet method invocation completed");
    }

    @SneakyThrows
    private void processAsynchronously(AsyncContext asyncContext) {
        HttpServletResponse resp = (HttpServletResponse) asyncContext.getResponse();
        ServletOutputStream outputStream = resp.getOutputStream();
        Consumer<Throwable> errorHandler = errorHandler(asyncContext);
        asyncBillingService.findBillingRecords()
                .subscribeOn(Schedulers.elastic())
                .map(BillingRecord::toCsvString)
                .subscribe(s -> {
                    try {
                        outputStream.println(s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, errorHandler, asyncContext::complete);
    }

    private Consumer<Throwable> errorHandler(AsyncContext asyncContext) {
        return ex -> onError(ex, asyncContext, (HttpServletResponse) asyncContext.getResponse());
    }

    @SneakyThrows
    private void onError(Throwable throwable, AsyncContext asyncContext, HttpServletResponse resp) {
        log.error("Error during getting billing data", throwable);
        resp.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Oops, something went wrong: " + throwable.getMessage());
        asyncContext.complete();
    }
}
