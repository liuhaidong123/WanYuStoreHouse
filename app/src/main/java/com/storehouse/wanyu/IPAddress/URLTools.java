package com.storehouse.wanyu.IPAddress;

/**
 * Created by liuhaidong on 2018/8/2.
 */

public class URLTools {

    //public static String urlBase = "http://192.168.1.168:8085";
    public static String urlBase = "http://59.110.169.148:8085";
    public static String loginUrl = "/admin/login?";//登录接口参数：post方法name=13717883005&password=123456
    public static String bumen_list = "/mobileapi/department/findList.do?";//部门列表接口
    public static String check_cookie = "/admin/isLogin";//get方法。判断cookie是否过期

    public static String saoyisao = "/webapi/assetQrCode/get.do?";//扫描二维码后需要调用的接口,barcode=扫面之后的字符串;

    public static String dai_and_yi = "/mobileapi/convergeApproval/findPage.do?";//start=0&limit=20&msgStatus=0待审批,msgStatus=1已审批，待审批和已审批接口
    public static String sp_status_list = "/mobileapi/convergeApply/findPage.do?";//msgStatus=0审批中，1=被驳回，2=已完成，3=已失效，注意：若再加参数msgType，则查询的是具体的某个申请

    public static String submit_caigou_apply = "/mobileapi/buyApply/save.do?";//提交采购申请接口post方法
    public static String submit_lingyong_apply = "/mobileapi/assetRecipients/save.do?";//提交领用申请接口post方法
    public static String zichan_list = "/mobileapi/asset/findList.do?"; //get方法获取总资产列表接口
    public static String submit_jieyong_apply = "/mobileapi/assetBorrow/save.do?";//提交借用申请接口post方法borrowDate=2018-08-17&willReturnDate=2018-08-27&assetsIds=1,2

