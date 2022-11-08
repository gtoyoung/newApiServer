package com.example.apiserver.utils;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.stereotype.Service;
import scala.collection.Seq;

import java.util.*;

public class WordAnalysis {

    /**
     * 문장에서 명사 추출
     * @param text
     * @return
     * @throws Exception
     */
    public static List<String> doWordNouns(String text) throws Exception {
        // Normalize (정규화)
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);

        // Tokenize (토큰화)
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);

//        List<String> tokenList = OpenKoreanTextProcessorJava.tokensToJavaStringList(tokens);

        List<KoreanTokenJava> tokenJavas = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);
//        System.out.println(tokenJavas.get(0).getPos() + " | " + tokenJavas.get(0).getStem());
        List<String> tokenList = tokenJavas.stream().map((token)->{
            if(token.getPos().name().equals("Noun")){
                return token.getText();
            }
            return null;
        }).distinct().filter((data)->{
            return data != "" && data != null && data.length() > 2;
        }).toList();

        return tokenList;
    }
}
