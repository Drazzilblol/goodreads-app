package com.training.dr.androidtraining.data.parsing;

import android.text.Html;
import android.util.Log;

import com.training.dr.androidtraining.data.models.Book;
import com.training.dr.androidtraining.data.models.Item;
import com.training.dr.androidtraining.data.models.User;
import com.training.dr.androidtraining.ulils.ApiUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


public final class XmlParsingManager implements ParsingManager {

    private static final String TAG = XmlParsingManager.class.getSimpleName();

    @Override
    public List<Item> parseUserBookList(String s) {
        XmlPullParserFactory factory;
        Book book = null;
        List<Integer> idList = null;
        List<String> images = null;
        List<Item> books = new ArrayList<>();
        try {
            factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(s));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("review")) {
                            book = new Book();
                            idList = new ArrayList<>();
                            images = new ArrayList<>();
                        } else if (tagname.equalsIgnoreCase("original_publication_year")) {
                            book.setYear(parser.nextText());
                        } else if (tagname.equalsIgnoreCase("average_rating")) {
                            book.setRating(Float.parseFloat(parser.nextText()));
                        } else if (tagname.equalsIgnoreCase("title")) {
                            book.setTitle(parser.nextText());
                        } else if (tagname.equalsIgnoreCase("name")) {
                            book.setAuthor(parser.nextText());
                        } else if (tagname.equalsIgnoreCase("image_url")) {
                            images.add(parser.nextText());
                        } else if (tagname.equalsIgnoreCase("id")) {
                            idList.add(Integer.parseInt(parser.nextText()));
                        } else if (tagname.equalsIgnoreCase("description")) {
                            book.setDescription(Html.fromHtml(parser.nextText()).toString());
                        } else if (tagname.equalsIgnoreCase("date_added")) {
                            book.setCreated(ApiUtils.parseDate(parser.nextText()));
                        } else if (tagname.equalsIgnoreCase("rating")) {
                            book.setMyRating(Integer.parseInt(parser.nextText()));
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("review")) {
                            book.setGoodreadId(idList.get(1));
                            book.setReviewId(idList.get(0));
                            book.setImageUrl(images.get(0));
                            books.add(book);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return books;
    }

    @Override
    public User parseCurrentUserInfo(String s) {
        XmlPullParserFactory factory;
        User user = new User();
        try {
            factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(s));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("user")) {
                            user.setGoodreadId(Integer.parseInt(parser.getAttributeValue(null, "id")));
                        } else if (tagname.equalsIgnoreCase("name")) {
                            user.setName(parser.nextText());
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return user;
    }

    @Override
    public User parseUserInfo(String s) {
        XmlPullParserFactory factory;
        User user = new User();
        List<String> names = new ArrayList<>();
        List<Integer> idList = new ArrayList<>();
        List<String> images = new ArrayList<>();
        try {
            factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(s));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("id")) {
                            idList.add(Integer.parseInt(parser.nextText()));
                        } else if (tagname.equalsIgnoreCase("name")) {
                            names.add(parser.nextText());
                        } else if (tagname.equalsIgnoreCase("image_url")) {
                            images.add(parser.nextText());
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("user_shelves")) {
                            user.setName(names.get(0));
                            user.setGoodreadId(idList.get(0));
                            user.setAvatarUrl(images.get(0));
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return user;
    }
}