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

public class AddClientRequest extends BaseBAORequest {

    public static String METHOD_SIGNATURE = "addClient";

    public AddClientRequest() {
        getReqContext().setMethodSig(METHOD_SIGNATURE);
    }

    @Override
    public Parameters getParameters() {

        WASMParameter wasmParameter = new WASMParameter(getReqContext().getMethodSig());

        return wasmParameter;
    }

    public static boolean isAddClientRequest(TransactionModel transactionModel) {
        if (transactionModel == null
                || transactionModel.getData() == null
                || !TransactionType.TX_CALL_CONTRACT.equals(transactionModel.getTxType())) {
            return false;
        }
        WASMOutput output = new WASMOutput(Hex.encodeHexString(transactionModel.getData()));
        String methodSig = getTransactionMethod(transactionModel, output);
        return StringUtils.equals(methodSig, AddClientRequest.METHOD_SIGNATURE);
    }

    public static AddClientRequest decodeFromTransaction(TransactionModel transactionModel) {

        AddClientRequest addClientRequest = new AddClientRequest();

        WASMOutput output = new WASMOutput(Hex.encodeHexString(transactionModel.getData()));
        String methodSig = getTransactionMethod(transactionModel, output);

        if (!StringUtils.equals(methodSig, AddClientRequest.METHOD_SIGNATURE)) {
            return null;
        }

        addClientRequest.getReqContext().setTxHash(transactionModel.getHash());

        addClientRequest
                .getReqContext()
                .setAccountNameHex(transactionModel.getFrom().hexStrValue());
        addClientRequest.getReqContext().setContractNameHex(transactionModel.getTo().hexStrValue());

        Account requestAccount =
                AccountManager.getInstance()
                        .getAccountByNameHex(transactionModel.getFrom().hexStrValue());
        if (null != requestAccount) {
            addClientRequest
                    .getReqContext()
                    .setAccount(new NamedIdentity(requestAccount.getName()));
        }

        return addClientRequest;
    }

    @Override
    public String toString() {
        return "AddClientRequest{\n" + "  reqContext=" + getReqContext() + "\n" + '}';
    }
}
