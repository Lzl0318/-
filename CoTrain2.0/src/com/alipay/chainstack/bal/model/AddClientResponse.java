package com.alipay.chainstack.bal.model;

import com.alipay.chainstack.jbcc.mychainx.exception.error.ErrorCode;
import com.alipay.chainstack.jbcc.mychainx.model.receipt.ReceiptModel;
import com.alipay.chainstack.jbcc.mychainx.model.response.BaseBAOResponse;
import com.alipay.mychain.sdk.vm.WASMOutput;
import org.apache.commons.codec.binary.Hex;

public class AddClientResponse extends BaseBAOResponse {

    public AddClientResponse() {
        super();
    }

    public static AddClientResponse decodeFromReceipt(ReceiptModel receiptModel) {

        AddClientResponse addClientResponse = new AddClientResponse();

        addClientResponse.getRespContext().setReceiptModel(receiptModel);

        if (receiptModel == null) {
            return addClientResponse;
        }

        if (receiptModel.getResult() == ErrorCode.SUCCESS.getErrorCode()) {

            addClientResponse.getRespContext().setSuccess(true);
            addClientResponse.getRespContext().setResultCode(receiptModel.getResult());

        } else {
            addClientResponse.getRespContext().setSuccess(false);
            addClientResponse.getRespContext().setResultCode(receiptModel.getResult());
            if (null != receiptModel.getOutput() && receiptModel.getOutput().length > 0) {
                WASMOutput output = new WASMOutput(Hex.encodeHexString(receiptModel.getOutput()));
                addClientResponse.getRespContext().setErrorMsg(output.getString());
            }
        }

        return addClientResponse;
    }

    @Override
    public String toString() {
        return "AddClientResponse{\n" + "  respContext=" + getRespContext() + "\n" + '}';
    }
}
