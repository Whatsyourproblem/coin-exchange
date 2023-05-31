package com.lx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.constant.Constants;
import com.lx.domain.CashRecharge;
import com.lx.domain.CoinRecharge;
import com.lx.domain.CoinWithdraw;
import com.lx.model.R;
import com.lx.service.CoinWithdrawService;
import com.lx.util.ReportCsvUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
@RequestMapping("/coinWithdraws")
@Api(tags = "提币记录的审核")
public class CoinWithdrawController {


    @Autowired
    private CoinWithdrawService coinWithdrawService;

    @GetMapping("/records")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页"),
            @ApiImplicitParam(name = "size",value = "每页显示的条数"),
            @ApiImplicitParam(name = "coinId",value = "币种Id"),
            @ApiImplicitParam(name = "userId",value = "用户的ID"),
            @ApiImplicitParam(name = "userName",value = "用户的名称"),
            @ApiImplicitParam(name = "mobile",value = "用户的手机号"),
            @ApiImplicitParam(name = "status",value = "充值的状态"),
            @ApiImplicitParam(name = "numMin",value = "充值金额的最小值"),
            @ApiImplicitParam(name = "numMax",value = "充值金额的最大值"),
            @ApiImplicitParam(name = "startTime",value = "充值开始时间"),
            @ApiImplicitParam(name = "endTime",value = "充值结束时间")
    })
    public R<Page<CoinWithdraw>> findByPage(
            @ApiIgnore Page<CoinWithdraw> page, Long coinId,
            Long userId, String userName, String mobile,
            Byte status, String numMin, String numMax,
            String startTime, String endTime
    ){
        Page<CoinWithdraw> pageData = coinWithdrawService.findByPage(page,coinId,userId,userName
                ,mobile,status,numMin,numMax,startTime,endTime);
        return R.ok(pageData);
    }


    @GetMapping("/exportList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coinId",value = "币种Id"),
            @ApiImplicitParam(name = "userId",value = "用户的ID"),
            @ApiImplicitParam(name = "userName",value = "用户的名称"),
            @ApiImplicitParam(name = "mobile",value = "用户的手机号"),
            @ApiImplicitParam(name = "status",value = "充值的状态"),
            @ApiImplicitParam(name = "numMin",value = "充值金额的最小值"),
            @ApiImplicitParam(name = "numMax",value = "充值金额的最大值"),
            @ApiImplicitParam(name = "startTime",value = "充值开始时间"),
            @ApiImplicitParam(name = "endTime",value = "充值结束时间")
    })
    public void exportList(
            Long coinId,
            Long userId, String userName, String mobile,
            Byte status, String numMin, String numMax,
            String startTime, String endTime
    ){
        Page<CoinWithdraw> page = new Page<>(1, 10000);
        Page<CoinWithdraw> pageData = coinWithdrawService.findByPage(page,coinId,userId,userName
                ,mobile,status,numMin,numMax,startTime,endTime);
        List<CoinWithdraw> records = pageData.getRecords();
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
                    Byte type = (Byte) o;
                    String typeName = "";
                    switch (type){
                        case 0:
                            typeName = "站内";
                            break;
                        case 1:
                            typeName = "其他";
                            break;
                        case 2:
                            typeName = "手工提币";
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
                            statusStr = "审核中";
                            break;
                        case 1:
                            statusStr = "成功";
                            break;
                        case 2:
                            statusStr = "拒绝";
                            break;
                        case 3:
                            statusStr = "撤销";
                            break;
                        case 4:
                            statusStr = "审核通过";
                            break;
                        case 5:
                            statusStr = "打币中";
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
                    longToStringAdapter,null,null,moneyCellProcessorAdaptor,moneyCellProcessorAdaptor,
                    moneyCellProcessorAdaptor,null,longToStringAdapter,timeCellProcessorAdaptor,timeCellProcessorAdaptor,
                    null,statusCellProcessorAdaptor,typeCellProcessorAdaptor,null
            };
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            try {
                // 导出csv文件
                ReportCsvUtils.reportList(requestAttributes.getResponse(), Constants.COIN_WITHDRAWS_HEADERS
                        ,Constants.COIN_WITHDRAWS_PROPERTIES,"数字货币体现审核记录.csv",records,processors);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @GetMapping("/user/record")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页"),
            @ApiImplicitParam(name = "size",value = "显示的条数"),
            @ApiImplicitParam(name = "coinId",value = "币种的Id"),
    })
    @ApiOperation(value = "查询用户某种币的提币记录")
    public R<Page<CoinWithdraw>> findUserCoinRecharge(@ApiIgnore Page<CoinWithdraw> page, Long coinId){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Page<CoinWithdraw> pageData = coinWithdrawService.findUserCoinRecharge(page,coinId,userId);
        return R.ok(pageData);
    }



}
