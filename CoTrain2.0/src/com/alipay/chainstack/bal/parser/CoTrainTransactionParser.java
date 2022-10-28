package com.alipay.chainstack.bal.parser;

import com.alipay.chainstack.bal.model.*;
import com.alipay.chainstack.jbcc.mychainx.domain.parser.BaseTransactionParser;
import com.alipay.chainstack.jbcc.mychainx.model.log.LogDO;
import com.alipay.chainstack.jbcc.mychainx.model.receipt.ReceiptModel;
import com.alipay.chainstack.jbcc.mychainx.model.request.BaseBAORequest;
import com.alipay.chainstack.jbcc.mychainx.model.response.BaseBAOResponse;
import com.alipay.chainstack.jbcc.mychainx.model.transaction.TransactionModel;
import com.alipay.chainstack.jbcc.mychainx.model.transaction.TransactionType;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** CoTrain contract tx parser */
public class CoTrainTransactionParser extends BaseTransactionParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoTrainTransactionParser.class);

    protected CoTrainTransactionParser() {
        super();
    }

    public CoTrainTransactionParser(ReceiptModel receiptModel) {
        super(receiptModel);
    }

    public CoTrainTransactionParser(TransactionModel transactionModel) {
        super(transactionModel);
    }

    public CoTrainTransactionParser(TransactionModel transactionModel, ReceiptModel receiptModel) {
        super(transactionModel, receiptModel);
    }

    /**
     * check if the tx is Init request
     *
     * @return true if yes, false if not
     */
    public boolean isInitRequest() {
        return InitRequest.isInitRequest(transactionModel);
    }

    /**
     * check if the tx is Download request
     *
     * @return true if yes, false if not
     */
    public boolean isDownloadRequest() {
        return DownloadRequest.isDownloadRequest(transactionModel);
    }

    /**
     * check if the tx is Upload request
     *
     * @return true if yes, false if not
     */
    public boolean isUploadRequest() {
        return UploadRequest.isUploadRequest(transactionModel);
    }

    /**
     * check if the tx is AddClient request
     *
     * @return true if yes, false if not
     */
    public boolean isAddClientRequest() {
        return AddClientRequest.isAddClientRequest(transactionModel);
    }

    /**
     * check if the tx is GetStatus request
     *
     * @return true if yes, false if not
     */
    public boolean isGetStatusRequest() {
        return GetStatusRequest.isGetStatusRequest(transactionModel);
    }

    /**
     * decode tx into Init request
     *
     * @return Init request
     */
    public InitRequest parseInitRequest() {

        if (null == transactionModel) {
            return null;
        }

        return InitRequest.decodeFromTransaction(transactionModel);
    }

    /**
     * decode tx into Download request
     *
     * @return Download request
     */
    public DownloadRequest parseDownloadRequest() {

        if (null == transactionModel) {
            return null;
        }

        return DownloadRequest.decodeFromTransaction(transactionModel);
    }

    /**
     * decode tx into Upload request
     *
     * @return Upload request
     */
    public UploadRequest parseUploadRequest() {

        if (null == transactionModel) {
            return null;
        }

        return UploadRequest.decodeFromTransaction(transactionModel);
    }

    /**
     * decode tx into AddClient request
     *
     * @return AddClient request
     */
    public AddClientRequest parseAddClientRequest() {

        if (null == transactionModel) {
            return null;
        }

        return AddClientRequest.decodeFromTransaction(transactionModel);
    }

    /**
     * decode tx into GetStatus request
     *
     * @return GetStatus request
     */
    public GetStatusRequest parseGetStatusRequest() {

        if (null == transactionModel) {
            return null;
        }

        return GetStatusRequest.decodeFromTransaction(transactionModel);
    }

    /**
     * decode receipt into Init response
     *
     * @return Init response
     */
    public InitResponse parseInitResponse() {

        if (null == receiptModel) {
            return null;
        }

        return InitResponse.decodeFromReceipt(receiptModel);
    }

    /**
     * decode receipt into Download response
     *
     * @return Download response
     */
    public DownloadResponse parseDownloadResponse() {

        if (null == receiptModel) {
            return null;
        }

        return DownloadResponse.decodeFromReceipt(receiptModel);
    }

    /**
     * decode receipt into Upload response
     *
     * @return Upload response
     */
    public UploadResponse parseUploadResponse() {

        if (null == receiptModel) {
            return null;
        }

        return UploadResponse.decodeFromReceipt(receiptModel);
    }

    /**
     * decode receipt into AddClient response
     *
     * @return AddClient response
     */
    public AddClientResponse parseAddClientResponse() {

        if (null == receiptModel) {
            return null;
        }

        return AddClientResponse.decodeFromReceipt(receiptModel);
    }

    /**
     * decode receipt into GetStatus response
     *
     * @return GetStatus response
     */
    public GetStatusResponse parseGetStatusResponse() {

        if (null == receiptModel) {
            return null;
        }

        return GetStatusResponse.decodeFromReceipt(receiptModel);
    }

    /**
     * decode tx into bao request
     *
     * @return bao request
     */
    @Override
    public BaseBAORequest parseRequest() {
        if (transactionModel == null
                || !TransactionType.TX_CALL_CONTRACT.equals(transactionModel.getTxType())) {
            return null;
        }

        if (isInitRequest()) {
            return parseInitRequest();
        }

        if (isDownloadRequest()) {
            return parseDownloadRequest();
        }

        if (isUploadRequest()) {
            return parseUploadRequest();
        }

        if (isAddClientRequest()) {
            return parseAddClientRequest();
        }

        if (isGetStatusRequest()) {
            return parseGetStatusRequest();
        }

        return null;
    }

    /**
     * decode tx into bao response
     *
     * @return bao response
     */
    @Override
    public BaseBAOResponse parseResponse() {
        if (transactionModel == null
                || !TransactionType.TX_CALL_CONTRACT.equals(transactionModel.getTxType())
                || receiptModel == null) {
            return null;
        }

        if (isInitRequest()) {
            return parseInitResponse();
        }

        if (isDownloadRequest()) {
            return parseDownloadResponse();
        }

        if (isUploadRequest()) {
            return parseUploadResponse();
        }

        if (isAddClientRequest()) {
            return parseAddClientResponse();
        }

        if (isGetStatusRequest()) {
            return parseGetStatusResponse();
        }

        return null;
    }

    /**
     * get all CoTrain logs from the tx
     *
     * @return all CoTrain logs
     */
    public List<LogDO> getAllCoTrainLogs() {
        List<LogDO> logDOS = new ArrayList<>();

        return logDOS;
    }
}
