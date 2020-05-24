package excel;

@FunctionalInterface
public interface ICallback {

    /**
     * 执行回调
     *
     * @param param
     */
    void execute(CallBackParam param);
}
