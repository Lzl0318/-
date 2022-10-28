
public class Main {
    public static void main(String[] args) throws Exception{
        int webPort = 8889;
        System.out.println("webPort: " + webPort);
        WebServer.port = webPort;
        WebServer.setHttpServer();
        WebServer.webServerStart();

        int trainingPort = 9999;
        System.out.println("trainingPort: " + trainingPort);
        TrainingClient.port = trainingPort;

        Data.accInit();

        while (true){
            if(Data.globalStatus.equals("CaTraining")){
                ChainClient.train();
                Data.globalStatus = "finished";
                Data.action = "Waiting";
            }
            ChainClient.delaySeconds(1);
        }
    }
}