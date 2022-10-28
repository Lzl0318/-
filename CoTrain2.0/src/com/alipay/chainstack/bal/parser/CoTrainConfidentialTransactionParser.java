package com.alipay.chainstack.bal.parser;

import com.alipay.chainstack.jbcc.mychainx.model.receipt.ReceiptModel;
import com.alipay.chainstack.jbcc.mychainx.model.transaction.TransactionModel;
import com.alipay.chainstack.jbcc.mychainx.model.transaction.TransactionType;
import com.alipay.chainstack.jbcc.mychainx.util.ConfidentialUtil;
import com.alipay.mychain.sdk.crypto.keypair.KeyTypeEnum;

public class CoTrainConfidentialTransactionParser extends CoTrainTransactionParser {

    public CoTrainConfidentialTransactionParser(ReceiptModel receiptModel, String decryptionKey) {
        super();
        this.receiptModel = ConfidentialUtil.decryptReceipt(receiptModel, decryptionKey);
    }

    public CoTrainConfidentialTransactionParser(
            TransactionModel transactionModel, String decryptionKey, KeyTypeEnum sealPubKeyType) {
        super();
        this.transactionModel =
                transactionModel.getTxType() == TransactionType.TX_CONFIDENTIAL
                        ? ConfidentialUtil.decryptTransaction(
                                transactionModel, sealPubKeyType, decryptionKey)
                        : transactionModel;
    }

    public CoTrainConfidentialTransactionParser(
            TransactionModel transactionModel,
            ReceiptModel receiptModel,
            String decryptionKey,
            KeyTypeEnum sealPubKeyType) {
        super();
        this.transactionModel =
                transactionModel.getTxType() == TransactionType.TX_CONFIDENTIAL
                        ? ConfidentialUtil.decryptTransaction(
                                transactionModel, sealPubKeyType, decryptionKey)
                        : transactionModel;
        this.receiptModel =
                transactionModel.getTxType() == TransactionType.TX_CONFIDENTIAL
                        ? ConfidentialUtil.decryptReceipt(receiptModel, decryptionKey)
                        : receiptModel;
    }
}
