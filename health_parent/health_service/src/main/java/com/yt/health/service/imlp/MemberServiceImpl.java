package com.yt.health.service.imlp;

import com.alibaba.dubbo.config.annotation.Service;
import com.yt.health.dao.MemberDao;
import com.yt.health.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 包名：com.yt.health.service.imlp
 *
 * @author Yangtao
 * 日期：2021-07-11  14:17
 */

@Service(interfaceClass = MemberService.class)
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    /*
     * @description:根据每月查询会员总数
     * @author: yangtao
     * @date: 2021/7/11 14:18
     * @param: month
     * @return: java.lang.Integer
     */
    @Override
    public Integer getMemberReport(String month) {
        return  memberDao.findMemberCountBeforeDate(month+"32");
    }
}
