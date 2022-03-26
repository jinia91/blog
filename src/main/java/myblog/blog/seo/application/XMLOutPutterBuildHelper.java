package myblog.blog.seo.application;

import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XMLOutPutterBuildHelper {
    static public XMLOutputter getXmlOutputter() {
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        format.setLineSeparator("\r\n");
        return new XMLOutputter(format);
    }
}
