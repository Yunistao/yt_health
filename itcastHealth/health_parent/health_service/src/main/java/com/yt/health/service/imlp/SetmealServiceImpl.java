package com.yt.health.service.imlp;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yt.health.dao.SetmealDao;
import com.yt.health.entity.PageResult;
import com.yt.health.entity.QueryPageBean;
import com.yt.health.exception.HealthException;
import com.yt.health.pojo.CheckGroup;
import com.yt.health.pojo.Setmeal;
import com.yt.health.service.SetmealService;
import com.yt.health.utils.QiNiuUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包名：com.yt.health.service.imlp
 *
 * @author Yangtao
 * 日期：2021-06-24  23:05
 */
@Service(interfaceClass = SetmealService.class)
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;

    @Override
    public List<CheckGroup> findAll() {
        return  setmealDao.findAll();

    }

    @Transactional
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        //1.添加套餐
        setmealDao.add(setmeal);
        //2.添加套餐和检查组
        if (null != checkgroupIds) {
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.addSetmealCheckGroup(setmeal.getId(), checkgroupId);
            }
        }
        //新增套餐后需要重新生成静态页面
        List<Setmeal> allSetmeal = setmealDao.findAllSetmeal();
        // 拼接图片地址
        allSetmeal.forEach(s->{
            s.setImg(QiNiuUtils.DOMAIN + s.getImg());
        });
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());

        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) {
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }

        Page<Setmeal> setmealPage = setmealDao.findPage(queryPageBean.getQueryString());
        return new PageResult(setmealPage.getTotal(),setmealPage.getResult());
    }

    @Override
    public Setmeal findById(int id) {
        return setmealDao.findById(id);
    }

    @Override
    public Integer[] findCheckGroupsBySetmealId(int id) {
        return setmealDao.findCheckGroupsBySetmealId(id);
    }

    @Override
    @Transactional
    public void update(Integer[] checkGroupIds, Setmeal setmeal) {
        //1.删除旧关系
       setmealDao.deleteSetmealCheckGroup(setmeal.getId());
        //2.更新套餐
        setmealDao.update(setmeal);
        //3.添加新关系
        if (null != checkGroupIds) {
            for (Integer checkGroupId : checkGroupIds) {
                setmealDao.addSetmealCheckGroup(setmeal.getId(),checkGroupId);
            }
        }

    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        //1.判断该套餐是否被下过订单
        int count = setmealDao.findOrderCountBySetmealId(id);
        //2.如果使用过 ，抛出异常，无法删除
        if (count > 0) {
             throw new HealthException("该套餐被使用，无法删除");
        }
        //3.删除套餐和检查组的关系
        setmealDao.deleteSetmealCheckGroup(id);
        //4.删除套餐
        setmealDao.deleteById(id);

    }

    @Override
    public List<String> findAllImgs() {
        return setmealDao.findAllImgs();
    }

    /*
     * @description:获得所有套餐列表信息（select * from t_setmeal）
     * @author: yangtao
     * @date: 2021/7/4 16:58
     * @param:
     * @return: java.util.List<com.yt.health.pojo.Setmeal>
     */
    @Override
    public List<Setmeal> findAllSetmeal() {

        List<Setmeal> setmealList = setmealDao.findAllSetmeal();
        // 拼接图片的完整路径 $.each
        setmealList.forEach(setmeal -> {
            setmeal.setImg(QiNiuUtils.DOMAIN + setmeal.getImg());
        });

        //为了方便起见（不想启动health_web，后台管理，通过后台crud出发页面静态化，此处改为获取套餐列表时，对每个套餐详情进行页面静态化）
        generateSetmealList(setmealList);
        for (Setmeal setmeal : setmealList) {
            Setmeal setmealById = findSetmealById(setmeal.getId());
            setmealById.setImg(setmeal.getImg());
            generateSetmealDetailHtml(setmealById);
        }
        return setmealList;
    }

    @Override
    public Setmeal findSetmealById(Integer id) {
        Setmeal setmeal = null;
        if (null != id) {
            setmeal = setmealDao.findSetmealById(id);
        }else{
            throw new HealthException("id不能为空");
        }
        return setmeal;
    }

    @Override
    public Setmeal findDetailById2(int id) {
        return setmealDao.findDetailById2(id);
    }

    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }

    @Autowired
    private FreeMarkerConfigurer freemarkerConfig;

    @Value("${out_put_path}")
    private String out_put_path;

    /**
     * 生成套餐列表静态页面
     * @param setmealList
     */
    private void generateSetmealList(List<Setmeal> setmealList){
        Map<String,Object> dataMap = new HashMap<String,Object>();
        // key setmealList 与模板中的变量要一致
        dataMap.put("setmealList",setmealList);
        // 输出
        String setmealListFile = out_put_path + "/mobile_setmeal.html";

        generateHtml("mobile_setmeal.ftl", dataMap, setmealListFile);
    }

    /*
     * @description:通过套餐id 生成套餐详情页面的静态化html
     * @author: yangtao
     * @date: 2021/7/4 16:51
     * @param: setmealById
     * @return: void
     */
    private void generateSetmealDetailHtml(Setmeal setmealById) {
        String templateName = "mobile_setmeal_detail.ftl";
        String fileName = String.format("%s/mobile_setmeal_detail_%d.html",out_put_path,setmealById.getId());
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("setmeal",setmealById);
        generateHtml(templateName,dataMap,fileName);
    }

    /*
     * @description:生成静态页面
     * @author: yangtao
     * @date: 2021/7/4 14:14
     * @param: templateName
     * @param: dataMap
     * @param: filename
     * @return: void
     */
    private void generateHtml(String templateName, Map<String, Object> dataMap, String filename) {
        // 获取模板
        Configuration configuration = freemarkerConfig.getConfiguration();
        try {
            Template template = configuration.getTemplate(templateName);
            // utf-8 不能少了。少了就中文乱码
            BufferedWriter writer =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"utf-8"));
            // 填充数据到模板
            template.process(dataMap,writer);
            // 关闭流
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
