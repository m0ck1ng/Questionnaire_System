package com.questionnaire.demo.mapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface FormMapper {
    @Select("SELECT body FROM Ques WHERE qid=#{qid}")
    String getBodyByQID(String qid);

    @Select("SELECT due_time FROM Ques WHERE qid=#{qid}")
    String getDuetimeByQID(String qid);

    @Select("SELECT 1 FROM Ques WHERE id=#{id} and qid=#{qid} LIMIT 1")
    Boolean isQuesExist(String id, String qid);

    @Select("SELECT state FROM Ques WHERE qid=#{qid}")
    String getStateByQID(String qid);

    @Select("SELECT * FROM Ques WHERE id=#{id}")
    List<Map<String, Object>> getFormsByID(String id);

    @Insert({"INSERT INTO Ques(qid, id,due_time,state, body) VALUES(#{uuid}, #{id},#{due_time}, #{state}, #{form_in_Json})"})
    void insert(String uuid, String id,String due_time, String state,String form_in_Json);

    @Delete("DELETE FROM Ques WHERE qid=#{qid} and id=#{id}")
    void deleteForm(String qid, String id);

    @Update("UPDATE Ques SET state=#{state} WHERE qid=#{qid} and id=#{id}")
    void updateState(String qid, String state, String id);
}
