package com.xianggole.sellergoods.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xianggole.mapper.TbItemCatMapper;
import com.xianggole.pojo.TbItemCat;
import com.xianggole.pojo.TbItemCatExample;
import com.xianggole.pojo.TbItemCatExample.Criteria;
import com.xianggole.sellergoods.service.ItemCatService;

import entity.PageResult;

/**
 * 服务实现层
 * @company 恩施迅博科技
 * @author Administrator
 *
 */
@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 * @throws Exception 
	 */
	@Override
	public void delete(Long[] ids) throws Exception {
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		for(Long id:ids){
			criteria.andParentIdEqualTo(id);
			List<TbItemCat> selectByExample = itemCatMapper.selectByExample(example);
			if(selectByExample.size()!=0) {
				throw new Exception("不能删除有子结点的数据");
			}else {
			 itemCatMapper.deleteByPrimaryKey(id);
			}
		}		
	}
	
	
		@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
						if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}
	
		}
		
		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Autowired
		private RedisTemplate redisTemplate ;
		@Override
		public List<TbItemCat> findByParentId(Long parentId) {
			TbItemCatExample example=new TbItemCatExample();
			Criteria criteria = example.createCriteria();
			criteria.andParentIdEqualTo(parentId);
			
			
			
			List<TbItemCat> itemCatList = findAll();
			for(TbItemCat itemCat:itemCatList) {
				//将模板放入缓存
				redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
			}
			System.out.println("将模板id放入缓存");
			return itemCatMapper.selectByExample(example);
		}
	
}
