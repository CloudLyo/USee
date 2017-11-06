package com.white.usee.app.config;

/**
 * 链接类
 * 所有的链接都放在这里
 * 在做开发的时候尽量把mainUrl里面换成testip，在测试服务器上进行开发，然后上线前测试时候换成mainip
 * Created by white on 15-10-29.
 */
public class HttpUrlConfig {
    private static String testip = "121.42.149.46";
    private static String mainip = "114.215.209.102";
    private static String mainUrl = "http://" + mainip + "/USee";
    public static String iconUrl = mainUrl + "/userIcons/";


    public static String qiniuTokenUrl =mainUrl+"/getuptoken"; //"http://115.231.183.102:9090/api/quick_start/simple_image_example_token.php";//七牛上传凭证的url
    //获取到敏感词汇
    public static String getSenSiteiveWord = mainUrl + "/sensitiveword";

    public static String getDanmubyTopic = mainUrl + "/getdmbytopic";

    //获取弹幕详情接口
    public static String getdmdetails = mainUrl + "/getdmdetails";

    //注册账户时短信验证
    public static String cellPhoneValidateRegister = mainUrl + "/cellphonevalidate/register";

    //通过手机注册
    public static String registerByPhoneNumber = mainUrl + "/user/signin";

    //忘记密码、绑定手机号发送短信验证码
    public static String sendCode = mainUrl + "/cellphonevalidate/getcode";

    //手机号登录
    public static String userLogin = mainUrl + "/user/login";

    //第三方登录
    public static String oauthLogin = mainUrl + "/oauthlogin/android";

    //修改密码
    public static String modifyPassword = mainUrl + "/user/modifypassword";

    //更新信息
    public static String updateUser = mainUrl + "/user/updateuser";
    public static String forgetpassword = mainUrl + "/user/forgetpassword";
    //上传头像
    public static String uploadicon = mainUrl + "/user/uploadicon";

    //绑定手机号
    public static String bindCellPhone = mainUrl + "/user/bindcellphone";

    //绑定第三方账户
    public static String bindOauth = mainUrl + "/user/bindoauth";

    //反馈接口
    public static String feedback = mainUrl + "/user/feedback";
    //发送弹幕
    public static String sendDanmu = mainUrl + "/senddanmu";

    //获取周围话题
    public static String getNearbyTopics = mainUrl + "/getnearbytopics";

    //获取话题详情
    public static String gettopicinfo = mainUrl+"/gettopicinfo";
    //获取与我相关话题

    public static String getUserTopics = mainUrl + "/getusertopics";

    //发送评论（对弹幕，对评论）
    public static String commentdanmu = mainUrl + "/commentdanmu";

    //创建话题
    public static String createTopic = mainUrl + "/createtopic";

    //更新话题
    public static String updateusertopic = mainUrl + "/updateusertopic";

    //获取话题内的头像

    public static String getusericonbytopic = mainUrl + "/getusericonbytopic";

    public static String getusericonbycomment = mainUrl + "/getusericonbycomment";


    //收藏弹幕
    public static String favdanmu = mainUrl + "/favdanmu";

    public static String updateUserAction = mainUrl + "/updateuseraction";

    public static String getFavdanmuList = mainUrl + "/getfavdanmulist";

    //搜索话题
    public static String searchtopic = mainUrl + "/searchtopic";

    //获取新消息数量
    public static String getNewMsgNum = mainUrl + "/message/getNewMsgsNum";

    public static String getNewMsgs = mainUrl + "/message/getNewMsgs";

    public static String getAllMsgs = mainUrl + "/message/getallMsgs";
    //获取弹幕数量
    public static String getallMsgsNum = mainUrl + "/message/getallMsgsNum";
    //获取到热门话题
    public static String getHotTopic = mainUrl + "/gethotsearch";
    public static String getlatestdanmu = mainUrl + "/getlatestdanmu";

    //举报弹幕评论
    public static String reportcontent = mainUrl + "/reportcontent";

    //不感兴趣话题
    public static String disliketopic = mainUrl + "/disliketopic";

    //移出黑名单
    public static String liketopic = mainUrl + "/liketopic";
    //敏感词过滤
    public static String getSWFileInfo = mainUrl + "/getSWFileInfo";

    public static String sharetopic = mainUrl + "/sharetopic";

    //删除弹幕
    public static String deleteDanmu = mainUrl+"/deletedanmu";

    //删除评论
    public static String deleteComment = mainUrl+"/deletecomment";

    public static String getRealNameInfo = mainUrl + "/user/getrealnameinfo";

    public static String getTopicsByType = mainUrl + "/gettopicsbytype";

    //获得活动数据
    public static String getviewpagers = mainUrl + "/getviewpages";
}
