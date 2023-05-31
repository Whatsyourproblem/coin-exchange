package com.lx.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.SysUser;
import com.lx.domain.WorkIssue;
import com.lx.enums.WorkIssueStatusEnum;
import com.lx.model.R;
import com.lx.service.SysUserService;
import com.lx.service.WorkIssueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 客户工单
 */
@Api(tags = "客户工单的控制器")
@RestController
@RequestMapping("/workIssues")
public class WorkIssueController {


    @Autowired
    private WorkIssueService workIssueService;

    @Autowired
    private SysUserService sysUserService;


    @GetMapping
    @ApiOperation(value = "分页条件查询工单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "工单当前的处理状态"),
            @ApiImplicitParam(name = "startTime", value = "工单创建的起始时间"),
            @ApiImplicitParam(name = "endTime", value = "工单创建的截至时间"),
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数"),
    })
    @PreAuthorize("hasAuthority('work_issue_query')")
    public R<Page<WorkIssue>> findByPage(@ApiIgnore Page<WorkIssue> page, Integer status, String startTime, String endTime) {
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<WorkIssue> workIssuePage = workIssueService.findByPage(page, status, startTime, endTime);
        return R.ok(workIssuePage);
    }

    @PatchMapping
    @ApiOperation(value = "回复某个工单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "工单的ID"),
            @ApiImplicitParam(name = "answer", value = "工单的answer"),
    })
    @PreAuthorize("hasAuthority('work_issue_update')")
    public R replyIssue(Long id, String answer) {
        WorkIssue workIssue = new WorkIssue();
        workIssue.setId(id);
        workIssue.setAnswer(answer);
        workIssue.setStatus(WorkIssueStatusEnum.ANSWERED.getStatus());
        //设置回复人id，就是当前登录的用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        workIssue.setAnswerUserId(Long.valueOf(userId));
        SysUser sysUser = sysUserService.getById(workIssue.getAnswerUserId());
        workIssue.setAnswerName(sysUser.getFullname());
        boolean success = workIssueService.updateById(workIssue);
        return success ? R.ok() : R.fail("回复失败");
    }

    @GetMapping("/issueList")
    @ApiOperation(value = "前台查询工单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页"),
            @ApiImplicitParam(name = "size",value = "每页显示的条数"),
    })
    public R<Page<WorkIssue>> getIssueList(@ApiIgnore Page<WorkIssue> page){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Page<WorkIssue> pageData = workIssueService.getIssueList(page,userId);
        return R.ok(pageData);
    }

    @PostMapping("/addWorkIssue")
    @ApiOperation(value = "会员的添加问题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workIssue",value = "workIssued1json数据")
    })
    public R addWorkIssue(@RequestBody WorkIssue workIssue){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        workIssue.setUserId(userId);
        workIssue.setStatus(1);

        boolean save = workIssueService.save(workIssue);
        if (save){
            return R.ok();
        }
        return R.fail("添加失败");
    }
}
