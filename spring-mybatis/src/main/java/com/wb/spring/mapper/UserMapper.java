package com.wb.spring.mapper;

import com.wb.spring.domain.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/5/10 22:31
 */
public interface UserMapper {

	@Select("SELECT * FROM user WHERE id=#{id}")
	User findUserById(@Param("id") Integer id);
}
