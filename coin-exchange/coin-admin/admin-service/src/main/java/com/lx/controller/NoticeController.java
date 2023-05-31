package com.lx.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.Notice;
import com.lx.model.R;
import com.lx.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;

/**
 * 公告管理
 */
@Api(value = "公告管理")
@RequestMapping("/notices")
@RestController
public class NoticeController {

    @Autowired
    private NoticeService noticeService;


    @GetMapping
    @ApiOperation(value = "条件分页查询公告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数"),
            @ApiImplicitParam(name = "title", value = "公告的标题"),
            @ApiImplicitParam(name = "startTime", value = "公告的创建开始时间"),
            @ApiImplicitParam(name = "endTime", value = "公告的创建结束时间时间"),
            @ApiImplicitParam(name = "status", value = "公告的状态"),
    })
    @PreAuthorize("hasAuthority('notice_query')")
    public R<Page<Notice>> findByPage(@ApiIgnore Page<Notice> page,
                                      String title,
                                      String startTime,
                                      String endTime,
                                      Integer status) {
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<Notice> noticePage = noticeService.findByPage(page, title, startTime, endTime, status);
        return R.ok(noticePage);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除一个公告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "要删除的id的集合")
    })
    @PreAuthorize("hasAuthority('notice_delete')")
    public R deleteNotice(@RequestBody String[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("删除时需要给id的值");
        }
        boolean success = noticeService.removeByIds(Arrays.asList(ids));
        return success ? R.ok() : R.fail("删除失败");
    }

    @PostMapping("/updateStatus")
    @ApiOperation(value = "启用/禁用一个公告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要启用/禁用一个公告的公告Id"),
            @ApiImplicitParam(name = "status", value = "要设置的公告状态")
    })
    @PreAuthorize("hasAuthority('notice_update')")
    public R updateStatus(Long id, Integer status) {
        Notice notice = new Notice();
        notice.setId(id);
        notice.setStatus(status);
        boolean success = noticeService.updateById(notice);
        return success ? R.ok("修改成功") : R.fail("修改失败");
    }

    @PostMapping
    @ApiOperation(value = "新增一个公告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "notice" ,value = "notice的json数据")
    })
    @PreAuthorize("hasAuthority('notice_create')")
    public R add(@RequestBody @Validated Notice notice){
        notice.setStatus(1);
        boolean success = noticeService.save(notice);
        return success ? R.ok() : R.fail("新增公告失败") ;
    }

    @PatchMapping
    @ApiOperation(value = "修改一个公告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "notice" ,value = "notice的json数据")
    })
    @PreAuthorize("hasAuthority('notice_update')")
    public R update(@RequestBody  @Validated  Notice notice){
        boolean success = noticeService.updateById(notice);
        return success ? R.ok() : R.fail("修改公告失败") ;
    }

    /*
    *  simple 就是给用户/会员看的
    * */
    @GetMapping("/simple")
    @ApiOperation(value = "查询前台展示的notice")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页"),
            @ApiImplicitParam(name = "size",value = "每页显示的条数")
    })
    public R<Page<Notice>> findNoticeForSimple(@ApiIgnore Page<Notice> page){
        Page<Notice> pageData = noticeService.findNoticeSimple(page);
        return R.ok(pageData);
    }

    @GetMapping("/simple/{id}")
    @ApiOperation(value = "查询某条Notice的详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "notice的id")
    })
    public R<Notice> noticeSimpleInfo(@PathVariable("id") Long id){
        Notice notice = noticeService.getById(id);
        return R.ok(notice);
    }
}
