package org.tools4j.tabular.util;

import java.io.Reader;

public interface FileDownloader {
    Reader downloadFile(String urlStr);
}
