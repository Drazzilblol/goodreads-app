package com.training.dr.androidtraining.data.parsing;

import com.training.dr.androidtraining.data.models.Item;
import com.training.dr.androidtraining.data.models.User;

import java.util.List;


interface ParsingManager {
    List<Item> parseUserBookList(String s);

    User parseCurrentUserInfo(String s);

    User parseUserInfo(String s);
}
