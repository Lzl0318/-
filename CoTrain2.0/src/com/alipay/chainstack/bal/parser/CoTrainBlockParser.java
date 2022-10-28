package com.alipay.chainstack.bal.parser;

import com.alipay.chainstack.bal.model.*;
import com.alipay.chainstack.jbcc.mychainx.domain.parser.BaseBlockParser;
import com.alipay.chainstack.jbcc.mychainx.model.block.BlockModel;
import com.alipay.chainstack.jbcc.mychainx.model.log.LogDO;
import com.alipay.chainstack.jbcc.mychainx.model.receipt.ReceiptModel;
import com.alipay.chainstack.jbcc.mychainx.model.request.BaseBAORequest;
import com.alipay.chainstack.jbcc.mychainx.model.response.BaseBAOResponse;
import com.alipay.chainstack.jbcc.mychainx.model.transaction.TransactionModel;
import java.util.*;

/** CoTrain contract block parser */
public class CoTrainBlockParser extends BaseBlockParser {

    protected CoTrainBlockParser() {
        super();
    }

    public CoTrainBlockParser(BlockModel blockModel) {
        super(blockModel, null);
    }

    public CoTrainBlockParser(BlockModel blockModel, Set<String> contractFilterIds) {
        super(blockModel, contractFilterIds);
    }

    protected CoTrainTransactionParser getParser(TransactionModel transactionModel) {
        return new CoTrainTransactionParser(transactionModel);
    }

    protected CoTrainTransactionParser getParser(ReceiptModel receipt) {
        return new CoTrainTransactionParser(receipt);
    }

    protected CoTrainTransactionParser getParser(
            TransactionModel transactionModel, ReceiptModel receipt) {
        return new CoTrainTransactionParser(transactionModel, receipt);
    }

    /**
     * check whether block contains Init requests
     *
     * @return true if have, false if not
     */
    public boolean hasInitRequest() {
        return hasRequest(InitRequest.METHOD_SIGNATURE);
    }

    /**
     * check whether block contains Download requests
     *
     * @return true if have, false if not
     */
    public boolean hasDownloadRequest() {
        return hasRequest(DownloadRequest.METHOD_SIGNATURE);
    }

    /**
     * check whether block contains Upload requests
     *
     * @return true if have, false if not
     */
    public boolean hasUploadRequest() {
        return hasRequest(UploadRequest.METHOD_SIGNATURE);
    }

    /**
     * check whether block contains AddClient requests
     *
     * @return true if have, false if not
     */
    public boolean hasAddClientRequest() {
        return hasRequest(AddClientRequest.METHOD_SIGNATURE);
    }

    /**
     * check whether block contains GetStatus requests
     *
     * @return true if have, false if not
     */
    public boolean hasGetStatusRequest() {
        return hasRequest(GetStatusRequest.METHOD_SIGNATURE);
    }

    /**
     * get all Init interface requests from block
     *
     * @return all Init interface requests in the block
     */
    public List<InitRequest> getInitRequests() {
        if (!hasInitRequest()) {
            return null;
        }

        List<InitRequest> initRequests = new ArrayList<>();

        for (TransactionModel transaction : transactionMap.get(InitRequest.METHOD_SIGNATURE)) {
            CoTrainTransactionParser coTrainTransactionParser = getParser(transaction);
            initRequests.add(coTrainTransactionParser.parseInitRequest());
        }

        return initRequests;
    }

    /**
     * get all Download interface requests from block
     *
     * @return all Download interface requests in the block
     */
    public List<DownloadRequest> getDownloadRequests() {
        if (!hasDownloadRequest()) {
            return null;
        }

        List<DownloadRequest> downloadRequests = new ArrayList<>();

        for (TransactionModel transaction : transactionMap.get(DownloadRequest.METHOD_SIGNATURE)) {
            CoTrainTransactionParser coTrainTransactionParser = getParser(transaction);
            downloadRequests.add(coTrainTransactionParser.parseDownloadRequest());
        }

        return downloadRequests;
    }

