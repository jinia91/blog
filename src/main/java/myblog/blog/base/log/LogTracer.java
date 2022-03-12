package myblog.blog.base.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogTracer {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    public TraceStatusVO begin(String message, String args, String clientIP){
        syncTraceId(clientIP);
        TraceId traceId = traceIdHolder.get();
        Long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{} ,args = {}",traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message, args);
        return new TraceStatusVO(traceId, startTimeMs, message);
    }

    private void syncTraceId(String clientIP) {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId(clientIP));
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    public void end(TraceStatusVO traceStatusVO){
        complete(traceStatusVO, null);
    }

    public void exception(TraceStatusVO traceStatusVO, Exception ex){
        complete(traceStatusVO, ex);
    }

    private void complete(TraceStatusVO traceStatusVO, Exception ex) {
        Long stopTimeMs = System.currentTimeMillis();
        Long resultTimeMs = stopTimeMs - traceStatusVO.getStartTimesMs();
        TraceId traceId = traceStatusVO.getTraceId();
        if(ex == null){
            log.info("[{}] {} {} time = {}ms", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()),
                    traceStatusVO.getMessage(), resultTimeMs);
        } else {
            log.info("[{}] {} {} time = {}ms ex={}", traceId.getId(), addSpace(EX_PREFIX, traceId.getLevel()),
                    traceStatusVO.getMessage(), resultTimeMs, ex.toString());
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
