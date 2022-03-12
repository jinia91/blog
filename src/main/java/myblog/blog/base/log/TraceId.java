package myblog.blog.base.log;

import lombok.Getter;

@Getter
public class TraceId {
    private final String id;
    private final int level;

    public TraceId(String clientIP) {
        this.id = clientIP;
        this.level = 0;
    }

    private TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

//    private String createdId() {
//        return UUID.randomUUID().toString().substring(0,8);
//    }

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
