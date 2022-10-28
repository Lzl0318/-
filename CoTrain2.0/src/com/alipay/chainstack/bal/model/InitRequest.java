package com.alipay.chainstack.bal.model;

import com.alipay.chainstack.jbcc.mychainx.domain.account.Account;
import com.alipay.chainstack.jbcc.mychainx.domain.account.AccountManager;
import com.alipay.chainstack.jbcc.mychainx.model.account.NamedIdentity;
import com.alipay.chainstack.jbcc.mychainx.model.request.BaseBAORequest;
import com.alipay.chainstack.jbcc.mychainx.model.transaction.TransactionModel;
import com.alipay.chainstack.jbcc.mychainx.model.transaction.TransactionType;
import com.alipay.mychain.sdk.common.Parameters;
import com.alipay.mychain.sdk.vm.WASMOutput;
import com.alipay.mychain.sdk.vm.WASMParameter;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

public class InitRequest extends BaseBAORequest {

    public static String METHOD_SIGNATURE = "Init";

    public InitRequest() {
        getReqContext().setMethodSig(METHOD_SIGNATURE);
    }

    @Override
    public Parameters getParameters() {

        WASMParameter wasmParameter = new WASMParameter(getReqContext().getMethodSig());

        return wasmParameter;
    }

    public static boolean isInitRequest(TransactionModel transactionModel) {
        if (transactionModel == null
                || transactionModel.getData() == null
                || !TransactionType.TX_CALL_CONTRACT.equals(transactionModel.getTxType())) {
            return false;
        }
        WASMOutput output = new WASMOutput(Hex.encodeHexString(transactionModel.getData()));
        String methodSig = getTransactionMethod(transactionModel, output);
        return StringUtils.equals(methodSig, InitRequest.METHOD_SIGNATURE);
    }

    public static InitRequest decodeFromTransaction(TransactionModel transactionModel) {

        InitRequest initRequest = new InitRequest();

        WASMOutput output = new WASMOutput(Hex.encodeHexString(transactionModel.getData()));
        String methodSig = getTransactionMethod(transactionModel, output);

        if (!StringUtils.equals(methodSig, InitRequest.METHOD_SIGNATURE)) {
            return null;
        }

        initRequest.getReqContext().setTxHash(transactionModel.getHash());

        initRequest.getReqContext().setAccountNameHex(transactionModel.getFrom().hexStrValue());
        initRequest.getReqContext().setContractNameHex(transactionModel.getTo().hexStrValue());

        Account requestAccount =
                AccountManager.getInstance()
                        .getAccountByNameHex(transactionModel.getFrom().hexStrValue());
        if (null != requestAccount) {
            initRequest.getReqContext().setAccount(new NamedIdentity(requestAccount.getName()));
        }

        return initRequest;
    }

    @Override
    public String toString() {
        return "InitRequest{\n" + "  reqContext=" + getReqContext() + "\n" + '}';
    }
}
