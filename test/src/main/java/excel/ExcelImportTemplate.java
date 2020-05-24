package excel;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @PackageName: excel
 * @ClassName: ExcelImportTemplate
 * @Description:
 * @author: 熊杰
 * @data：20202020/5/2415:57
 */
@Slf4j
@Data
public class ExcelImportTemplate<T> {
    private List<String> errorMessage;

    private final ICallback callback;

    private final Class<T> tClass;

    private final Field[] fields;

    private final MultipartFile uploadFile;

    private Integer failNumber = 0;

    private Integer successNumber = 0;

    private Integer useLessNumber = 0;

    //构造方法 传入文件 类的字节码对象 字段属性 及回调方法
    public ExcelImportTemplate(MultipartFile uploadFile, Class<T> tClass, Field[] fields, ICallback callback) throws Exception {
        if (Objects.isNull(uploadFile) || uploadFile.isEmpty()) {
            //自定义异常
            throw new Exception("");
        }
        this.uploadFile = uploadFile;
        errorMessage = new ArrayList<>();
        this.tClass = tClass;
        this.callback = callback;
        this.fields = fields;
    }
    //导入
    public List<T> getNewExcelList() throws Exception {
        List<T> list = new ArrayList<>();
        try {
            //校验字段
            if (ArrayUtils.isEmpty(fields)) {
                throw new Exception("");
            }
            Workbook wwb = Workbook.getWorkbook(uploadFile.getInputStream());
            Sheet sheet = wwb.getSheet(0);
            if (sheet.getRows() <= 1) {
                throw new Exception("");
            }
            Cell[] cells;
            int totalNum = 0;

//            log.info("行数：" + sheet.getRows());
            int singRowNum = 0;
            for (int i = 2; i < sheet.getRows(); i++) {
                T t = ReflectUtils.newTClass(tClass);
                cells = sheet.getRow(i);
                totalNum++;
                if (cells.length <= 0 || StringUtils.isBlank(cells[0].getContents())) {
                    singRowNum++;
                }
                if (singRowNum > 5) {
                    break;
                }

                //校验导入上限
                if (totalNum > 5000) {
                    throw new Exception("");
                }

                //标记一行中是否存在某一列成功设置过字段值
                boolean hasDataSetInRow = false;
                int singNum = 0;
                for (int j = 0; (j < cells.length && j < fields.length); j++) {
                    boolean columnDataSet;
                    String value = cells[j].getContents().trim();
                    if (StringUtils.isNotBlank(value)) {
                        //如果不为空，重置break标识
                        singNum = 0;
                        columnDataSet = populateFieldValue(fields[j], t, value);
                        if (columnDataSet) {
                            //填充成功，才标记
                            hasDataSetInRow = true;
                        }
                    } else {
                        //如果是空的默认当前单元格值是设置了的
                        columnDataSet = true;
                        //列控制，大于10次空，需要break
                        if (++singNum >= 10) {
                            break;
                        }
                    }
                    if (!columnDataSet) {
                        //单元格数据填充失败，诊断为数据类型错误
                        failNumber++;
                        errorMessage.add(String.format("第%s行%S列存在问题：[%s]，请全部重新导入！", i + 1, j + 1, "错误信息"));
                    }
                }
                if (hasDataSetInRow) {
                    CallBackParam callBackParam = new CallBackParam();
                    callBackParam.setInput(t);
                    callback.execute(callBackParam);
                    Boolean callBackResult = (Boolean) callBackParam.getOutput();
                    if (!callBackResult) {
                        //校验失败
                        failNumber++;
                        errorMessage.add(String.format("第%s行存在问题：[%s]，请全部重新导入！", (i + 1), callBackResult));
                    } else {
                        //校验成功，值有效
                        if (!list.contains(t)) {
                            list.add(t);
                            successNumber++;
                        } else {
                            //校验成功，值无效
                            useLessNumber++;
                        }
                    }
                    //重新初始化
                    singRowNum = 0;
                }
            }
        } catch (Exception ex) {
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return list;
    }

    //填充数据
    private boolean populateFieldValue(Field key, T t, String value) {
        if (Objects.isNull(key)) {
            //为空表明改位置无需设置内容
            return true;
        }
        key.setAccessible(true);
        Class<?> declaringClass = key.getType();
        try {
            if (declaringClass.equals(String.class)) {
                ReflectUtils.stringValueSetterThrow(t, key.getName(), value);
            } else if (declaringClass.equals(Long.class)) {
                ReflectUtils.setterThrow(t, key.getName(), Long.valueOf(value), declaringClass);
            } else if (declaringClass.equals(Double.class)) {
                ReflectUtils.setterThrow(t, key.getName(), Double.valueOf(value), declaringClass);
            } else if (declaringClass.equals(Byte.class)) {
                ReflectUtils.setterThrow(t, key.getName(), Byte.valueOf(value), declaringClass);
            } else if (declaringClass.equals(Boolean.class)) {
                ReflectUtils.setterThrow(t, key.getName(), Boolean.valueOf(value), declaringClass);
            } else if (declaringClass.equals(BigDecimal.class)) {
                ReflectUtils.setterThrow(t, key.getName(), new BigDecimal(value), declaringClass);
            } else if (declaringClass.equals(Float.class)) {
                ReflectUtils.setterThrow(t, key.getName(), Float.valueOf(value), declaringClass);
            } else if (declaringClass.equals(Integer.class)) {
                ReflectUtils.setterThrow(t, key.getName(), Integer.valueOf(value), declaringClass);
            } else {
                //未知属性返回false
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
