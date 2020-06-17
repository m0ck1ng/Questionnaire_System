package com.questionnaire.demo.mapper;

import com.questionnaire.demo.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    @Select("SELECT * FROM User WHERE username=#{username}")
    User getByUsername(String username);

    @Select("SELECT id FROM User WHERE name=#{name}")
    String getByName(String name);

    @Select("SELECT id FROM User WHERE id=#{id} AND password=#{password}")
    String getByIdAndPassword(String id, String password);

    @Select("SELECT * FROM User WHERE username=#{username} AND password=#{password}")
    User getByUsernameAndPassword(String username, String password);

    @Select("SELECT * FROM User WHERE id=#{id}")
    User getById(String id);

    @Insert({"INSERT INTO User(username, password, id, name) VALUES(#{username}, #{password}, #{id}, #{name})"})
    void insert(User user);

    @Update("UPDATE User SET password=#{password} WHERE id=#{id}")
    void updatePassword(String password, String id);
}
