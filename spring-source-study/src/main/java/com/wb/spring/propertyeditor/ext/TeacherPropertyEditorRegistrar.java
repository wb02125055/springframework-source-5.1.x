package com.wb.spring.propertyeditor.ext;

import com.wb.spring.propertyeditor.domain.Teacher;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/10/20 16:55
 */
public class TeacherPropertyEditorRegistrar implements PropertyEditorRegistrar {
	@Override
	public void registerCustomEditors(PropertyEditorRegistry registry) {
		registry.registerCustomEditor(Teacher.class, new TeacherEditor());
	}
}
