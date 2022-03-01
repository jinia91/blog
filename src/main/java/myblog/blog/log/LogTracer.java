package myblog.blog.log;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogTracer {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    public TraceStatus begin(String message){
        syncTraceId();
        TraceId traceId = traceIdHolder.get();
        Long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{}",traceId.getId(), addSpace(START_PREFIX,
                traceId.getLevel()), message);
        return new TraceStatus(traceId, startTimeMs, message);
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    public void end(TraceStatus traceStatus){
        complete(traceStatus, null);
    }

    public void exception(TraceStatus traceStatus, Exception ex){
        complete(traceStatus, ex);
    }

    private void complete(TraceStatus traceStatus, Exception ex) {
        Long stopTimeMs = System.currentTimeMillis();
        Long resultTimeMs = stopTimeMs - traceStatus.getStartTimesMs();
        TraceId traceId = traceStatus.getTraceId();
        if(ex == null){
            log.info("[{}] {} {} time = {}ms", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()),
                    traceStatus.getMessage(), resultTimeMs);
        } else {
            log.info("[{}] {} {} time = {}ms ex={}", traceId.getId(), addSpace(EX_PREFIX, traceId.getLevel()),
                    traceStatus.getMessage(), resultTimeMs, ex.toString());
            Sentry.captureMessage(String.format("[%s] %s %s time = %sms ex = %s",
                    traceId.getId(),addSpace(START_PREFIX, traceId.getLevel()),traceStatus.getMessage(), resultTimeMs, ex.toString()));
        }
        releaseTraceId();
    }

    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove(); //destroy
        } else {
            traceIdHolder.set(traceId.createPrevId());
        }
    }

    private String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i< level; i++){
            sb.append((i==level-1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }
}
