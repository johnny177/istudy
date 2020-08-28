package com.nnoboa.istudy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.jakewharton.threetenabp.AndroidThreeTen;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.blog.blogUtils.BlogItem;

import java.util.ArrayList;
import java.util.Random;

public class BlogAdapter extends ArrayAdapter<BlogItem> {
    public BlogAdapter(@NonNull Context context, ArrayList<BlogItem> blogItems) {
        super(context, 0, blogItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View blogView = convertView;

        AndroidThreeTen.init(getContext());

        if (blogView == null) {
            blogView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.blog, parent, false);
        }

        BlogItem blogItems = getItem(position);

        TextView authCred = blogView.findViewById(R.id.auth_name);
        TextView author = blogView.findViewById(R.id.author);
        TextView title = blogView.findViewById(R.id.title);
        TextView time = blogView.findViewById(R.id.time);

        authCred.setText(blogItems.getmAuthor().substring(0, 1));
        author.setText(blogItems.getmAuthor());
        title.setText(blogItems.getmTitle());
        time.setText(formatString(blogItems.getmDatePublished()));

        GradientDrawable nameCircle = (GradientDrawable) authCred.getBackground();

        int nameColor = getRandomColor();

        nameCircle.setColor(nameColor);

        return blogView;
    }

    private int getRandomColor() {
        Random random = new Random();
        return Color.argb(180, random.nextInt(200), random.nextInt(210), random.nextInt(250));
    }

    private String formatString(String time) {
        int last = time.indexOf("T");
        time = time.substring(0, last);
        return time;
    }

//    public long Millis(String time){
//        long milli;
//        try{
//
//
////        DateTime = DateTime.replace(" ","T").replace("/","-");
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);
//
//            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
//            OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();
//
//            milli = offsetDateTime.toInstant().toEpochMilli();
//            Log.i("Millis"," "+milli);}
//        catch (org.threeten.bp.format.DateTimeParseException e ){
////            String DateTime = date+" "+time+":00";
////
////
//////            DateTime = DateTime.replace(" ","T").replace("/","-");
////            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
////            LocalDateTime localDateTime = LocalDateTime.parse(DateTime, dateTimeFormatter);
////
////            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
////            OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();
////
////            milli = offsetDateTime.toInstant().toEpochMilli();
////            Log.i("Millis"," "+milli);
//        Log.d("TimeTo Milli :","failed",e);
//        }
//        return milli;
//    }
}
