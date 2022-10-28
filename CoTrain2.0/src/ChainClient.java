import com.alipay.chainstack.bal.bao.CoTrainBAO;
import com.alipay.chainstack.bal.model.*;
import com.alipay.chainstack.jbcc.mychainx.domain.account.Account;
import com.alipay.chainstack.jbcc.mychainx.domain.account.AccountManager;
import com.alipay.chainstack.jbcc.mychainx.domain.account.Signer;
import com.alipay.chainstack.jbcc.mychainx.domain.blockchain.IBaseChainClient;
import com.alipay.chainstack.jbcc.mychainx.domain.contract.Contract;
import com.alipay.chainstack.jbcc.mychainx.domain.key.Key;
import com.alipay.chainstack.jbcc.mychainx.model.receipt.ReceiptModel;
import com.alipay.chainstack.jbcc.mychainx.util.KeyUtils;
import com.alipay.chainstack.providers.baasrest.chain.BaaSRestChainProvider;
import com.alipay.chainstack.providers.baasrest.constants.BaaSRestCipher;
import com.alipay.chainstack.providers.baasrest.option.ClientOption;
import com.alipay.mychain.sdk.common.VMTypeEnum;
import com.alipay.mychain.sdk.vm.WASMParameter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class ChainClient {
    static String accountName;
    static String accountKeyPath;
    static String accountPassword;
    static String contractName;
    static String contractCodePath;
    static String restUrl;
    static String accessId;
    static String accessSecretPath;
    static IBaseChainClient iBaseChainClient;
    static Contract contract;
    static CoTrainBAO coTrainBAO;
    static int retry = 100;


    // 设置Access信息
    public static void setRestInfo(String restUrl, String accessId, String accessSecretPath){
        ChainClient.restUrl = restUrl;
        ChainClient.accessId = accessId;
        ChainClient.accessSecretPath = accessSecretPath;
    }

    // 设置Account信息
    public static void setAccountInfo(String accountName, String accountKeyPath, String accountPassword){
        ChainClient.accountName = accountName;
        ChainClient.accountKeyPath = accountKeyPath;
        ChainClient.accountPassword = accountPassword;
    }

    // 设置Contract信息
    public static void setContractInfo(String contractName, String contractCodePath){
        ChainClient.contractName = contractName;
        ChainClient.contractCodePath = contractCodePath;
    }

    // 创建IBaseChainClient
    public static void setIBaseChainClient(){
        System.out.println("Set chain client ...");
        // 配置client信息
        ClientOption clientOption = new ClientOption();
        clientOption.setChainId("0b06e2744ad341538b7a3b3cc5ad9e57");
        clientOption.setCipher(BaaSRestCipher.ec);
        clientOption.setRestUrl(restUrl);

        clientOption.setAccessId(accessId);
        clientOption.setAccessSecretPath(accessSecretPath);

        clientOption.setDefaultAccount(accountName);
        clientOption.setDefaultAccountKeyPath(accountKeyPath);
        clientOption.setDefaultAccountKeyPassword(accountPassword);

        // 初始化并启动client
        iBaseChainClient = new BaaSRestChainProvider(clientOption);
        if (!iBaseChainClient.startUp()) {
            throw new RuntimeException("failed to connect to chain");
        }else{
            System.out.println("client startup!");
        }
        System.out.println("Set chain client end. \n");
    }

    // 设置Contract
    public static void setContract(){
        setIBaseChainClient();
        System.out.println("Set contract ...");

        Contract contract = new Contract();
        contract.setName(contractName);
        contract.setCodePath(contractCodePath);
        contract.setVmType(VMTypeEnum.WASM);
        contract.setClient(iBaseChainClient);
        contract.setAdminName(accountName);

        ChainClient.contract = contract;

        System.out.println("Set chain client end. \n");
    }

    // 设置CoTrainBAO
    public static void setCoTrainBAO() {
        setContract();
        System.out.println("Set BAO ...");
        coTrainBAO = new CoTrainBAO(contract);
        System.out.println("Set BAO end. \n");
    }

    // 部署合约
    public static void deployContract(){
        // 部署合约
        ReceiptModel receipt = contract.deploy(new WASMParameter("Init"), BigInteger.ZERO);
        if(receipt.getResult() != 0){
            System.out.println(receipt.getResult());
            System.out.println(new String(receipt.getOutput()));
            throw new RuntimeException("部署失败");
        }
        else{
            System.out.println("部署成功");
        }
    }

    // 从python读取本地模型
    public static String getLocalModelFromPython(int num) throws IOException {
//        double []array = new double[num];
//        for(int i=0; i<num; i++){
//            array[i] = Math.random()*10;
//        }
//        String arrStr = Arrays.toString(array);
//        String arrStr_ = arrStr.substring(1, arrStr.length()-1);
//        return arrStr_;

        File file = new File("temp/param"+TrainingClient.port+".txt");
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

    // 向python发送全局模型
    public static Boolean sendGlobalModelToPython(String globalModel){
        System.out.println("globalModel: " + globalModel);
        return true;
    }

    // 向合约添加协作者名单
    public static boolean addClientsWithRetry(){
        System.out.println("Add Client ...");

        // 配置初始化request
        AddClientRequest addClientRequest = new AddClientRequest();
        addClientRequest.getReqContext().setAccountName(accountName);

        for(int i=0; i<retry; i++){
            delayRandomSeconds();
            AddClientResponse addClientResponse = coTrainBAO.addClient(addClientRequest);
            if(addClientResponse.getRespContext().isSuccess()){
                System.out.println("Add clients end. \n");
                return true;
            }
            else{
                System.out.println("Add clients failed.");
                System.out.println("ErrorMsg: " + addClientResponse.getRespContext().getErrorMsg().toString());
                System.out.println();
                return false;
            }
        }
        return false;
    }

    // 上传本地模型
    public static boolean uploadWithRetry(String localModel){
        System.out.println("Upload ...");

        // 设置上传request
        UploadRequest uploadRequest = new UploadRequest();
        uploadRequest.setStr(localModel);
        uploadRequest.getReqContext().setAccountName(accountName);
        UploadResponse uploadResponse;

        for(int i=0; i<retry; i++){
            delayRandomSeconds();

            try{
                uploadResponse = coTrainBAO.upload(uploadRequest);
            }catch (RuntimeException err){
                System.out.println("RuntimeException");
                delayRandomSeconds();
                continue;
            }

            if(uploadResponse.getRespContext().isSuccess()){
                System.out.println("Upload end. \n");
                return true;
            }
            else{
                System.out.println("Upload failed.");
                String err = uploadResponse.getRespContext().getErrorMsg();
                System.out.println("ErrorMsg: " + err + "\n");
                if(err.equals("Status illegal.") || err.equals("had updated already.")){
                    break;
                }
            }
        }
        return false;
    }

    // 下载全局模型
    public static String downloadWithRetry(){
        System.out.println("download ...");

        // 设置下载request
        DownloadRequest downloadRequest = new DownloadRequest();
        downloadRequest.getReqContext().setAccountName(accountName);
        DownloadResponse downloadResponse;
        for(int i=0; i<retry; i++){
            delayRandomSeconds();

            try{
                downloadResponse = coTrainBAO.download(downloadRequest);
            }catch (RuntimeException err){
                System.out.println("RuntimeException");
                delayRandomSeconds();
                continue;
            }

            if(downloadResponse.getRespContext().isSuccess()){
                System.out.println("download end. \n");
                return downloadResponse.getResponse();
            }
            else{
                System.out.println("Download failed.");
                String err = downloadResponse.getRespContext().getErrorMsg();
                System.out.println("ErrorMsg: " + err + "\n");
                if(err.equals("Status illegal") || err.equals("had downdated already.")) {
                    break;
                }
            }
        }
        return "";
    }

    // 获取合约状态并等待
    public static void getStatusAndWait(String target){
        // 设置状态读取request
        GetStatusRequest getStatusRequest = new GetStatusRequest();
        getStatusRequest.getReqContext().setAccountName(accountName);
        GetStatusResponse getStatusResponse;

        for(int i=0; i<retry; i++){
            delayRandomSeconds();

            try{
                getStatusResponse = coTrainBAO.getStatus(getStatusRequest);
            }catch (RuntimeException err){
                System.out.println("RuntimeException");
                delayRandomSeconds();
                continue;
            }

            if(getStatusResponse.getRespContext().isSuccess()){
                if(getStatusResponse.getResponse().contains(target)){
                    return;
                }
                else{
                    System.out.println("Wait to " + target + " ...");
                }
            }
            else{
                System.out.println("Get statue failed.");
            }
        }
    }

    // 协作训练一轮次
    public static void coTrainRound() throws IOException {
        // 获取本地模型
        Data.action = "Training";
        TrainingClient.beginTrainModel();
        String localModel = TrainingClient.getLocalModel();

        // 等待状态为uploadTrainingClient
        Data.action = "Uploading";
        getStatusAndWait("upload");

        // 上传本地模型
        uploadWithRetry(localModel);

        // 等待状态为download
        Data.action = "Downloading";
        getStatusAndWait("download");

        // 下载全局模型
        String globalModel = downloadWithRetry();

        // 发送全局模型
        TrainingClient.sendGlobalModel(globalModel);
        TrainingClient.getTestResult();
    }

    public static void train() throws IOException{
        for(int i=0; i<Data.epochs; i++){
            System.out.println("Round " + i + "begin ... \n");
            Data.progress = i + 1;
            coTrainRound();
            System.out.println("Round" + i + " end. \n");
        }
    }

    // 延时函数
    public static void delayRandomSeconds(){
        int s = (int) (Math.random() * 15 - 5);
        delaySeconds(s);
    }

    public static void delaySeconds(int s){
        try{
            Thread.sleep(s*1000);
        }
        catch (Exception e) {
        }
    }

    public static void main(String[] args) throws IOException{
        String str = ChainClient.getLocalModelFromPython(10);
        System.out.println(str);
    }
}
