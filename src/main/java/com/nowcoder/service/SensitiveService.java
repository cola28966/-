package com.nowcoder.service;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    /**
     * 默认敏感词替换符
     */
    private static final String DEFAULT_REPLACEMENT = "***";


    private class TrieNode {

        /**
         * true 关键词的终结 ； false 继续
         */
        private boolean end = false;

        /**
         * key下一个字符，value是对应的节点
         */
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        /**
         * 向指定位置添加节点树
         */
        void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        /**
         * 获取下个节点
         */
        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeywordEnd() {
            return end;
        }

        void setKeywordEnd(boolean end) {
            this.end = end;
        }

        public int getSubNodeCount() {
            return subNodes.size();
        }


    }


    /**
     * 根节点
     */
    private TrieNode rootNode = new TrieNode();


    /**
     * 判断是否是一个符号
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }


    /**
     * 过滤敏感词
     */
    public String filter(String text) {
        StringBuffer sb = new StringBuffer();
        int pos,cur;
        pos=cur=0;
        int len = text.length();
        TrieNode tempNode = rootNode;
        while (cur<len){
            Character c = text.charAt(pos);
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    sb.append(c);
                    cur++;
                }
                pos++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if(tempNode==null){
                sb.append(text.charAt(cur));
                tempNode = rootNode;
                cur++;
                pos=cur;
            }
            else if(tempNode.isKeywordEnd()){
                sb.append(DEFAULT_REPLACEMENT);
                tempNode=rootNode;
                pos++;
                cur=pos;
            }
            else {
                pos++;
            }
        }
        sb.append(text.substring(cur));
        return sb.toString();
    }

    private void addWord(String lineTxt) {
        TrieNode tempNode = rootNode;
        for(int i =0 ;i<lineTxt.length();++i){
            Character c = lineTxt.charAt(i);
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);
            if (node == null) {
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }
            tempNode = node;
            if (i == lineTxt.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line;
            while ( (line=br.readLine())!=null){
                addWord(line);
            }
            inputStreamReader.close();
        }catch (Exception e){
            logger.error("敏感词文件加载失败"+e.getMessage());
        }
    }

    public static void main(String[] argv) {
        SensitiveService s = new SensitiveService();
        s.addWord("色情");
        s.addWord("好色");
        String string = s.filter("你好X色**情XX");
        System.out.print(string);
    }
}
