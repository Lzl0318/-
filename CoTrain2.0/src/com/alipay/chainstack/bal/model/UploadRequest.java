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

public class UploadRequest extends BaseBAORequest {

    public static String METHOD_SIGNATURE = "upload";

    private String str;

    public UploadRequest() {
        getReqContext().setMethodSig(METHOD_SIGNATURE);
    }

    @Override
    public Parameters getParameters() {

        WASMParameter wasmParameter = new WASMParameter(getReqContext().getMethodSig());

        wasmParameter.addString(this.str);

        return wasmParameter;
    }

    public static boolean isUploadRequest(TransactionModel transactionModel) {
        if (transactionModel == null
                || transactionModel.getData() == null
                || !TransactionType.TX_CALL_CONTRACT.equals(transactionModel.getTxType())) {
            return false;
        }
        WASMOutput output = new WASMOutput(Hex.encodeHexString(transactionModel.getData()));
        String methodSig = getTransactionMethod(transactionModel, output);
        return StringUtils.equals(methodSig, UploadRequest.METHOD_SIGNATURE);
    }

    public static UploadRequest decodeFromTransaction(TransactionModel transactionModel) {

        UploadRequest uploadRequest = new UploadRequest();

        WASMOutput output = new WASMOutput(Hex.encodeHexString(transactionModel.getData()));
        String methodSig = getTransactionMethod(transactionModel, output);
        WASMOutput params = new WASMOutput(Hex.encodeHexString(output.getBytes()));

        if (!StringUtils.equals(methodSig, UploadRequest.METHOD_SIGNATURE)) {
            return null;
        }

        uploadRequest.setStr(params.getString());

        uploadRequest.getReqContext().setTxHash(transactionModel.getHash());

        uploadRequest.getReqContext().setAccountNameHex(transactionModel.getFrom().hexStrValue());
        uploadRequest.getReqContext().setContractNameHex(transactionModel.getTo().hexStrValue());

        Account requestAccount =
                AccountManager.getInstance()
                        .getAccountByNameHex(transactionModel.getFrom().hexStrValue());
        if (null != requestAccount) {
            uploadRequest.getReqContext().setAccount(new NamedIdentity(requestAccount.getName()));
        }

        return uploadRequest;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return "UploadRequest{\n" + "  reqContext=" + getReqContext() + "\n" + "  str=" + str + '}';
    }
}
