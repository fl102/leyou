package com.leyou.Service;

import com.leyou.domain.SpecGroup;
import com.leyou.domain.SpecParam;

import java.util.List;

public interface SpecService {

    List<SpecGroup> findGroupsByCid(Long cid);

    List<SpecParam> findParams(Long gid, Long cid, Boolean generic, Boolean searching);

    void save(SpecParam specParam);

    void delete(Long pid);

    List<SpecGroup> findGroupAndParamsByCid(Long cid);
}