    /**
     * get all Upload interface requests from block
     *
     * @return all Upload interface requests in the block
     */
    public List<UploadRequest> getUploadRequests() {
        if (!hasUploadRequest()) {
            return null;
        }

        List<UploadRequest> uploadRequests = new ArrayList<>();

        for (TransactionModel transaction : transactionMap.get(UploadRequest.METHOD_SIGNATURE)) {
            CoTrainTransactionParser coTrainTransactionParser = getParser(transaction);
            uploadRequests.add(coTrainTransactionParser.parseUploadRequest());
        }

        return uploadRequests;
    }

    /**
     * get all AddClient interface requests from block
     *
     * @return all AddClient interface requests in the block
     */
    public List<AddClientRequest> getAddClientRequests() {
        if (!hasAddClientRequest()) {
            return null;
        }

        List<AddClientRequest> addClientRequests = new ArrayList<>();

        for (TransactionModel transaction : transactionMap.get(AddClientRequest.METHOD_SIGNATURE)) {
            CoTrainTransactionParser coTrainTransactionParser = getParser(transaction);
            addClientRequests.add(coTrainTransactionParser.parseAddClientRequest());
        }

        return addClientRequests;
    }

    /**
     * get all GetStatus interface requests from block
     *
     * @return all GetStatus interface requests in the block
     */
    public List<GetStatusRequest> getGetStatusRequests() {
        if (!hasGetStatusRequest()) {
            return null;
        }

        List<GetStatusRequest> getStatusRequests = new ArrayList<>();

        for (TransactionModel transaction : transactionMap.get(GetStatusRequest.METHOD_SIGNATURE)) {
            CoTrainTransactionParser coTrainTransactionParser = getParser(transaction);
            getStatusRequests.add(coTrainTransactionParser.parseGetStatusRequest());
        }

        return getStatusRequests;
    }

    /**
     * get all bao requests of current contract from block
     *
     * @return bao requests list
     */
    @Override
    public List<BaseBAORequest> getAllRequests() {
        if (blockModel == null
                || blockModel.getBlockBody() == null
                || blockModel.getBlockBody().getTransactionList() == null) {
            return null;
        }

        List<TransactionModel> txs = blockModel.getBlockBody().getTransactionList();
        List<BaseBAORequest> baoRequests = new ArrayList<>();

        for (TransactionModel tx : txs) {
            if (isSkip(tx)) {
                continue;
            }
            CoTrainTransactionParser coTrainTransactionParser = getParser(tx);
            BaseBAORequest req = coTrainTransactionParser.parseRequest();
            if (req != null) {
                baoRequests.add(req);
            }
        }

        return baoRequests;
    }

    /**
     * et all bao response of current contract from block
     *
     * @return bao response list
     */
    @Override
    public List<BaseBAOResponse> getAllResponses() {
        if (blockModel == null
                || blockModel.getBlockBody() == null
                || blockModel.getBlockBody().getTransactionList() == null) {
            return null;
        }

        List<TransactionModel> txs = blockModel.getBlockBody().getTransactionList();
        List<ReceiptModel> receipts = blockModel.getBlockBody().getReceiptList();
        List<BaseBAOResponse> baoResponses = new ArrayList<>();

        for (int i = 0; i < txs.size(); ++i) {
            TransactionModel tx = txs.get(i);
            if (isSkip(tx)) {
                continue;
            }
            ReceiptModel receipt = receipts.get(i);
            CoTrainTransactionParser coTrainTransactionParser = getParser(tx, receipt);
            BaseBAOResponse resp = coTrainTransactionParser.parseResponse();
            if (resp != null) {
                baoResponses.add(resp);
            }
        }

        return baoResponses;
    }

    /**
     * get all Init interface response in the block
     *
     * @return Init interface responses
     */
    public List<InitResponse> getInitResponses() {
        if (!hasInitRequest()) {
            return null;
        }

        List<InitResponse> initResponses = new ArrayList<>();

        for (int i = 0; i < transactionMap.get(InitRequest.METHOD_SIGNATURE).size(); ++i) {

            CoTrainTransactionParser coTrainTransactionParser =
                    getParser(
                            transactionMap.get(InitRequest.METHOD_SIGNATURE).get(i),
                            receiptMap.get(InitRequest.METHOD_SIGNATURE).get(i));

            initResponses.add(coTrainTransactionParser.parseInitResponse());
        }

        return initResponses;
    }

