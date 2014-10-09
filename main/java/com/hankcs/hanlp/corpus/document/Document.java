/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/9/8 19:01</create-date>
 *
 * <copyright file="Document.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.hankcs.hanlp.corpus.document;

import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.CompoundWord;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.corpus.document.sentence.word.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hankcs
 */
public class Document implements Serializable
{
    static Logger logger = LoggerFactory.getLogger(Document.class);
    public List<Sentence> sentenceList;

    public Document(List<Sentence> sentenceList)
    {
        this.sentenceList = sentenceList;
    }

    public static Document create(String param)
    {
        Pattern pattern = Pattern.compile(".+?((。/w)|(！/w )|(？/w )|\\n|$)");
        Matcher matcher = pattern.matcher(param);
        List<Sentence> sentenceList = new LinkedList<>();
        while (matcher.find())
        {
            String single = matcher.group();
            Sentence sentence = Sentence.create(single);
            if (sentence == null)
            {
                logger.warn("使用{}构建句子失败", single);
                return null;
            }
            sentenceList.add(sentence);
        }
        return new Document(sentenceList);
    }

    /**
     * 获取单词序列
     *
     * @return
     */
    public List<IWord> getWordList()
    {
        List<IWord> wordList = new LinkedList<>();
        for (Sentence sentence : sentenceList)
        {
            wordList.addAll(sentence.wordList);
        }
        return wordList;
    }

    public List<Word> getSimpleWordList()
    {
        List<IWord> wordList = getWordList();
        List<Word> simpleWordList = new LinkedList<>();
        for (IWord word : wordList)
        {
            if (word instanceof CompoundWord)
            {
                simpleWordList.addAll(((CompoundWord) word).innerList);
            }
            else
            {
                simpleWordList.add((Word) word);
            }
        }

        return simpleWordList;
    }

    /**
     * 获取简单的句子列表，其中复合词会被拆分为简单词
     * @return
     */
    public List<List<Word>> getSimpleSentenceList()
    {
        List<List<Word>> simpleList = new LinkedList<>();
        for (Sentence sentence : sentenceList)
        {
            List<Word> wordList = new LinkedList<>();
            for (IWord word : sentence.wordList)
            {
                if (word instanceof CompoundWord)
                {
                    for (Word inner : ((CompoundWord) word).innerList)
                    {
                        wordList.add(inner);
                    }
                }
                else
                {
                    wordList.add((Word) word);
                }
            }
            simpleList.add(wordList);
        }

        return simpleList;
    }

    /**
     * 获取简单的句子列表
     * @param spilt 如果为真，其中复合词会被拆分为简单词
     * @return
     */
    public List<List<Word>> getSimpleSentenceList(boolean spilt)
    {
        List<List<Word>> simpleList = new LinkedList<>();
        for (Sentence sentence : sentenceList)
        {
            List<Word> wordList = new LinkedList<>();
            for (IWord word : sentence.wordList)
            {
                if (word instanceof CompoundWord)
                {
                    if (spilt)
                    {
                        for (Word inner : ((CompoundWord) word).innerList)
                        {
                            wordList.add(inner);
                        }
                    }
                    else
                    {
                        wordList.add(((CompoundWord) word).toWord());
                    }
                }
                else
                {
                    wordList.add((Word) word);
                }
            }
            simpleList.add(wordList);
        }

        return simpleList;
    }

    /**
     * 获取简单的句子列表，其中复合词的标签如果是set中指定的话会被拆分为简单词
     * @param labelSet
     * @return
     */
    public List<List<Word>> getSimpleSentenceList(Set<String> labelSet)
    {
        List<List<Word>> simpleList = new LinkedList<>();
        for (Sentence sentence : sentenceList)
        {
            List<Word> wordList = new LinkedList<>();
            for (IWord word : sentence.wordList)
            {
                if (word instanceof CompoundWord)
                {
                    if (labelSet.contains(word.getLabel()))
                    {
                        for (Word inner : ((CompoundWord) word).innerList)
                        {
                            wordList.add(inner);
                        }
                    }
                    else
                    {
                        wordList.add(((CompoundWord) word).toWord());
                    }
                }
                else
                {
                    wordList.add((Word) word);
                }
            }
            simpleList.add(wordList);
        }

        return simpleList;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (Sentence sentence : sentenceList)
        {
            sb.append(sentence);
            sb.append(' ');
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}