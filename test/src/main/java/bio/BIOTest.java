package bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @PackageName: bio
 * @ClassName: BIOTest
 * @Description:
 * @author: 熊杰
 * @data：20202020/3/3016:21
 */
public class BIOTest {
    public static void main(String[] args) throws Exception {
        //建立线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        //创建套接字端口6666
        ServerSocket serverSocket = new ServerSocket(6666);
        while (true){
            //从队列中取出连接请求
            Socket accept = serverSocket.accept();
            executorService.execute(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }
    public static void handler(Socket socket){

    }
}
