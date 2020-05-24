package excel;

import lombok.Data;

import java.io.Serializable;

/**
 * @PackageName: excel
 * @ClassName: CallBackParam
 * @Description:
 * @author: 熊杰
 * @data：20202020/5/2416:05
 */
@Data
public class CallBackParam implements Serializable {
    private String apiName;
    private transient Object input;

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public void setOutput(Object output) {
        this.output = output;
    }

    public String getApiName() {
        return apiName;
    }

    public Object getInput() {
        return input;
    }

    public Object getOutput() {
        return output;
    }

    private transient Object output;

    public CallBackParam(Object input, Object output) {
        this.input = input;
        this.output = output;
    }

    public CallBackParam(String apiName,Object input) {
        this.input = input;
        this.apiName = apiName;
    }

    public CallBackParam() {
    }

    /**
     * 构建成功的结果
     */
    public void buildSuccessModelResult() {
        this.setOutput(true);
    }

    /**
     * 构建无效的结果
     */
    public void buildUselessModelResult() {
        this.setOutput("");
    }

    /**
     * 构建失败的结果
     */
    public void buildFailModelResult(String msg) {
        this.setOutput(false);
    }
}
