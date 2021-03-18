package com.wb.spring.selfapplicationcontext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/20 20:03
 */
public class JsonTestService {
	private JsonTestBean jsonTestBean;

	public JsonTestService() {}

	public JsonTestService(JsonTestBean jsonTestBean) {
		this.jsonTestBean = jsonTestBean;
	}

	public JsonTestBean getJsonTestBean() {
		return jsonTestBean;
	}

	public void setJsonTestBean(JsonTestBean jsonTestBean) {
		this.jsonTestBean = jsonTestBean;
	}

	@Override
	public String toString() {
		return "JsonTestService{" +
				"jsonTestBean=" + jsonTestBean +
				'}';
	}
}