    public static String query_oneself_property_list = "/mobileapi/asset/findMyList.do?";//查询个人资产列表 start,limit,name=""为全部资产
    public static String submit_weixiu_apply = "/mobileapi/maintenanceLog/save.do?";//post assetId=1提交维修申请接口
    public static String submit_newold_apply = "/mobileapi/assetOldfornew/save.do?";//post assetId=1 提交以旧换新接口
    public static String submit_baofei_apply = "/mobileapi/assetScrap/save.do?"; //提交报废申请接口scrapModeId=1&assetsId=1&scrapDate=2018-08-17
    public static String query_baofei_way = "/mobileapi/scrapMode/findList.do?";//查询报废方式
    public static String submit_tuiku_apply = "/mobileapi/assetReturn/save.do?";//提交退库申请接口post returnDate=2018-08-17&assetsIds=1,2
    public static String caigou_details_url = "/mobileapi/buyApply/get.do?"; // 请求采购申请详情，待审批详情，已审批详情接口id=1
    public static String lingyong_details_url = "/mobileapi/assetRecipients/get.do?";//请求领用申请详情，待审批详情，已审批详情接口id=1
    public static String jieyong_details_url = "/mobileapi/assetBorrow/get.do?";//请求借用申请详情，待审批详情，已审批详情接口id=1
    public static String weixiu_details_url = "/mobileapi/maintenanceLog/get.do?";//请求维修申请详情，待审批详情，已审批详情接口id=1
    public static String newold_details_url = "/mobileapi/assetOldfornew/get.do?";//请求以旧换新申请详情，待审批详情，已审批详情接口id=1
    public static String baofei_details_url = "/mobileapi/assetScrap/get.do?";//请报废申请详情，待审批详情，已审批详情接口id=1
    public static String tuiku_details_url = "/mobileapi/assetReturn/get.do?";//v请退库申请详情，待审批详情，已审批详情接口id=1
    public static String caigou_agree_and_disagree = "/mobileapi/buyApply/doApproval.do?";//采购申请同意和驳回接口 post  isAdopt=是否同意，1同意，0驳回  rejectReason=驳回原因id=对应从待审批列表接口拿到的 refer_id
    public static String newold_agree_and_disagree = "/mobileapi/assetOldfornew/doApproval.do?";//以旧换新同意和驳回接口 post  isAdopt=是否同意，1同意，0驳回  rejectReason=驳回原因id=对应从待审批列表接口拿到的 refer_id
    public static String jieyong_agree_and_disagree = "/mobileapi/assetBorrow/doApproval.do?";//借用同意和驳回接口 post  isAdopt=是否同意，1同意，0驳回  rejectReason=驳回原因id=对应从待审批列表接口拿到的 refer_id
    public static String lingyong_agree_and_disagree = "/mobileapi/assetRecipients/doApproval.do?";//领用用同意和驳回接口 post  isAdopt=是否同意，1同意，0驳回  rejectReason=驳回原因id=对应从待审批列表接口拿到的 refer_id
    public static String tuiku_agree_and_disagree = "/mobileapi/assetReturn/doApproval.do?";//退库同意和驳回接口 post  isAdopt=是否同意，1同意，0驳回  rejectReason=驳回原因id=对应从待审批列表接口拿到的 refer_id
    public static String baofei_agree_and_disagree = "/mobileapi/assetScrap/doApproval.do?";//报废同意和驳回接口 post  isAdopt=是否同意，1同意，0驳回  rejectReason=驳回原因id=对应从待审批列表接口拿到的 refer_id
    public static String weixiu_agree_and_disagree = "/mobileapi/maintenanceLog/doApproval.do?";//维修同意和驳回接口 post  isAdopt=是否同意，1同意，0驳回  rejectReason=驳回原因id=对应从待审批列表接口拿到的 refer_id
    public static String pan_dian_property_list_url = "/mobileapi/asset/listForInventory.do"; //盘点中资产列表
    public static String submit_pan_pian = "/mobileapi/inventory/save.do?";//subject=盘点主题&description=盘点说明&assetsIds=1,2,3,4  post方法
    public static String pandian_huizong_list_url = "/mobileapi/inventory/findPage.do?";//盘点汇总
    public static String pandian_mes_url = "/mobileapi/inventory/get.do?";//盘点详情接口id=2
    public static String erweima_pandian_url = "/mobileapi/inventoryItem/getByCode.do?";//二维码扫描盘点接口inventoryId=8&barcode=zyyy0000000000000001
    public static String pandian_location_url = "/mobileapi/saveAddress/findList.do";//盘点中查询存放地点列表接口
    public static String pandian_status_url = "/mobileapi/inventoryItem/getProfitLoss.do";//盘点中查询资产状态列表接口
    public static String pandian_sure_url = "/mobileapi/inventoryItem/doInventory.do?";//提交每个物品的盘点物品id=1&资产状态profitAndLoss=0&资产存放地编码addressCode=
    public static String pandian_jiean_url = "/mobileapi/inventory/doClose.do?";//id=1&isClosed=isClosed,id 必须，盘点的编号isClosed=isClosed    参数和值都是固定的。
    public static String call_us_url = "/mobileapi/dict/findList.do?category=contact";//联系我们接口
    public static String suggestion_url = "/mobileapi/feedback/save.do?";//content=意见反馈接口
    public static String modify_password_url = "/mobileapi/user/modPwd.do?";//password=oldpwd&password2=newpwd  修改密码接口
    public static String property_list = "/mobileapi/asset/findPage.do?";  //资产管理》资产列表分页查询接口
    public static String property_leibie = "/mobileapi/category/findList.do?";//请求资产类别接口parentId=0表示最外面的最大集合
    public static String property_manager = "/mobileapi/user/saveList.do";//请求资产保管人接口
    public static String property_add = "/mobileapi/asset/save.do?";//增加资产接口post categoryCode="资产类别"&assetName ="资产名称"&saveUserId ="保管人ID"specTyp ="资产型号"addressCode="存放地"barcode="资产编号"
    public static String property_message = "/mobileapi/asset/get.do?";//资产详情接口，id=assetQrCode.getAssetId()
    public static String property_location_list = "/mobileapi/saveAddress/findList.do?";//资产存放地点接口parentId=0

    public static String post_notify = "/mobileapi/message/save.do?";//发布通知接口 post 参数msgType =0；title="通知标题"；
    public static String query_notify_list = "/mobileapi/message/findPage.do?";//分页查询通知接口
    public static String post_head_url = "/mobileapi/user/saveAvatar.do?";// 上传头像接口：post 参数 file

