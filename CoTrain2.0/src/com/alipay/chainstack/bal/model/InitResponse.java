package com.alipay.chainstack.bal.model;

import com.alipay.chainstack.jbcc.mychainx.exception.error.ErrorCode;
import com.alipay.chainstack.jbcc.mychainx.model.receipt.ReceiptModel;
import com.alipay.chainstack.jbcc.mychainx.model.response.BaseBAOResponse;
import com.alipay.mychain.sdk.vm.WASMOutput;
import org.apache.commons.codec.binary.Hex;

public class InitResponse extends BaseBAOResponse {

    public InitResponse() {
        super();
    }

    public static InitResponse decodeFromReceipt(ReceiptModel receiptModel) {

        InitResponse initResponse = new InitResponse();

        initResponse.getRespContext().setReceiptModel(receiptModel);

        if (receiptModel == null) {
            return initResponse;
        }

        if (receiptModel.getResult() == ErrorCode.SUCCESS.getErrorCode()) {

            initResponse.getRespContext().setSuccess(true);
            initResponse.getRespContext().setResultCode(receiptModel.getResult());

        } else {
            initResponse.getRespContext().setSuccess(false);
            initResponse.getRespContext().setResultCode(receiptModel.getResult());
            if (null != receiptModel.getOutput() && receiptModel.getOutput().length > 0) {
                WASMOutput output = new WASMOutput(Hex.encodeHexString(receiptModel.getOutput()));
                initResponse.getRespContext().setErrorMsg(output.getString());
            }
        }

        return initResponse;
    }

    @Override
    public String toString() {
        return "InitResponse{\n" + "  respContext=" + getRespContext() + "\n" + '}';
    }
}
