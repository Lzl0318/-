import java.io.*;

public class TrainingClient {

    static int port;

    public static String getLocalModel() throws IOException {
        HttpRequest httpRequest = new HttpRequest();
        String content = httpRequest.sendPost("http://localhost:"+port+"/getLocalModel", "address=../temp/param"+port+".txt");
        System.out.println(content);

        File file = new File("temp/param"+port+".txt");
        FileInputStream fileReader = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fileReader,"utf-8"));
        StringBuffer sb = new StringBuffer();
        String str;
        while((str=br.readLine()) != null ){
            sb.append(str).append(" ");
        }
        br.close();
        String param = sb.toString();
        String param_ = param.substring(0, param.length()-1);
        return param_;
    }

    public static void sendGlobalModel(String param) throws IOException {
        File file = new File("temp/param"+port+".txt");
        FileWriter fw =new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        String [] spString = param.split("\\s+");
        for(String ss : spString){
            bw.write(ss);
            bw.newLine();
            bw.flush();
        }
        bw.flush();
        bw.close();
        System.out.println("客户端将聚合后的模型参数保存在本地目录 temp/param+port+.txt");

        HttpRequest httpRequest = new HttpRequest();
        String content = httpRequest.sendPost("http://localhost:"+port+"/sendGlobalModel", "address=../temp/param"+port+".txt");
        System.out.println(content);
    }

    public static void beginTrainModel() {
        System.out.println("请服务器开启模型训练");
        HttpRequest httpRequest = new HttpRequest();
        String content = httpRequest.sendPost("http://localhost:"+port+"/beginTrainModel", null);
        System.out.println(content);
    }

    public static void getTestResult() {
        HttpRequest httpRequest = new HttpRequest();
        String content = httpRequest.sendPost("http://localhost:"+port+"/getTestResult", null);

        Data.acc[Data.progress - 1] = Double.parseDouble(content);
    }

    public static void main(String[] args) throws IOException {
        TrainingClient.port = 9997;
        String param = TrainingClient.getLocalModel();
        TrainingClient.sendGlobalModel(param);
        TrainingClient.beginTrainModel();
        TrainingClient.getTestResult();

        param = TrainingClient.getLocalModel();
        TrainingClient.sendGlobalModel(param);
        TrainingClient.beginTrainModel();
        TrainingClient.getTestResult();
    }
}
