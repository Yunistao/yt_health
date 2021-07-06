package com.yt.health.service.imlp;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yt.health.constant.MessageConstant;
import com.yt.health.dao.CheckGroupDao;
import com.yt.health.entity.PageResult;
import com.yt.health.entity.QueryPageBean;
import com.yt.health.exception.HealthException;
import com.yt.health.pojo.CheckGroup;
import com.yt.health.pojo.CheckItem;
import com.yt.health.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包名：com.yt.health.service.imlp
 *
 * @author Yangtao
 * 日期：2021-06-18  00:51
 */
@Service(interfaceClass = CheckGroupService.class)
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    private CheckGroupDao checkGroupDao;

    @Override
    public List<CheckItem> findAll() {
        return checkGroupDao.findAll();
    }

    @Override
    @Transactional
    public void add(CheckGroup checkGroup, Integer[] integers) {
        //新增检查组
        checkGroupDao.add(checkGroup);

        //一个检查组里包含多个检查项，按检查组id 逐个添加检查项
        if (null != integers) {
            for (Integer integer : integers) {
                Map map = new HashMap();
                map.put("gid",checkGroup.getId());
                map.put("id",integer);
                checkGroupDao.addCheckGroupCheckItem(map);
            }
        }
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) {
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        Page<CheckGroup> page = checkGroupDao.findPage(queryPageBean.getQueryString());
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public CheckGroup findById(int id) {
        return checkGroupDao.findById(id);
    }

    @Transactional
    @Override
    public void update(CheckGroup checkGroup, Integer[] checkitemIds) {
        //1.删除旧关系
        checkGroupDao.deleteCheckGroupCheckItem(checkGroup.getId());
        //2.更新检查组
        checkGroupDao.update(checkGroup);
        //3.更新和检查组关联的多条检查项信息
        if (null != checkitemIds) {
            for (Integer checkitemId : checkitemIds) {
                Map map = new HashMap();
                map.put("gid",checkGroup.getId());
                map.put("id",checkitemId);
                checkGroupDao.addCheckGroupCheckItem(map);
            }
        }
    }

    @Override
    public void delete(int id) {
        //标准答案;不能直接删除，需要判断当前检查组是否和检查项、套餐关联，如果已经和检查项、套餐进行了关联则不允许删除
        //1. 检查 这个检查组是否被套餐使用了
        int count = checkGroupDao.findSetmealCountByCheckGroupId(id);
        if(count > 0){
            // 被使用了
            throw new HealthException(MessageConstant.CHECKGROUP_IN_USE);
        }

        //2.如果存在，抛出异常，提示无法删除信息
        if (count > 0) {
            throw new HealthException("该检查组存在关联的检查项，无法删除！");
        }
        //3.删除检查组和检查项的关系
        checkGroupDao.deleteCheckGroupCheckItem(id);
        //4.删除检查组
        checkGroupDao.delete(id);
    }

    @Override
    public Integer[] findCheckItemsByCheckGroupId(int checkGroupId) {
        return  checkGroupDao.findCheckItemsByCheckGroupId(checkGroupId);
    }

}
