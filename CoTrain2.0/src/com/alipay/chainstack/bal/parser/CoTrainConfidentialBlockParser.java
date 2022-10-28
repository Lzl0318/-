package com.alipay.chainstack.bal.parser;

import com.alipay.chainstack.jbcc.mychainx.model.block.BlockBodyModel;
import com.alipay.chainstack.jbcc.mychainx.model.block.BlockModel;
import com.alipay.chainstack.jbcc.mychainx.model.receipt.ReceiptModel;
import com.alipay.chainstack.jbcc.mychainx.model.transaction.TransactionModel;
import com.alipay.chainstack.jbcc.mychainx.model.transaction.TransactionType;
import com.alipay.chainstack.jbcc.mychainx.util.ConfidentialUtil;
import com.alipay.mychain.sdk.crypto.keypair.KeyTypeEnum;
import java.util.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class CoTrainConfidentialBlockParser extends CoTrainBlockParser {
    protected String rootKey;
    protected KeyTypeEnum keyType;
    protected BlockModel encryptedBlock;

    public CoTrainConfidentialBlockParser(
            BlockModel blockModel, String rootKey, KeyTypeEnum keyType) {
        this(blockModel, rootKey, keyType, null);
    }

    public CoTrainConfidentialBlockParser(
            BlockModel blockModel,
            String rootKey,
            KeyTypeEnum keyType,
            Set<String> contractFilterIds) {
        super();
        this.encryptedBlock = blockModel;
        this.rootKey = rootKey;
        this.keyType = keyType;
        if (contractFilterIds != null) {
            this.contractIds.addAll(
                    contractFilterIds.stream()
                            .map(this::parseContractId)
                            .collect(Collectors.toSet()));
        }
        processBlock(blockModel);
    }

    @Override
    protected void processBlock(BlockModel blockModel) {
        List<TransactionModel> transactionModels = new ArrayList<>();
        List<ReceiptModel> receiptModels = new ArrayList<>();

        BlockBodyModel blockBodyModel = new BlockBodyModel();
        blockBodyModel.setConsensusProof(blockBodyModel.getConsensusProof());
        blockBodyModel.setReceiptList(receiptModels);
        blockBodyModel.setTransactionList(transactionModels);

        BlockModel decryptedBlock = new BlockModel();
        decryptedBlock.setBlockHeader(blockModel.getBlockHeader());
        decryptedBlock.setBlockBody(blockBodyModel);

        for (int i = 0; i < blockModel.getBlockBody().getTransactionList().size(); i++) {
            TransactionModel transaction = blockModel.getBlockBody().getTransactionList().get(i);
            ReceiptModel receipt = blockModel.getBlockBody().getReceiptList().get(i);
            if (transaction.getTxType() == TransactionType.TX_CONFIDENTIAL) {
                try {
                    transaction =
                            ConfidentialUtil.decryptTransaction(transaction, keyType, rootKey);
                    receipt = ConfidentialUtil.decryptReceipt(receipt, rootKey);
                } catch (Exception e) {
                    // do nothing here, we just cannot decrypt these tx, just keep them as original
                    // tx
                }
            }
            transactionModels.add(transaction);
            receiptModels.add(receipt);
            if (isSkip(transaction)) {
                continue;
            }
            String method = getTransactionMethod(transaction);
            if (!StringUtils.isEmpty(method)) {
                transactionMap.computeIfAbsent(method, m -> new ArrayList<>());
                transactionMap.get(method).add(transaction);
                receiptMap.computeIfAbsent(method, m -> new ArrayList<>());
                receiptMap.get(method).add(receipt);
            }
        }
        this.blockModel = decryptedBlock;
    }

    @Override
    protected CoTrainTransactionParser getParser(TransactionModel transactionModel) {
        return new CoTrainConfidentialTransactionParser(transactionModel, rootKey, keyType);
    }

    @Override
    protected CoTrainTransactionParser getParser(ReceiptModel receipt) {
        return new CoTrainConfidentialTransactionParser(receipt, rootKey);
    }

    @Override
    protected CoTrainTransactionParser getParser(
            TransactionModel transactionModel, ReceiptModel receipt) {
        return new CoTrainConfidentialTransactionParser(
                transactionModel, receipt, rootKey, keyType);
    }

    public BlockModel getEncryptedBlock() {
        return encryptedBlock;
    }
}
