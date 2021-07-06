package com.yt.health.dao;

import com.github.pagehelper.Page;
import com.yt.health.pojo.CheckGroup;
import com.yt.health.pojo.Setmeal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 包名：com.yt.health.dao
 *
 * @author Yangtao
 * 日期：2021-06-24  23:06
 */
public interface SetmealDao {
    /*
     * @description:查询所有的检查组
     * @author: yangtao
     * @date: 2021/6/24 23:17
     * @param:
     * @return: java.util.List<com.yt.health.pojo.CheckGroup>
     */
    List<CheckGroup> findAll();

    /*
     * @description:添加套餐
     * @author: yangtao
     * @date: 2021/6/24 23:28
     * @param: null
     * @return:
     */

    void add(Setmeal setmeal);


    void addSetmealCheckGroup(@Param("setmealId") Integer id, @Param("checkgroupId") Integer checkgroupId);

    Page<Setmeal> findPage(String queryString);

    Setmeal findById(int id);

    Integer[] findCheckGroupsBySetmealId(int id);

    /*
     * @description:根据套餐id删除检查组
     * @author: yangtao
     * @date: 2021/6/27 16:12
     * @param: id
     * @return: void
     */
    void deleteSetmealCheckGroup(Integer id);

    /*
     * @description:更新套餐
     * @author: yangtao
     * @date: 2021/6/27 16:13
     * @param: setmeal
     * @return: void
     */
    void update(Setmeal setmeal);

    /*
     * @description:根据id删除套餐
     * @author: yangtao
     * @date: 2021/6/27 16:49
     * @param: id
     * @return: void
     */
    void deleteById(Integer id);

    /*
     * @description:更加套餐id 查询关联订单的数量
     * @author: yangtao
     * @date: 2021/6/27 16:58
     * @param: id
     * @return: int
     */
    int findOrderCountBySetmealId(Integer id);

    /*
     * @description:查询所有的图片名称
     * @author: yangtao
     * @date: 2021/6/27 21:38
     * @param:
     * @return: java.util.List<java.lang.String>
     */
    List<String> findAllImgs();

    /*
     * @description:查询所有的套餐
     * @author: yangtao
     * @date: 2021/7/1 21:24
     * @param:
     * @return: java.util.List<com.yt.health.pojo.Setmeal>
     */
    List<Setmeal> findAllSetmeal();

    /*
     * @description:根据id查询套餐内容，包含多个检查组、每个检查组包含多个检查项
     * @author: yangtao
     * @date: 2021/7/1 22:06
     * @param: id
     * @return: com.yt.health.pojo.Setmeal
     */
    Setmeal findSetmealById(Integer id);

    /**
     * 查询套餐详情 方式二
     * @param id
     * @return
     */
    Setmeal findDetailById2(int id);
}
