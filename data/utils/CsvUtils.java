package abcdef.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * 相关注解
 * 排序:@JsonPropertyOrder(value={"field1", "field2"})
 * 起别名:@JsonProperty("name")
 * @author wuhuan 2018-07-13
 */
public class CsvUtils {

	/**
	 * 默认输出csv文件格式,使用字段头,引用是null
	 * @param <T>
	 * @param object	输出内容
	 * @param file		输出路径
	 * @param separator	分隔符
	 */
	@SuppressWarnings("rawtypes")
	public static <T> void writeCsvFile(Object object, File file, char separator) {
		Class<?> clazz;
		if (object instanceof Collection<?>) {
			Collection c = (Collection) object;
			Iterator it = c.iterator();
			if (!it.hasNext()) {
				return;
			}
			clazz = it.next().getClass();
		} else {
			clazz = object.getClass();
		}
		writeCsvFile(object, clazz, file, separator, true, null);
	}
	
	/**
	 * 自定义输出格式
	 * @param <T>
	 * @param object		输出内容
	 * @param clazz        输出结构
	 * @param file			输出路径
	 * @param separator		分隔符
	 * @param headerFlag	是否输出头
	 * @param quote			引用符
	 */
	public static <T> void writeCsvFile(Object object, Class<T> clazz, File file, char separator, boolean headerFlag, Character quote) {
		CsvMapper csvMapper = new CsvMapper();
		try {
			CsvSchema csvSchema = csvMapper.schemaFor(clazz)
					.withColumnSeparator(separator)
					.withNullValue(null);
			if (quote == null) {
                csvSchema = csvSchema.withoutQuoteChar();               
            } else {
                csvSchema = csvSchema.withQuoteChar(quote);                
            }
			if (headerFlag) {
	        	csvSchema = csvSchema.withHeader();
			}
			SequenceWriter wirte = csvMapper.writer(csvSchema).writeValues(file);
			wirte.write(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 默认格式读csv文件首条内容,使用字段头,引用是null
	 * @param clazz		存储信息实体
	 * @param file		csv文件路径
	 * @param separator	分隔符
	 * @return
	 */
	public static <T> T readCsvFile(Class<T> clazz, File file, char separator) {
		return readCsvFile(clazz, file, separator, true, null);
	}
	
	/**
	 * 自定义格式读csv文件首条内容
	 * @param clazz			存储信息实体
	 * @param file			csv文件路径
	 * @param separator		分隔符
	 * @param headerFlag	是否读头
	 * @param quote			引用符
	 * @return
	 */
	public static <T> T readCsvFile(Class<T> clazz, File file, char separator, boolean headerFlag, Character quote) {
		CsvMapper csvMapper = new CsvMapper();
		try {
			CsvSchema csvSchema = csvMapper.schemaFor(clazz)
					.withColumnSeparator(separator)
					.withNullValue(null);
			if (quote != null) {
				csvSchema = csvSchema.withQuoteChar(quote);
			}
			if (headerFlag) {
	        	csvSchema = csvSchema.withHeader();
			}
			T t = csvMapper.readerFor(clazz).with(csvSchema).readValue(file);
			return t;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 默认格式读csv文件,使用字段头,引用是null
	 * @param clazz		存储信息实体
	 * @param file		csv文件路径
	 * @param separator	分隔符
	 * @return
	 */
	public static <T> List<T> readCsvFileAll(Class<T> clazz, File file, char separator) {
		return readCsvFileAll(clazz, file, separator, true, null);
	}
	
	/**
	 * 自定义格式读csv文件
	 * @param clazz			存储信息实体
	 * @param file			csv文件路径
	 * @param separator		分隔符
	 * @param headerFlag	是否读头
	 * @param quote			引用符
	 * @return
	 */
	public static <T> List<T> readCsvFileAll(Class<T> clazz, File file, char separator, boolean headerFlag, Character quote) {
		CsvMapper csvMapper = new CsvMapper();
		try {
			CsvSchema csvSchema = csvMapper.schemaFor(clazz)
					.withColumnSeparator(separator)
					.withNullValue(null);
			if (quote != null) {
				csvSchema = csvSchema.withQuoteChar(quote);
			}
			if (headerFlag) {
	        	csvSchema = csvSchema.withHeader();
			}
			MappingIterator<T> mappingIterator = csvMapper.readerFor(clazz).with(csvSchema).readValues(file);
			return mappingIterator.readAll();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
