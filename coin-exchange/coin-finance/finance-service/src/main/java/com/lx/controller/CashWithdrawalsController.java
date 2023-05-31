package com.lx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.constant.Constants;
import com.lx.domain.CashRecharge;
import com.lx.domain.CashWithdrawAuditRecord;
import com.lx.domain.CashWithdrawals;
import com.lx.model.CashSellParam;
import com.lx.model.R;
import com.lx.service.CashWithdrawalsService;
import com.lx.util.ReportCsvUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/cashWithdrawals")
@Api(tags = "GCN体现记录")
public class CashWithdrawalsController {

    @Autowired
    private CashWithdrawalsService cashWithdrawalsService;

    @GetMapping("/records")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页"),
            @ApiImplicitParam(name = "size",value = "每页显示的条数"),
            @ApiImplicitParam(name = "userId",value = "用户的ID"),
            @ApiImplicitParam(name = "userName",value = "用户的名称"),
            @ApiImplicitParam(name = "mobile",value = "用户的手机号"),
            @ApiImplicitParam(name = "status",value = "充值的状态"),
            @ApiImplicitParam(name = "numMin",value = "充值金额的最小值"),
            @ApiImplicitParam(name = "numMax",value = "充值金额的最大值"),
            @ApiImplicitParam(name = "startTime",value = "充值开始时间"),
            @ApiImplicitParam(name = "endTime",value = "充值结束时间"),
    })
    public R<Page<CashWithdrawals>> findByPage(
            @ApiIgnore Page<CashWithdrawals> page,
            Long userId, String userName, String mobile,
            Byte status, String numMin, String numMax,
            String startTime, String endTime
    ){
        Page<CashWithdrawals> pageData = cashWithdrawalsService.findByPage(page,userId,userName,mobile,status,numMin,numMax,startTime,endTime);
        return R.ok(pageData);
    }

    @GetMapping("/exportCNYWithDrawals")
    @ApiOperation(value = "导出GCN提现记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="userId" ,value = "用户id"),
            @ApiImplicitParam(name ="username" ,value = "用户名"),
            @ApiImplicitParam(name ="mobile" ,value = "手机号"),
            @ApiImplicitParam(name ="status" ,value = "状态"),
            @ApiImplicitParam(name ="numMin" ,value = "提现金额下限"),
            @ApiImplicitParam(name ="numMax" ,value = "提现金额上限"),
            @ApiImplicitParam(name ="startTime" ,value = "开始时间"),
            @ApiImplicitParam(name ="endTime" ,value = "终止时间"),
    })
    public void exportCNYWithDrawals(Long userId, String userName, String mobile, Byte status,
                                     String numMin, String numMax, String startTime, String endTime) {
        Page<CashWithdrawals> cashWithdrawalsPage = new Page<>(1, 10000);
        Page<CashWithdrawals> pageData = cashWithdrawalsService.findByPage(cashWithdrawalsPage, userId, userName, mobile, status, numMin, numMax, startTime, endTime);
        List<CashWithdrawals> records = pageData.getRecords();
        if (!CollectionUtils.isEmpty(records)){
            // 进行导出操作
            // 格式转换
            CellProcessorAdaptor longToStringAdapter = new CellProcessorAdaptor() {
                @Override
                public <T> T execute(Object o, CsvContext csvContext) {
                    // Object为需要转成的类型
                    return (T) String.valueOf(o);
                }
            };
            // 对于金额，需要保留8位有效数字
            DecimalFormat decimalFormat = new DecimalFormat("0.00000000");
            CellProcessorAdaptor moneyCellProcessorAdaptor = new CellProcessorAdaptor(){
                @Override
                public <T> T execute(Object o, CsvContext csvContext) {
                    BigDecimal num = (BigDecimal) o;
                    String numReal = decimalFormat.format(num);
                    return (T) numReal;
                }
            };
            // 对类型进行处理
            CellProcessorAdaptor typeCellProcessorAdaptor = new CellProcessorAdaptor() {
                @Override
                public <T> T execute(Object o, CsvContext csvContext) {
                    String type = String.valueOf(o);
                    String typeName = "";
                    switch (type){
                        case "alipay":
                            typeName = "支付宝";
                            break;
                        case "cai1pay":
                            typeName = "财易付";
                            break;
                        case "bank":
                            typeName = "银联";
                            break;
                        case "linepay":
                            typeName = "在线支付";
                            break;
                        default:
                            typeName = "未知";
                            break;
                    }
                    return (T) typeName;
                }
            };

            // 对时间进行处理
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CellProcessorAdaptor timeCellProcessorAdaptor = new CellProcessorAdaptor() {
                @Override
                public <T> T execute(Object o, CsvContext csvContext) {
                    Date date = (Date) o;
                    String dateStr = simpleDateFormat.format(date);
                    return (T) dateStr;
                }
            };

            // 单独对状态status做一个类型处理器 倒数第三个参数
            CellProcessorAdaptor statusCellProcessorAdaptor = new CellProcessorAdaptor() {
                @Override
                public <T> T execute(Object o, CsvContext csvContext) {
                    Byte status = (Byte) o;
                    String statusStr = "";
                    switch (status){
                        case 0:
                            statusStr = "待审核";
                            break;
                        case 1:
                            statusStr = "审核通过";
                            break;
                        case 2:
                            statusStr = "拒绝";
                            break;
                        case 3:
                            statusStr = "体现成功";
                            break;
                        default:
                            statusStr = "未知";
                            break;
                    }
                    return (T) statusStr;
                }
            };


            // 对 headers和properties进行类型转化
            CellProcessor[] processors = new CellProcessor[]{
                    longToStringAdapter,longToStringAdapter,null,moneyCellProcessorAdaptor,moneyCellProcessorAdaptor,
                    moneyCellProcessorAdaptor,null,null,
                    timeCellProcessorAdaptor,timeCellProcessorAdaptor,statusCellProcessorAdaptor,
                    null,null
            };
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            try {
                // 导出csv文件
                ReportCsvUtils.reportList(requestAttributes.getResponse(), Constants.CASH_WITHDRAWS_HEADERS
                        ,Constants.CASH_WITHDRAWS_PROPERTIES,"场外交易提现审核.csv",records,processors);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/updateWithdrawalsStatus")
    @ApiOperation(value = "现金的提现审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cashWithdrawAuditRecord", value = "cashWithdrawAuditRecord json数据")
    })
    public R updateWithdrawalsStatus(@RequestBody CashWithdrawAuditRecord cashWithdrawAuditRecord){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        boolean isOk = cashWithdrawalsService.updateWithdrawalsStatus(userId,cashWithdrawAuditRecord);
        return isOk ? R.ok():R.fail("审核失败");
    }

    @GetMapping("/user/records")
    @ApiOperation(value = "查询当前用户的提现(出售)记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页"),
            @ApiImplicitParam(name = "size",value = "每页显示的大小"),
            @ApiImplicitParam(name = "status",value = "充值的状态")
    })
    public R<Page<CashWithdrawals>> findUserCashWithdrawals(@ApiIgnore Page<CashWithdrawals> page,Byte status){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Page<CashWithdrawals> cashWithdrawalsPage = cashWithdrawalsService.findUserCashWithdrawals(page,userId,status);
        return R.ok(cashWithdrawalsPage);
    }

    @PostMapping("/sell")
    @ApiOperation(value = "GCN卖出")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="cashSellParam" ,value = "cashSellParam json数据"),
    })
    public R<Object> sell(@RequestBody @Validated CashSellParam cashSellParam) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        boolean isOk = cashWithdrawalsService.sell(userId, cashSellParam);
        if (isOk){
            return R.ok("提交申请成功");
        }
        return R.fail("提交申请失败");
    }
}
