package com.yt.health.service.imlp;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yt.health.constant.MessageConstant;
import com.yt.health.dao.CheckItemDao;
import com.yt.health.entity.PageResult;
import com.yt.health.entity.QueryPageBean;
import com.yt.health.exception.HealthException;
import com.yt.health.pojo.CheckItem;
import com.yt.health.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 包名：com.yt.health.service.imlp
 *
 * @author Yangtao
 * 日期：2021-06-15  01:43
 */

@Service(interfaceClass = CheckItemService.class)
public class CheckItemServiceImpl implements CheckItemService {
    @Autowired
    private CheckItemDao checkItemDao;
    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //判断是否存在查询条件
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) {
            //有查询条件进行百分号拼接
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        //Page<T> 是 PageHelper的内置对象
        Page<CheckItem> page = checkItemDao.findPage(queryPageBean.getQueryString());
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void deleteById(int id){
        //判断检查项是否有关联
        int count = checkItemDao.findCountByCheckItemId(id);
        if (count>0) {
            throw new HealthException("该检查项已经被使用了，不能删除!");
        }
        checkItemDao.deleteById(id);
    }

    @Override
    public CheckItem findById(int id) {
        return checkItemDao.findById(id);
    }

    @Override
    public void update(CheckItem checkItem) {
        checkItemDao.update(checkItem);
    }
}
