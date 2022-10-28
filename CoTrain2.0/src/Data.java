public class Data {
    static String globalStatus = "Ready";   // "CaTraining", "finished"
    static String action = "Waiting";       // "Uploading", "Downloading", "Training"
    static int progress = 0;                // 当前轮次

    static int epochs = 10;
    static double[] acc = new double[epochs+1];

    static void accInit(){
        for(int i=0; i< acc.length; i++){
            acc[i] = -1;
        }
    }

}
