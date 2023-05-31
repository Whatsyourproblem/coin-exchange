package com.lx.util;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/*
*  导出工具类
* */
public class ReportCsvUtils {

    public static void reportList(
            HttpServletResponse response,
            String[] headers,
            String[] properties,
            String filename,
            List<?> sourceList
    ) throws Exception{
        reportList(response, headers, properties, filename, sourceList, null);
    }

    /**
     * 导出CSV
     * @param response      响应
     * @param headers       cav头(文字)
     * @param properties    csv对应的bean属性
     * @param filename      文件名
     * @param sourceList    源数据
     * @param processors    单元格处理器
     * @throws Exception    异常
     */
    public static void reportList(
            HttpServletResponse response,
            String[] headers,
            String[] properties,
            String filename,
            List<?> sourceList,
            CellProcessor[] processors
    ) throws IOException {
        if (ArrayUtils.isEmpty(headers) || ArrayUtils.isEmpty(properties) || CollectionUtils.isEmpty(sourceList)) {
            return;
        }
        if (StringUtils.isEmpty(filename)) {
            filename = "1.csv";
        }
        response.setContentType("application/csv");
        response.setCharacterEncoding("GBK");
        response.setHeader("Content-FileName", URLEncoder.encode(filename, "UTF-8"));
        response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(filename, "UTF-8") + "\"");
        CsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        csvBeanWriter.writeHeader(headers);
        if (ArrayUtils.isEmpty(processors)) {
            for (Object obj : sourceList) {
                csvBeanWriter.write(obj, properties);
            }
        } else {
            for (Object obj : sourceList) {
                csvBeanWriter.write(obj, properties, processors);
            }
        }

        csvBeanWriter.close();
    }
}
