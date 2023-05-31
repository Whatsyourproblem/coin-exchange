package com.lx.constant;

/*
*  登录常量
* */
public class LoginConstant {

    /*
    *  后台管理人员
    * */
    public static final String ADMIN_TYPE = "admin_type";

    /*
    *  普通的会员
    * */
    public static final String MEMBER_TYPE = "member_type";

    /**
     * 根据用户名查询用户
     **/
    public static final String QUERY_ADMIN_SQL =
            "SELECT `id` ,`username`, `password`, `status` FROM sys_user WHERE username = ? ";

    /**
     * 0: 判断用户是否为管理员
     **/
    public static final String QUERY_ROLE_CODE_SQL =
            "SELECT `code` FROM sys_role LEFT JOIN sys_user_role ON sys_role.id = sys_user_role.role_id WHERE sys_user_role.user_id= ?";

    /**
     * 1: 用户为管理员时：（拥有全部的权限）
     **/
    public static final String QUERY_ALL_PERMISSIONS =
            "SELECT `name` FROM sys_privilege";

    /**
     * 2: 普通用户时（通过用户的角色查询用户的权限）
     **/
    public static final String QUERY_PERMISSION_SQL =
            "SELECT * FROM sys_privilege LEFT JOIN sys_role_privilege ON sys_role_privilege.privilege_id = sys_privilege.id LEFT JOIN sys_user_role  ON sys_role_privilege.role_id = sys_user_role.role_id WHERE sys_user_role.user_id = ?";

    /**
     * 超级管理员角色的code
     **/
    public static final String ADMIN_ROLE_CODE = "ROLE_ADMIN";

    /**
     *  会员查询sql
     **/
    public static final String QUERY_MEMBER_SQL =
            "SELECT `id`,`password`, `status` FROM `user` WHERE mobile = ? or email = ? ";

    /**
     * token的刷新
     */
    public static  final  String REFRESH_TYPE= "REFRESH_TOKEN" ;

    /**
     * 使用后台会员的id 查询会员名称
     */
    public static  final  String QUERY_ADMIN_USER_WITH_ID = "SELECT `username` FROM sys_user where id = ?" ;

    /**
     * 使用前台用户的id 查询用户名称
     */
    public static  final  String QUERY_MEMBER_USER_WITH_ID = "SELECT `mobile` FROM user where id = ?" ;
}