    public static String lingyong_property_list = "/mobileapi/asset/fpRecipients.do?";//post 方法 领用申请中资产列表,name=搜索内容,若name=""为搜索全部资产；
    public static String jieyong_property_list = "/mobileapi/asset/fpBorrow.do?";//post方法 借用申请中资产列表,name=搜索内容,若name=""为搜索全部资产；
    public static String weixiu_property_list = "/mobileapi/asset/fpMaintenance.do?";//post方法 维修申请中资产列表,name=搜索内容,若name=""为搜索全部资产；
    public static String newold_property_list = "/mobileapi/asset/fpOldfornew.do?";////post方法 以旧换新中资产列表,name=搜索内容,若name=""为搜索全部资产；
    public static String baofei_property_list = "/mobileapi/asset/fpScrap.do?";////post方法 报废中资产列表,name=搜索内容,若name=""为搜索全部资产；
    public static String tuiku_property_list = "/mobileapi/asset/fpReturn.do?";//退库资产列表

    public static String department_list = "/mobileapi/department/findList.do?";//科室列表接口parentId=0 表示最外层数据集合
    public static String submit_Inventory = "/mobileapi/inventory/saveBy.do?";//提交盘点接口，mode=0&code=01&subject=description
    public static String property_new_list = "/mobileapi/asset/fpManage.do?";//资产管理中资产列表接口
    public static String record_list = "/mobileapi/changesLog/findPage.do?";//变更记录列表 assetId=
    public static String purchase_order_list = "/mobileapi/buyApply/fpManage.do?";//采购订单列表 status=按状态查询：1=待采购，2=采购中，3=已入库，4=已退货
    public static String start_purchase_url = "/mobileapi/buyApply/startBuy.do?";//开始采购按钮 id=1请求详情时的id comment="备注"
    public static String repair_list = "/mobileapi/maintenanceLog/fpManage.do?";//维修列表 status=1待维修，2=已维修
    public static String parts_list = "/mobileapi/asset/fpFittings.do?";//维修管理 配件列表 name=""
    public static String repair_mess = "/mobileapi/maintenanceLog/getManage.do?";// 维修物品详情 id=1
    public static String repair_submit = "/mobileapi/maintenanceLog/saveManage.do?";//提交是否维修完毕
    public static String ku_yanshou = "/mobileapi/buyApply/fpCheck.do?";//出库入库待验收列表接口
    public static String ku_yanshou_submit = "/mobileapi/buyApply/saveCheck.do?";//出库入库》提交采购验收接口
    public static String ku_lingyong = "/mobileapi/assetRecipients/fpOutbound.do?";//出库入库》分页查询资产领用申请列表
    public static String ku_lingyong_getout = "/mobileapi/assetRecipients/saveOutbound.do?";//出库入库》领用出库接口
    public static String ku_jieying = "/mobileapi/assetBorrow/fpOutbound.do?";//出库入库》分页查询资产借用列表接口
    public static String ku_jieyong_getout = "/mobileapi/assetBorrow/saveOutbound.do?";//出库入库》借用出库接口
    public static String ku_jieyong_getin = "/mobileapi/assetBorrow/saveInbound.do?";//出库入库》借用归还接口
    public static String ku_oldnew = "/mobileapi/assetOldfornew/fpOutbound.do?";//出库入库》以旧换新》分页查询资产以旧换新列表接口
    public static String ku_newold_agree = "/mobileapi/assetOldfornew/saveChange.do?";////出库入库》以旧换新》同意换新接口
    public static String ku_tuiku = "/mobileapi/assetReturn/fpInbound.do?";//出库入库》分页查询资产退库列表接口
    public static String ku_tuiku_agree = "/mobileapi/assetReturn/saveInbound.do?";// 出库入库》资产退库申请》退库接口、入库接口
    public static String message_isread="/mobileapi/message/setReaded.do?";//消息》标记为已读接口

    public static String check_verson_code="/mobilepub/dict/get.do?id=6";//检测版本号
    public static String query_new_old_goods="/mobileapi/assetQrCode/getByCodeOldfornew.do?";//出库入库》以旧换新》根据barcode查询可更换的资产信息barcode=zyyy0000000000000001


}

