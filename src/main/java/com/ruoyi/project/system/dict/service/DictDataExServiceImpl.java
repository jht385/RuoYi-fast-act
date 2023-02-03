package com.ruoyi.project.system.dict.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.project.system.dict.domain.DictData;
import com.ruoyi.project.system.dict.mapper.DictDataMapper;

import cn.hutool.core.convert.Convert;

@Service
public class DictDataExServiceImpl implements IDictDataExService {
	@Autowired
	private DictDataMapper dictDataMapper;

	@Override
	public List<Map<String, Object>> echoDict(String str, String dictType) {
		List<DictData> dictDatas = dictDataMapper.selectDictDataByType(dictType);
		List<String> strList = Convert.toList(String.class, str.split(","));
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		dictDatas.stream().forEach(dictData -> {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("dictValue", dictData.getDictValue());
			map.put("dictLabel", dictData.getDictLabel());
			map.put("flag", strList.contains(dictData.getDictValue()));
			list.add(map);
		});
		return list;
	}
}
