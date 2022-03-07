package myblog.blog.shared.utils;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;

public class MapperUtils {
    private static final ModelMapper modelMapper;
    private static final Gson gson;

    static {
        modelMapper =  new ModelMapper();
            modelMapper.getConfiguration()
                    .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                    .setFieldMatchingEnabled(true);
        gson = new Gson();
    }

    public static ModelMapper getModelMapper(){
        return modelMapper;
    }

    public static Gson getGson(){
        return gson;
    }
}
