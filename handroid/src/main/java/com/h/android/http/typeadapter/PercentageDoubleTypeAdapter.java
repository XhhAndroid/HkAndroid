package com.h.android.http.typeadapter;

import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * @Description: 将百分数的字符串转换成小数
 * @Author: XGod  xuanyouwu@163.com  17611639080  https://github.com/NBXXF     https://blog.csdn.net/axuanqq
 * @CreateDate: 2020/10/19 17:33
 */
public class PercentageDoubleTypeAdapter extends TypeAdapter<Double> {
    @Override
    public void write(JsonWriter jsonWriter, Double s) throws IOException {
        if (null == s) {
            jsonWriter.value(0);
        } else {
            jsonWriter.value(s);
        }
    }

    @Override
    public Double read(JsonReader jsonReader) throws IOException {
        JsonToken peek = jsonReader.peek();
        jsonReader.setLenient(true);
        switch (peek) {
            case STRING:
                String s = jsonReader.nextString();
                if (!TextUtils.isEmpty(s) && s.contains("%")) {
                    try {
                        return NumberFormat.getPercentInstance().parse(s).doubleValue();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0.0;
                    }
                }
                return Double.parseDouble(s);
            case NULL:
                try {
                    jsonReader.nextNull();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return 0.0;
            case NUMBER:
                return jsonReader.nextDouble();
            default:
                throw new JsonParseException("Expected number but was " + peek);
        }
    }
}

