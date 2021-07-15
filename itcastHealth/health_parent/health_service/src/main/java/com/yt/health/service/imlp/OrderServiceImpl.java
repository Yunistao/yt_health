package com.yt.health.service.imlp;

import com.alibaba.dubbo.config.annotation.Service;
import com.yt.health.dao.MemberDao;
import com.yt.health.dao.OrderDao;
import com.yt.health.dao.OrderSettingDao;
import com.yt.health.exception.HealthException;
import com.yt.health.pojo.Member;
import com.yt.health.pojo.Order;
import com.yt.health.pojo.OrderSetting;
import com.yt.health.service.OrderService;
import com.yt.health.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 包名：com.yt.health.service.imlp
 *
 * @author Yangtao
 * 日期：2021-07-04  23:51
 */

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    @Override
    public Order submit(Map<String, String> paramMap) {
       // 1. 通过日期查询预约设置是否存在 t_ordersetting
        Date orderDate = null;
        try {
            orderDate = DateUtils.parseString2Date(paramMap.get("orderDate"), "yyyy-MM-dd");
        } catch (Exception e) {
            e.printStackTrace();
            throw new HealthException("日期格式错误");
        }
        OrderSetting orderSetting = orderSettingDao.findOrderDate(orderDate);
        // 不存在就报错
        if (null == orderSetting) {
            throw new HealthException("该日期无法预约，请选择其他日期");
        }
       // 存在 则判断是否约满
        if (orderSetting.getReservations()>=orderSetting.getNumber()) {
       // full 报错
            throw new HealthException("该日期已约满，请选择其他日期");
        }
       // 2. 是否为会员 通过手机号码查询 t_member
        Member member = memberDao.findByTelephone(paramMap.get("telephone"));
        //非会员
        if (null == member) {
            // 添加到会员表 获取id
            member = new Member();
            member.setIdCard(paramMap.get("idCard"));
            member.setName(paramMap.get("name"));
            member.setPhoneNumber(paramMap.get("telephone"));
            member.setRegTime(new Date());
            member.setSex(paramMap.get("sex"));
            memberDao.add(member);
        }
       // 3. 判断是否重复预约 t_order 通过member_id,orderDate, setmeal_id
        Order order = new Order();
        order.setMemberId(member.getId());
        order.setOrderDate(orderDate);
        order.setSetmealId(Integer.parseInt(paramMap.get("id")));
        List<Order> orderList = orderDao.findByCondition(order);
        // 重复则报错
        if (null !=orderList && orderList.size()>0) {
            throw new HealthException("不能重复预约");
        }
       //         没重复
       // 可预约，添加订单
        orderDao.add(order);
       // 4. 更新已预约数量 t_ordersetting
        orderSettingDao.editReservationsByOrderDate(orderSetting);
        return order;
    }

    @Override
    public Map<String, Object> findOrderDetailById(int id) {
        return orderDao.findById4Detail(id);
    }
}
