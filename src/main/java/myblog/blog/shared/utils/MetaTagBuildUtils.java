package myblog.blog.shared.utils;

import java.util.List;

public class MetaTagBuildUtils {
    static public String buildMetaTags(List<String> tags){
        var metaTags = new StringBuilder();
        for (var tag : tags) {
            metaTags.append(tag).append(", ");
        }
        return metaTags.toString();
    }
}
