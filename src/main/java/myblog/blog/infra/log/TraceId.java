package myblog.blog.infra.log;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TraceId {
    private final String id;
    private final int level;

    public TraceId(String id) {
        this.id = id +"/"+ createdTransactionId();
        this.level = 0;
    }

    private TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    private String createdTransactionId() {
        return UUID.randomUUID().toString().substring(0,8);
    }

    public TraceId createNextId(){
        return new TraceId(id, level+1);
    }

    public TraceId createPrevId(){
        return new TraceId(id, level-1);
    }

    public boolean isFirstLevel(){
        return level == 0;
    }
}
