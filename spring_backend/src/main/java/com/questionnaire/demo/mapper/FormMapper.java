package com.questionnaire.demo.mapper;
import com.questionnaire.demo.model.formtemplate.Form;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface FormMapper {
    @Select("SELECT body FROM Ques WHERE qid=#{qid}")
    String getBodyByQID(String qid);

    @Select("SELECT due_time FROM Ques WHERE qid=#{qid}")
    String getDueByQID(String qid);

    @Select("SELECT * FROM Ques WHERE id=#{id}")
    List<Map<String, String>> getByID(String id);

    @Insert({"INSERT INTO Ques(qid, id,due_time,state, body) VALUES(#{uuid}, #{id},#{due_time}, #{state}, #{form_in_Json})"})
    void insert(String uuid, String id,String due_time, String state,String form_in_Json);
}
