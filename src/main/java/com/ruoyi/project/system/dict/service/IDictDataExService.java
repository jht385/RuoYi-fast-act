package com.ruoyi.project.system.dict.service;

import java.util.List;
import java.util.Map;

public interface IDictDataExService {
	List<Map<String, Object>> echoDict(String str, String dictType);
}
