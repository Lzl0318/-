package com.alipay.chainstack.bal.model;

import com.alipay.chainstack.jbcc.mychainx.exception.error.ErrorCode;
import com.alipay.chainstack.jbcc.mychainx.model.receipt.ReceiptModel;
import com.alipay.chainstack.jbcc.mychainx.model.response.BaseBAOResponse;
import com.alipay.mychain.sdk.vm.WASMOutput;
import org.apache.commons.codec.binary.Hex;

public class DownloadResponse extends BaseBAOResponse {

    private String response;

    public DownloadResponse() {
        super();
    }

    public static DownloadResponse decodeFromReceipt(ReceiptModel receiptModel) {

        DownloadResponse downloadResponse = new DownloadResponse();

        downloadResponse.getRespContext().setReceiptModel(receiptModel);

        if (receiptModel == null) {
            return downloadResponse;
        }

        if (receiptModel.getResult() == ErrorCode.SUCCESS.getErrorCode()) {

            downloadResponse.getRespContext().setSuccess(true);
            downloadResponse.getRespContext().setResultCode(receiptModel.getResult());

            byte[] returnBytes = receiptModel.getOutput();
            if (null != returnBytes && returnBytes.length > 0) {

                WASMOutput wasmOutput = new WASMOutput(Hex.encodeHexString(returnBytes));

                downloadResponse.setResponse(wasmOutput.getString());
            }
        } else {
            downloadResponse.getRespContext().setSuccess(false);
            downloadResponse.getRespContext().setResultCode(receiptModel.getResult());
            if (null != receiptModel.getOutput() && receiptModel.getOutput().length > 0) {
                WASMOutput output = new WASMOutput(Hex.encodeHexString(receiptModel.getOutput()));
                downloadResponse.getRespContext().setErrorMsg(output.getString());
            }
        }

        return downloadResponse;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "DownloadResponse{\n"
                + "  respContext="
                + getRespContext()
                + "\n"
                + "  response="
                + response
                + "\n"
                + '}';
    }
}
