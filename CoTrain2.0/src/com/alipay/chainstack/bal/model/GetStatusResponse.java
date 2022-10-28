package com.alipay.chainstack.bal.model;

import com.alipay.chainstack.jbcc.mychainx.exception.error.ErrorCode;
import com.alipay.chainstack.jbcc.mychainx.model.receipt.ReceiptModel;
import com.alipay.chainstack.jbcc.mychainx.model.response.BaseBAOResponse;
import com.alipay.mychain.sdk.vm.WASMOutput;
import org.apache.commons.codec.binary.Hex;

public class GetStatusResponse extends BaseBAOResponse {

    private String response;

    public GetStatusResponse() {
        super();
    }

    public static GetStatusResponse decodeFromReceipt(ReceiptModel receiptModel) {

        GetStatusResponse getStatusResponse = new GetStatusResponse();

        getStatusResponse.getRespContext().setReceiptModel(receiptModel);

        if (receiptModel == null) {
            return getStatusResponse;
        }

        if (receiptModel.getResult() == ErrorCode.SUCCESS.getErrorCode()) {

            getStatusResponse.getRespContext().setSuccess(true);
            getStatusResponse.getRespContext().setResultCode(receiptModel.getResult());

            byte[] returnBytes = receiptModel.getOutput();
            if (null != returnBytes && returnBytes.length > 0) {

                WASMOutput wasmOutput = new WASMOutput(Hex.encodeHexString(returnBytes));

                getStatusResponse.setResponse(wasmOutput.getString());
            }
        } else {
            getStatusResponse.getRespContext().setSuccess(false);
            getStatusResponse.getRespContext().setResultCode(receiptModel.getResult());
            if (null != receiptModel.getOutput() && receiptModel.getOutput().length > 0) {
                WASMOutput output = new WASMOutput(Hex.encodeHexString(receiptModel.getOutput()));
                getStatusResponse.getRespContext().setErrorMsg(output.getString());
            }
        }

        return getStatusResponse;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "GetStatusResponse{\n"
                + "  respContext="
                + getRespContext()
                + "\n"
                + "  response="
                + response
                + "\n"
                + '}';
    }
}
