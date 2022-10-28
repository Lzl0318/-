import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class WebServer {
    static HttpServer httpServer;
    static int port;

    public static void setHttpServer() throws Exception {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);

        // 静态文件服务
        httpServer.createContext("/lib", new StaticFileServer());
        httpServer.createContext("/css", new StaticFileServer());
        httpServer.createContext("/js", new StaticFileServer());
        httpServer.createContext("/img", new StaticFileServer());
        httpServer.createContext("/scss", new StaticFileServer());

        httpServer.createContext("/myInit", new PageServer());
        httpServer.createContext("/myCoTrain", new PageServer());
        httpServer.createContext("/myDebug", new PageServer());
        httpServer.createContext("/myInfo", new PageServer());
        // 请求处理
        httpServer.createContext("/home", new HomeHandler());

        httpServer.createContext("/accountSetting", new accountSettingHandler());
        httpServer.createContext("/contractSetting", new contractSettingHandler());
        httpServer.createContext("/restSetting", new restSettingHandler());
        httpServer.createContext("/initUsers", new initUsersHandler());
        httpServer.createContext("/startTraining", new startTrainingHandler());
        httpServer.createContext("/updateData", new updateDataHandler());
    }

    public static void webServerStart(){
        httpServer.start();
    }

    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0],"");
            }
        }
        return result;
    }

    static class HomeHandler implements HttpHandler{
        private byte[] data;

        @Override
        public void handle(HttpExchange exchange) throws IOException{
            try(InputStream in = this.getClass().getResourceAsStream("html/myInit.html")){
                this.data = new byte[in.available()];
                in.read(data);
            }catch (Exception e){
                throw  new RuntimeException(e);
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, 0);

            OutputStream os = exchange.getResponseBody();
            os.write(data);
            os.close();
        }
    }

    static class PageServer implements HttpHandler{
        private byte[] data;

        @Override
        public void handle(HttpExchange exchange) throws IOException{
            String fileId = exchange.getRequestURI().getPath();
            try(InputStream in = this.getClass().getResourceAsStream("html"+fileId+".html")){
                this.data = new byte[in.available()];
                in.read(data);
            }catch (Exception e){
                throw  new RuntimeException(e);
            }

            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(data);
            os.close();
        }
    }

    static class StaticFileServer implements HttpHandler{
        private byte[] data;

        @Override
        public void handle(HttpExchange exchange) throws IOException{
            String fileId = exchange.getRequestURI().getPath();
            try(InputStream in = this.getClass().getResourceAsStream("html"+fileId)){
                this.data = new byte[in.available()];
                in.read(data);
            }catch (Exception e){
                throw  new RuntimeException(e);
            }

            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(data);
            os.close();
        }
    }

    static class accountSettingHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException{

            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            System.out.println("param accountName=" + params.get("accountName"));
            System.out.println("param accountKeyFilePath=" + params.get("accountKeyFilePath"));
            System.out.println("param accountKeyPassword=" + params.get("accountKeyPassword"));

            // 设置账户参数
            ChainClient.setAccountInfo(params.get("accountName"),
                    params.get("accountKeyFilePath"),
                    params.get("accountKeyPassword"));

            String response = "1";
            byte[] strByte = response.getBytes();
            exchange.sendResponseHeaders(200, strByte.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class contractSettingHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException{

            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            System.out.println("param contractName=" + params.get("contractName"));
            System.out.println("param contractCodeFilePath=" + params.get("contractCodeFilePath"));
            System.out.println("param contractCallOrDeploy=" + params.get("contractCallOrDeploy"));

            // 设置合约信息
            ChainClient.setContractInfo(params.get("contractName"), params.get("contractCodeFilePath"));
            // 初始化BAO
            ChainClient.setCoTrainBAO();
            // 部署合约
            if(params.get("contractCallOrDeploy").equals("deploy")){
                ChainClient.deployContract();
            }

            String response = "1";
            byte[] strByte = response.getBytes();
            exchange.sendResponseHeaders(200, strByte.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class restSettingHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException{

            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            System.out.println("param restUrl=" + params.get("restUrl"));
            System.out.println("param accessId=" + params.get("accessId"));
            System.out.println("param accessSecretPath=" + params.get("accessSecretPath"));

            // 设置rest参数
            ChainClient.setRestInfo(params.get("restUrl"),
                    params.get("accessId"),
                    params.get("accessSecretPath"));

            String response = "1";
            byte[] strByte = response.getBytes();
            exchange.sendResponseHeaders(200, strByte.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class initUsersHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException{
            String request_method = exchange.getRequestMethod();

            System.out.println("request_method: " + request_method);
            System.out.println(exchange.getRequestURI().getQuery());
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            String[] userList = params.get("users").split(",");
            for(int i=0; i<userList.length; i++){
                System.out.println(userList[i]);
            }

            String response = "1";
            if(ChainClient.addClientsWithRetry()){
                response = "1";
            }
            else {
                response = "0";
            }

            byte[] strByte = response.getBytes();
            exchange.sendResponseHeaders(200, strByte.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class startTrainingHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException{

            System.out.println("startTrainingHandler");

            Data.globalStatus = "CaTraining";
            Data.action = "Training";

            String response = "ok";
            byte[] strByte = response.getBytes();
            exchange.sendResponseHeaders(200, strByte.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class updateDataHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException{
            System.out.println("supdateDataHandler");

            String response = Data.globalStatus + "," + Data.action + "," + Data.progress + "," + Data.epochs + "," + 0.0;

            for(int i=0; i<=Data.epochs; i++){
                if(Data.acc[i] >= 0){
                    response = response + " " + Data.acc[i] ;
                }
                else{
                    break;
                }
            }

            byte[] strByte = response.getBytes();
            exchange.sendResponseHeaders(200, strByte.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static void main(String[] args) throws Exception{
        WebServer.port = 8888;
        WebServer.setHttpServer();
        WebServer.webServerStart();
    }
}
