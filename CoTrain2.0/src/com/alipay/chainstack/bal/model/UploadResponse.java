package com.alipay.chainstack.bal.model;

import com.alipay.chainstack.jbcc.mychainx.exception.error.ErrorCode;
import com.alipay.chainstack.jbcc.mychainx.model.receipt.ReceiptModel;
import com.alipay.chainstack.jbcc.mychainx.model.response.BaseBAOResponse;
import com.alipay.mychain.sdk.vm.WASMOutput;
import org.apache.commons.codec.binary.Hex;

public class UploadResponse extends BaseBAOResponse {

    public UploadResponse() {
        super();
    }

    public static UploadResponse decodeFromReceipt(ReceiptModel receiptModel) {

        UploadResponse uploadResponse = new UploadResponse();

        uploadResponse.getRespContext().setReceiptModel(receiptModel);

        if (receiptModel == null) {
            return uploadResponse;
        }

        if (receiptModel.getResult() == ErrorCode.SUCCESS.getErrorCode()) {

            uploadResponse.getRespContext().setSuccess(true);
            uploadResponse.getRespContext().setResultCode(receiptModel.getResult());

        } else {
            uploadResponse.getRespContext().setSuccess(false);
            uploadResponse.getRespContext().setResultCode(receiptModel.getResult());
            if (null != receiptModel.getOutput() && receiptModel.getOutput().length > 0) {
                WASMOutput output = new WASMOutput(Hex.encodeHexString(receiptModel.getOutput()));
                uploadResponse.getRespContext().setErrorMsg(output.getString());
            }
        }

        return uploadResponse;
    }

    @Override
    public String toString() {
        return "UploadResponse{\n" + "  respContext=" + getRespContext() + "\n" + '}';
    }
}
