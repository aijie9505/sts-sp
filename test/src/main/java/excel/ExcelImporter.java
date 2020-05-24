package excel;

import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @PackageName: excel
 * @ClassName: ExcelImporter
 * @Description:
 * @author: 熊杰
 * @data：20202020/5/2415:49
 */
public class ExcelImporter {

    public Boolean excelImportTemplate(MultipartFile file){
        //模板类
        ExcelImportTemplate<ImportTemplate> template;
        //初始化类的字节码对象
        Class c = ImportTemplate.class;
        //获取字节码对象中字段属性
        Field[] fields = new Field[0];
        try {
            fields = new Field[]{c.getDeclaredField("age"),c.getDeclaredField("name")};
            //导入Excel
            template = new ExcelImportTemplate<>(file, ImportTemplate.class, fields, this::checkParam);
            //从Excel模板中获取转换成的对象集合
            List<ImportTemplate> machineCodeList = template.getNewExcelList();



            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void checkParam(CallBackParam param) {

    }
}
