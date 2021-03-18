package com.wb.spring.propertyeditor.ext;

import com.wb.spring.propertyeditor.domain.Teacher;

import java.beans.PropertyEditorSupport;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/10/20 16:53
 */
public class TeacherEditor extends PropertyEditorSupport {
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		String[] arr = text.split("_");
		Teacher teacher = new Teacher();
		teacher.setName(arr[0]);
		teacher.setAge(Integer.parseInt(arr[1]));
		teacher.setEmail(arr[2]);
		setValue(teacher);
	}
}
