package com.leyou.Service.impl;

import com.leyou.Dao.SpecGroupDao;
import com.leyou.Dao.SpecParamsDao;
import com.leyou.Service.SpecService;
import com.leyou.domain.SpecGroup;
import com.leyou.domain.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpecServiceImpl implements SpecService {
    @Autowired
    private SpecGroupDao specGroupDao;

    @Autowired
    private SpecParamsDao specParamsDao;

    @Override
    public List<SpecGroup> findGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.specGroupDao.select(specGroup);
    }

    @Override
    public List<SpecParam> findParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        return this.specParamsDao.select(specParam);
    }

    @Override
    @Transactional
    public void save(SpecParam specParam) {
        specParamsDao.insertSelective(specParam);
    }

    @Override
    @Transactional
    public void delete(Long pid) {
        specParamsDao.deleteByPrimaryKey(pid);
    }

    @Override
    public List<SpecGroup> findGroupAndParamsByCid(Long cid) {
        List<SpecGroup> groups = this.findGroupsByCid(cid);
        groups.forEach(group -> {
            List<SpecParam> params = this.findParams(group.getId(), null, null, null);
            group.setParams(params);
        });
        return groups;
    }
}