    /**
     * get all Download interface response in the block
     *
     * @return Download interface responses
     */
    public List<DownloadResponse> getDownloadResponses() {
        if (!hasDownloadRequest()) {
            return null;
        }

        List<DownloadResponse> downloadResponses = new ArrayList<>();

        for (int i = 0; i < transactionMap.get(DownloadRequest.METHOD_SIGNATURE).size(); ++i) {

            CoTrainTransactionParser coTrainTransactionParser =
                    getParser(
                            transactionMap.get(DownloadRequest.METHOD_SIGNATURE).get(i),
                            receiptMap.get(DownloadRequest.METHOD_SIGNATURE).get(i));

            downloadResponses.add(coTrainTransactionParser.parseDownloadResponse());
        }

        return downloadResponses;
    }

    /**
     * get all Upload interface response in the block
     *
     * @return Upload interface responses
     */
    public List<UploadResponse> getUploadResponses() {
        if (!hasUploadRequest()) {
            return null;
        }

        List<UploadResponse> uploadResponses = new ArrayList<>();

        for (int i = 0; i < transactionMap.get(UploadRequest.METHOD_SIGNATURE).size(); ++i) {

            CoTrainTransactionParser coTrainTransactionParser =
                    getParser(
                            transactionMap.get(UploadRequest.METHOD_SIGNATURE).get(i),
                            receiptMap.get(UploadRequest.METHOD_SIGNATURE).get(i));

            uploadResponses.add(coTrainTransactionParser.parseUploadResponse());
        }

        return uploadResponses;
    }

    /**
     * get all AddClient interface response in the block
     *
     * @return AddClient interface responses
     */
    public List<AddClientResponse> getAddClientResponses() {
        if (!hasAddClientRequest()) {
            return null;
        }

        List<AddClientResponse> addClientResponses = new ArrayList<>();

        for (int i = 0; i < transactionMap.get(AddClientRequest.METHOD_SIGNATURE).size(); ++i) {

            CoTrainTransactionParser coTrainTransactionParser =
                    getParser(
                            transactionMap.get(AddClientRequest.METHOD_SIGNATURE).get(i),
                            receiptMap.get(AddClientRequest.METHOD_SIGNATURE).get(i));

            addClientResponses.add(coTrainTransactionParser.parseAddClientResponse());
        }

        return addClientResponses;
    }

    /**
     * get all GetStatus interface response in the block
     *
     * @return GetStatus interface responses
     */
    public List<GetStatusResponse> getGetStatusResponses() {
        if (!hasGetStatusRequest()) {
            return null;
        }

        List<GetStatusResponse> getStatusResponses = new ArrayList<>();

        for (int i = 0; i < transactionMap.get(GetStatusRequest.METHOD_SIGNATURE).size(); ++i) {

            CoTrainTransactionParser coTrainTransactionParser =
                    getParser(
                            transactionMap.get(GetStatusRequest.METHOD_SIGNATURE).get(i),
                            receiptMap.get(GetStatusRequest.METHOD_SIGNATURE).get(i));

            getStatusResponses.add(coTrainTransactionParser.parseGetStatusResponse());
        }

        return getStatusResponses;
    }

    /**
     * get all CoTrain logs in the block
     *
     * @return all CoTrain logs
     */
    public List<LogDO> getAllCoTrainLogs() {
        List<LogDO> logDOS = new ArrayList<>();

        for (int i = 0; i < blockModel.getBlockBody().getReceiptList().size(); i++) {
            TransactionModel transaction = blockModel.getBlockBody().getTransactionList().get(i);
            if (isSkip(transaction)) {
                continue;
            }
            ReceiptModel receipt = blockModel.getBlockBody().getReceiptList().get(i);

            CoTrainTransactionParser coTrainTransactionParser = getParser(transaction, receipt);

            logDOS.addAll(coTrainTransactionParser.getAllCoTrainLogs());
        }

        return logDOS;
    }
}
