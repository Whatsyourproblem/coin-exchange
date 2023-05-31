package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.WorkIssue;
import com.lx.dto.UserDto;
import com.lx.feign.UserServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.WorkIssueMapper;
import com.lx.service.WorkIssueService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkIssueServiceImpl extends ServiceImpl<WorkIssueMapper, WorkIssue> implements WorkIssueService{

    @Autowired
    private UserServiceFeign userServiceFeign;

    /**
     * <h2>分页条件查询工单</h2>
     * @param page       分页参数
     * @param status     工单当前的处理状态
     * @param startTime  工单创建的起始时间
     * @param endTime    工单创建的截至时间
     **/
    @Override
    public Page<WorkIssue> findByPage(Page<WorkIssue> page, Integer status, String startTime, String endTime) {
        Page<WorkIssue> pageData = page(page, new LambdaQueryWrapper<WorkIssue>()
                .eq(status != null, WorkIssue::getStatus, status)
                .between(
                        !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime),
                        WorkIssue::getCreated,
                        startTime,
                        endTime + " 23:59:59"
                )
        );
        List<WorkIssue> records = pageData.getRecords();
        if (CollectionUtils.isEmpty(records)){
            return pageData;
        }
        // 远程调用member-service
        // 错误示范
/*        for (WorkIssue record : records) {
            Long userId = record.getUserId();
            // 使用userId去进行远程调用，这样不行，非常浪费性能，应该收集好id集合，批量查询
        }*/
        // 1.收集用户id集合 ids
        List<Long> userIds = records.stream().map(WorkIssue::getUserId).collect(Collectors.toList());
        // 2.远程调用
        //List<UserDto> basicUsers = userServiceFeign.getBasicUsers(userIds);
        // 2.1 小技巧: list->  map<id,userDto>
/*        if (CollectionUtils.isEmpty(basicUsers)){
            return pageData;
        }
        Map<Long, UserDto> idMapUserDtos = basicUsers.stream()
                .collect(
                        // key和value的转换
                        Collectors.toMap(userDto -> userDto.getId(), userDto -> userDto));*/

        // 修改了远程调用的接口代码，可以直接获取Map<userId,UserDto>集合
        Map<Long, UserDto> idMapUserDtos = userServiceFeign.getBasicUsers(userIds, null, null);
        records.forEach(workIssue -> {
            // 循环每一个workIssue,给它里面设置用户的信息 map.get(userId)
            UserDto userDto = idMapUserDtos.get(workIssue.getUserId());
            workIssue.setUsername(userDto==null?"测试用户":userDto.getUsername());
            workIssue.setRealName(userDto==null?"测试用户":userDto.getRealName());
        });

        return pageData;
    }

    /*
     *  前台系统查询客户工单
     * */
    @Override
    public Page<WorkIssue> getIssueList(Page<WorkIssue> page,Long userId) {
        return page(page,new LambdaQueryWrapper<WorkIssue>()
                .eq(WorkIssue::getUserId,userId));
                //.eq(WorkIssue::getStatus,1)
    }
}
