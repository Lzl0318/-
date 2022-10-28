package com.alipay.chainstack.bal.bao;

import com.alipay.chainstack.bal.model.*;
import com.alipay.chainstack.jbcc.mychainx.domain.bao.BaseBAO;
import com.alipay.chainstack.jbcc.mychainx.domain.contract.Contract;
import com.alipay.chainstack.jbcc.mychainx.model.receipt.ReceiptModel;

/** CoTrain contract access object */
public class CoTrainBAO extends BaseBAO {

    public CoTrainBAO() {
        super();
    }

    public CoTrainBAO(Contract contract) {
        super(contract);
    }

    public InitResponse init(InitRequest initRequest) {
        initRequest.getReqContext().setLocalCall(false);
        ReceiptModel receipt = processTransaction(initRequest);
        return InitResponse.decodeFromReceipt(receipt);
    }

    public InitResponse localInit(InitRequest initRequest) {
        initRequest.getReqContext().setLocalCall(true);
        ReceiptModel receipt = processTransaction(initRequest);
        return InitResponse.decodeFromReceipt(receipt);
    }

    public DownloadResponse download(DownloadRequest downloadRequest) {
        downloadRequest.getReqContext().setLocalCall(false);
        ReceiptModel receipt = processTransaction(downloadRequest);
        return DownloadResponse.decodeFromReceipt(receipt);
    }

    public DownloadResponse localDownload(DownloadRequest downloadRequest) {
        downloadRequest.getReqContext().setLocalCall(true);
        ReceiptModel receipt = processTransaction(downloadRequest);
        return DownloadResponse.decodeFromReceipt(receipt);
    }

    public UploadResponse upload(UploadRequest uploadRequest) {
        uploadRequest.getReqContext().setLocalCall(false);
        ReceiptModel receipt = processTransaction(uploadRequest);
        return UploadResponse.decodeFromReceipt(receipt);
    }

    public UploadResponse localUpload(UploadRequest uploadRequest) {
        uploadRequest.getReqContext().setLocalCall(true);
        ReceiptModel receipt = processTransaction(uploadRequest);
        return UploadResponse.decodeFromReceipt(receipt);
    }

    public AddClientResponse addClient(AddClientRequest addClientRequest) {
        addClientRequest.getReqContext().setLocalCall(false);
        ReceiptModel receipt = processTransaction(addClientRequest);
        return AddClientResponse.decodeFromReceipt(receipt);
    }

    public AddClientResponse localAddClient(AddClientRequest addClientRequest) {
        addClientRequest.getReqContext().setLocalCall(true);
        ReceiptModel receipt = processTransaction(addClientRequest);
        return AddClientResponse.decodeFromReceipt(receipt);
    }

    public GetStatusResponse getStatus(GetStatusRequest getStatusRequest) {
        getStatusRequest.getReqContext().setLocalCall(false);
        ReceiptModel receipt = processTransaction(getStatusRequest);
        return GetStatusResponse.decodeFromReceipt(receipt);
    }

    public GetStatusResponse localGetStatus(GetStatusRequest getStatusRequest) {
        getStatusRequest.getReqContext().setLocalCall(true);
        ReceiptModel receipt = processTransaction(getStatusRequest);
        return GetStatusResponse.decodeFromReceipt(receipt);
    }
}
