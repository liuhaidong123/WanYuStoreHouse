package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2019/1/7.
 */

public class QueryNewOldGoodsRoot {
    private QueryNewOldGoodsAssetQrCode assetQrCode;

    private String code;

    private QueryNewOldGoodsAsset Asset;

    private String message;

    public QueryNewOldGoodsAssetQrCode getAssetQrCode() {
        return assetQrCode;
    }

    public void setAssetQrCode(QueryNewOldGoodsAssetQrCode assetQrCode) {
        this.assetQrCode = assetQrCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public QueryNewOldGoodsAsset getAsset() {
        return Asset;
    }

    public void setAsset(QueryNewOldGoodsAsset asset) {
        Asset = asset;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
