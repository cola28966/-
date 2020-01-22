package com.nowcoder.controller;

import com.nowcoder.model.Question;
import com.nowcoder.service.QuestionService;
import com.nowcoder.utils.HostHolder;
import com.nowcoder.utils.WendaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/question")
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,@RequestParam("content") String content){
        try {
            Question question = new Question();
            if(hostHolder.getUser()==null){
                return WendaUtils.getJSONString(999);
            }else{
                question.setUserId(hostHolder.getUser().getId());
            }
            System.out.println(title+content);
            question.setContent(content);
            question.setTitle(title);
            question.setCreatedDate(new Date());
            if(questionService.addQuestion(question)>0){
                return WendaUtils.getJSONString(0);
            }

        }catch (Exception e){
            logger.error("增加题目失败"+e.getMessage());
        }
        return WendaUtils.getJSONString(1,"增加失败");
    }
}
