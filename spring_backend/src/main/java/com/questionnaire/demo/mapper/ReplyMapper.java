package com.questionnaire.demo.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ReplyMapper {
    @Insert("INSERT INTO Results(rid, qid, ip, body, submit_time) VALUES(#{rid},#{qid},#{ip},#{body},#{submit_time})")
    void insert(String rid, String qid, String ip, String body, String submit_time);

    @Select("SELECT count(*) FROM Results WHERE ip=#{ip} and qid=#{qid}")
    int countByAll(String ip, String qid);

    @Select("SELECT count(*) FROM Results WHERE ip=#{ip} and qid=#{qid} and to_days(submit_time)=to_days(#{submit_time})")
    int countByDay(String ip, String qid, String submit_time);

    @Delete("DELETE FROM Results WHERE qid=#{qid}")
    void deleteByQid(String qid);

    @Select("SELECT ip, body, submit_time FROM Results WHERE qid=#{qid}")
    ArrayList<HashMap<String, Object>> getInfoByQid(String qid);
}
